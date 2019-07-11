package websocket

import engine.GameAPI
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.runBlocking

@ObsoleteCoroutinesApi
fun main() {
    runBlocking {
        LogManager.enable()
        Server(GameAPI())
    }
}