package websocket

import engine.DataTransferEntity
import engine.GameAPI
import io.javalin.Javalin
import io.javalin.http.staticfiles.Location
import io.javalin.websocket.WsContext
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject

class Server(val gapi: GameAPI, val tick: Long = 16) {

    private val l = Logger("SRV")
    private val gameActor = GameActor(gapi)

    private val clients = mutableMapOf<WsContext, Int>()
    private var prevState = HashMap<Int, DataTransferEntity>()
    private val stateManger = StateManger()

    init {
        Javalin.create {
            it.addStaticFiles("../front/", Location.EXTERNAL)
        }.apply {
            ws("/game") { ws ->
                ws.onConnect {
                    l.log("sc")
                    var id: Int = -1
                    runBlocking { id = gameActor.login().await() }
                    l.log("sending $id")
                    it.send(id)
                    clients[it] = id
                    GlobalScope.launch { stateManger.sendFullState(it, gameActor.getState().await()) }

                }
                ws.onMessage {
                    GlobalScope.launch(Dispatchers.Default) {
                        val obj = JSONObject(it.message())
                        parseRequest(clients[it]!!, obj)
                    }
                }
                ws.onClose {
                    gameActor.logout(clients[it]!!)
                    clients.remove(it)
                }
            }
        }.start(8080)

        GlobalScope.launch(Dispatchers.Default) {
            while (true) {
                delay(tick)
                gameActor.tick().await()
                stateManger.sendToAll(clients.keys, gameActor.getState().await())
            }
        }
        l.log("Started!")
    }

    fun parseRequest(id: Int, obj: JSONObject) {
        val name = obj.getString("op")
        val args = obj.getJSONArray("args")
        when (name) {
            "makeShot" -> gameActor.shot(id, args.getInt(0))
            "changeAngle" -> gameActor.changeAngle(id, args.getFloat(0))
            "accelerate" -> gameActor.accelerate(id, args.getBoolean(0))
        }
    }
}