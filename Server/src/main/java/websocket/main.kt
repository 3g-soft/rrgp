package websocket

import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) = runBlocking{
    LogManager.enable()
    val srv = Server(Engine());
//    srv.join()
}