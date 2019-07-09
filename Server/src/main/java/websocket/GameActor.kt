package websocket

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlin.system.measureTimeMillis
import Engine
import Entity

sealed class Request
class LoginRequest(val response: CompletableDeferred<Int>) : Request()
class LogoutRequest(val id: Int) : Request()
class TickRequest: Request()
class GetStateRequest(val response: CompletableDeferred<List<Entity>>): Request()

class GameActor(val eng: Engine) {
    private val requestChannel = GlobalScope.actor<Request> {
        for (request in channel){
            processRequest(request)
        }
    }

    fun login() = GlobalScope.async{
        val resp = CompletableDeferred<Int>()
        requestChannel.send(LoginRequest(resp))
        resp.await()
    }

    fun logout(id: Int) = GlobalScope.async{
        requestChannel.send(LogoutRequest(id))
    }

    fun tick() = GlobalScope.async {
        requestChannel.send(TickRequest())
    }

    fun getState() = GlobalScope.async {
        val response = CompletableDeferred<List<Entity>>()
        requestChannel.send(GetStateRequest(response))
        response.await()
    }

    private fun processRequest(r: Request){
        when (r) {
            is LoginRequest -> r.response.complete(eng.addNewPlayer())
            is LogoutRequest -> eng.removePlayer(r.id)
            is TickRequest -> eng.update()
            is GetStateRequest -> r.response.complete(eng.getState())
        }
    }
}