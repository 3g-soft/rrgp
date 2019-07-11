package websocket

import engine.DataTransferEntity
import engine.GameAPI
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.actor

sealed class Request
class LoginRequest(val response: CompletableDeferred<Int>) : Request()
class LogoutRequest(val id: Int) : Request()
object TickRequest : Request()
class GetStateRequest(val response: CompletableDeferred<List<DataTransferEntity>>) : Request()
class ShotRequest(val id: Int, val type: Int) : Request()
class ChangeAngleRequest(val id: Int, val angle: Float) : Request()
class AccelerateRequest(val id: Int, val isForward: Boolean) : Request()

@ObsoleteCoroutinesApi
class GameActor(private val gameAPI: GameAPI) {

    private val requestChannel = GlobalScope.actor<Request> {
        for (request in channel) {
            processRequest(request)
        }
    }

    fun loginAsync() = GlobalScope.async {
        val resp = CompletableDeferred<Int>()
        requestChannel.send(LoginRequest(resp))
        resp.await()
    }

    fun logoutAsync(id: Int) = GlobalScope.launch {
        requestChannel.send(LogoutRequest(id))
    }

    fun tickAsync() = GlobalScope.async {
        requestChannel.send(TickRequest)
    }

    fun getStateAsync() = GlobalScope.async {
        val response = CompletableDeferred<List<DataTransferEntity>>()
        requestChannel.send(GetStateRequest(response))

        val stateMap = HashMap<Int, DataTransferEntity>()
        response.await().forEach {
            stateMap[it.id] = it
        }
        stateMap
    }

    fun shotAsync(id: Int, type: Int) = GlobalScope.launch {
        requestChannel.send(ShotRequest(id, type))
    }

    fun changeAngleAsync(id: Int, angle: Float) = GlobalScope.launch {
        requestChannel.send(ChangeAngleRequest(id, angle))
    }

    fun accelerateAsync(id: Int, isForward: Boolean) = GlobalScope.launch {
        requestChannel.send(AccelerateRequest(id, isForward))
    }

    private fun processRequest(r: Request) {
        when (r) {
            is LoginRequest -> r.response.complete(gameAPI.createPlayer().id)
            is LogoutRequest -> gameAPI.removeEntity(r.id)
            is TickRequest -> gameAPI.update()
            is GetStateRequest -> r.response.complete(gameAPI.getAllEntities())
            is ShotRequest -> gameAPI.makeShot(r.id, r.type)
            is ChangeAngleRequest -> gameAPI.setPlayerAngle(r.angle, r.id)
            is AccelerateRequest -> gameAPI.accelerate(r.id, r.isForward)
        }
    }
}