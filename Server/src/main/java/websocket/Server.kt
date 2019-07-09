package websocket


import io.javalin.Javalin
import io.javalin.websocket.WsContext
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlin.reflect.KFunction
import kotlin.reflect.full.*
import kotlin.system.measureTimeMillis
import Engine
import org.json.JSONObject
import org.json.*

data class RequestData(val op: String, val args: Array<String>, val rid: Int = -1)
data class Operation(val c: KFunction<*>, val needId: Boolean)

sealed class Request
class CommonRequest(val id: Int, val data: RequestData, val response: CompletableDeferred<Any?>) : Request()
class LoginRequest(val response: CompletableDeferred<Int>) : Request()
class LogoutRequest(val id: Int) : Request()

@Target(AnnotationTarget.FUNCTION)
annotation class Requestable(val needId: Boolean = false)


class Server(val eng: Engine, val tick: Long = 100) {
    class InvalidRequestParameterTypeException(str: String? = null) : Throwable(str)

    val l = Logger("SRV")
    val requests = mutableMapOf<String, Operation>()

    val clients = mutableMapOf<WsContext, WebClient>()

    private val requestChannel: SendChannel<Request>
    private lateinit var job: Job

    init {
        l.log("Starting...")
        val fs = eng::class.declaredFunctions.filter { f ->
            f.findAnnotation<Requestable>() != null
        }



        for (f in fs) {
            for (i in 1 until f.parameters.size) {
                val p = f.parameters[i]
                if (p.type.toString() != "kotlin.String" && !(f.findAnnotation<Requestable>()!!.needId && i == 1 && p.type.toString() == "kotlin.Int")) {
                    throw InvalidRequestParameterTypeException(f.name)
                }
            }
            requests[f.name] = Operation(f, f.findAnnotation<Requestable>()!!.needId)
            l.log("Registered request ${f.name}()")
        }

        requestChannel = GlobalScope.actor<Request> {
            job = coroutineContext[Job]!!
            while (isActive) {
                delay(tick)
                var requestsCount = 0
                val time = measureTimeMillis {
                    eng.update()
                    while (!channel.isEmpty) {
                        processRequest(channel.receiveOrNull()!!)
                        requestsCount++
                    }
                    broadcastState()

                }
            }
        }

        Javalin.create {}.apply {
            ws("/game") { ws ->
                ws.onConnect {
                    val nc = WebClient(it, this@Server)
                    clients[it] = nc
                }
                ws.onMessage {
                    GlobalScope.launch(Dispatchers.Default) {
                        val obj = JSONObject(it.message())
                        TODO("must receive request")
//                        clients[it]?.receive(RequestData(obj.getString("op")))
                    }
                }
                ws.onClose {
                    runBlocking { requestChannel.send(LogoutRequest(clients[it]!!.id)) }
                    clients.remove(it)
                }
            }
        }.start(8080)

        l.log("Started!")
    }

    suspend fun createRequest(rd: RequestData, id: Int): CompletableDeferred<Any?> {
        val response = CompletableDeferred<Any?>()
        requestChannel.send(CommonRequest(id, rd, response))
        return response
    }

    fun processRequest(r: Request) {
        when (r) {
            is CommonRequest -> {
                val op = requests[r.data.op]!!
                val result: Any?
                if (op.needId) {
                    result = op.c.call(eng, r.id, *r.data.args)
                } else {
                    result = op.c.call(eng, *r.data.args)
                }
                TODO("Do we really need response?")
//                r.response.complete(gson.toJson(result))
            }
            is LoginRequest -> r.response.complete(eng.addNewPlayer())
            is LogoutRequest -> eng.removePlayer(r.id)
        }
    }

    suspend fun login(): CompletableDeferred<Int> {
        val resp = CompletableDeferred<Int>()
        requestChannel.send(LoginRequest(resp))
        return resp
    }

    fun broadcastState(){
        val bs = JSONObject().put("name", "gd").put("response", eng.getState()).toString()
        for(c in clients.values){
            GlobalScope.launch(Dispatchers.Default) { c.ctx.send(bs)}
        }
    }
}