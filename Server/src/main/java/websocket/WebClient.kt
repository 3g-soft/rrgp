package websocket

import io.javalin.websocket.WsContext
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.runBlocking

class WebClient(val ctx: WsContext, val srv: Server) {
    var id = -1
    val channel: SendChannel<RequestData>
    lateinit var log: Logger
//    val requests = mutableMapOf<Int, CompletableDeferred<Any>>()
    init {
        fun CoroutineScope.webClientActor() = actor<RequestData>{
            id = srv.login().await()
            log = Logger("CL#$id")
            log.log("Ama Alive")
            ctx.send(id)
            for(msg in channel){
                val response = srv.createRequest(msg, id)
                response.invokeOnCompletion {
                    val r = response.getCompleted()
                    ctx.send("{\"rid\": ${msg.rid}, \"response\": $r}")
                }
            }
        }
        channel = GlobalScope.webClientActor()
    }

    fun stop(){
        channel.close()
    }

    fun recieve(r: RequestData){
        runBlocking { channel.send(r) }
    }
}