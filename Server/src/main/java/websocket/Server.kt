package websocket

import com.google.gson.GsonBuilder

import io.javalin.Javalin
import io.javalin.websocket.WsContext
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlin.reflect.KFunction
import kotlin.reflect.full.*

data class RequestData(val op: String, val args: Array<String>, val rid: Int = -1)
data class Operation(val c: KFunction<*>, val needId: Boolean)

sealed class Request
class CommonRequest(val id: Int, val data: RequestData, val response: CompletableDeferred<Any?>) : Request()
class LoginRequest(val response: CompletableDeferred<Int>) : Request()
class LogoutRequest(val id: Int) : Request()

@Target(AnnotationTarget.FUNCTION)
annotation class Requestable(val needId: Boolean = false)

class Server(val eng: EngineInterface, val tick: Long = 100) {
    class InvalidRequestParameterTypeException(str: String? = null) : Throwable(str)

    val l = Logger("SRV")
    val requests = mutableMapOf<String, Operation>()
    val gson = GsonBuilder().setPrettyPrinting().create()

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

        fun CoroutineScope.serverActor() = actor<Request> {
            job = coroutineContext[Job]!!
            eng.setup()
            while (isActive) {
                delay(tick)
                eng.doTick()
                while (!channel.isEmpty) {
                    processRequest(channel.receiveOrNull()!!)
                }
            }
        }

        requestChannel = GlobalScope.serverActor()

        Javalin.create {}.apply {
            ws("/game") { ws ->
                ws.onConnect {
                    val nc = WebClient(it, this@Server)
                    clients[it] = nc
                }
                ws.onMessage {
                    clients[it]?.recieve(gson.fromJson(it.message(), RequestData::class.java))
                }
                ws.onClose {
                    runBlocking { requestChannel.send(LogoutRequest(clients[it]!!.id)) }
                    clients[it]?.stop()
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
                r.response.complete(gson.toJson(result))
            }
            is LoginRequest -> r.response.complete(eng.addPlayer())
            is LogoutRequest -> eng.removePlayer(r.id)
        }
    }

    suspend fun login(): CompletableDeferred<Int> {
        val resp = CompletableDeferred<Int>()
        requestChannel.send(LoginRequest(resp))
        return resp
    }

    suspend fun join() {
        job.join()
    }
}