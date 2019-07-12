package websocket

import engine.GameAPI
import io.javalin.Javalin
import io.javalin.http.staticfiles.Location
import io.javalin.websocket.WsContext
import kotlinx.coroutines.*
import org.json.JSONObject

@ObsoleteCoroutinesApi
class Server(gameAPI: GameAPI, private val tick: Long = 16) {

    private val l = Logger("SRV")
    private val gameActor = GameActor(gameAPI)

    private val clients = mutableMapOf<WsContext, Int>()
    private val stateManger = StateManger()

    init {
        val port = System.getenv()["PORT"] ?: "8080"
        Javalin.create {
            it.addStaticFiles("../Client/", Location.EXTERNAL)
        }.apply {
            ws("/game") { ws ->
                ws.onConnect {
                    l.log("sc")
                    var id: Int = -1
                    runBlocking { id = gameActor.loginAsync().await() }
                    l.log("sending $id")
                    it.send(id)
                    clients[it] = id
                    GlobalScope.launch { stateManger.sendFullState(it, gameActor.getStateAsync().await()) }

                }
                ws.onMessage {
                    GlobalScope.launch(Dispatchers.Default) {
                        val obj = JSONObject(it.message())
                        parseRequest(clients[it]!!, obj)
                    }
                }
                ws.onClose {
                    gameActor.logoutAsync(clients[it]!!)
                    clients.remove(it)
                }
            }
        }.start(port.toInt())

        GlobalScope.launch(Dispatchers.Default) {
            while (true) {
                delay(tick)
                gameActor.tickAsync().await()
                stateManger.sendToAll(clients.keys, gameActor.getStateAsync().await())
            }
        }
        l.log("Started!")
    }

    private fun parseRequest(id: Int, obj: JSONObject) {
        val name = obj.getString("op")
        val args = obj.getJSONArray("args")
        when (name) {
            "makeShot" -> gameActor.shotAsync(id, args.getInt(0))
            "changeAngle" -> gameActor.changeAngleAsync(id, args.getFloat(0))
            "accelerate" -> gameActor.accelerateAsync(id, args.getBoolean(0))
            "setNickname" -> gameActor.setNickname(id, args.getString(0))
        }
    }
}
