package websocket

import io.javalin.websocket.WsContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking

@ExperimentalCoroutinesApi
class WebClient(val ctx: WsContext, private val srv: Server) {
    var id = -1
    private val log: Logger

    init {
        runBlocking { id = srv.login().await()}
        log = Logger("CL#$id")
        log.log("Ama Alive")
        ctx.send(id)
    }

    suspend fun receive(msg: RequestData){
        val response = srv.createRequest(msg, id)
        response.invokeOnCompletion {
            val r = response.getCompleted()
            ctx.send("{\"rid\": ${msg.rid}, \"response\": $r}")
        }
    }
}