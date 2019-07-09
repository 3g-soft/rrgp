package websocket

import io.javalin.Javalin
import io.javalin.websocket.WsContext
import kotlinx.coroutines.*
import Engine
import org.json.JSONObject

class Server(val eng: Engine, val tick: Long = 100) {

    private val l = Logger("SRV")
    private val gameActor = GameActor(eng)

    private val clients = mutableMapOf<WsContext, Int>()

    init {
        Javalin.create {}.apply {
            ws("/game") { ws ->
                ws.onConnect {
                    l.log("sc")
                    var id: Int = -1
                    runBlocking { id = gameActor.login().await() }
                    it.send(id)
                    clients[it] = id
                }
                ws.onMessage {
                    GlobalScope.launch(Dispatchers.Default) {
                        val obj = JSONObject(it.message())
                        TODO("must receive request")
//                        clients[it]?.receive(RequestData(obj.getString("op")))
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
                val bs = JSONObject().put("name", "gd").put("response", gameActor.getState().await()).toString()
                for (c in clients.keys) {
//                    l.log(bs)
                    GlobalScope.launch(Dispatchers.Default) { c.send(bs) }
                }
            }
        }
        l.log("Started!")
    }
}