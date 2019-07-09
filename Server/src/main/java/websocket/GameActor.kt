package websocket

import engine.DataTransferEntity
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.actor
import engine.Entity
import engine.GameAPI

sealed class Request
class LoginRequest(val response: CompletableDeferred<Int>) : Request()
class LogoutRequest(val id: Int) : Request()
class TickRequest: Request()
class GetStateRequest(val response: CompletableDeferred<List<DataTransferEntity>>): Request()
class ShotRequest(val id: Int, val type: Int): Request()
class ChangeAngleRequest(val id: Int, val angle: Float): Request()

class GameActor(val gapi: GameAPI) {
    private val l = Logger("GA")

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
        val response = CompletableDeferred<List<DataTransferEntity>>()
        requestChannel.send(GetStateRequest(response))
        response.await()
    }

    fun shot(id: Int, type: Int) = GlobalScope.async {
        requestChannel.send(ShotRequest(id, type))
    }

    fun changeAngle(id: Int, angle: Float) = GlobalScope.async {
        requestChannel.send(ChangeAngleRequest(id, angle))
    }

    private fun processRequest(r: Request){
        when (r) {
            is LoginRequest -> r.response.complete(gapi.createPlayer().id)
            is LogoutRequest -> gapi.removePlayer(r.id)
            is TickRequest -> gapi.update()
            is GetStateRequest -> r.response.complete(gapi.getAllEntities())
            is ShotRequest -> l.log("Shot: not implemented")
            is ChangeAngleRequest -> gapi.setPlayerAngle(r.angle, r.id)
        }
    }
}