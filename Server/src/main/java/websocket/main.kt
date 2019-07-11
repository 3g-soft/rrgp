package websocket

import engine.Engine
import engine.GameAPI
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) = runBlocking {
    LogManager.enable()
    val srv = Server(GameAPI())
//    srv.join()
}