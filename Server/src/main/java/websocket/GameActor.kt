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
class SetNicknameRequest(val id: Int, val string: String) : Request()
class AddSkillRequest(val playerid: Int, val id: Int): Request()

@ObsoleteCoroutinesApi
class GameActor(private val gameAPI: GameAPI) {

    private val requestChannel = GlobalScope.actor<Request> {
        for (request in channel) {
            when (request) {
                is LoginRequest -> request.response.complete(gameAPI.createPlayer().id)
                is LogoutRequest -> gameAPI.removeEntity(request.id)
                is TickRequest -> gameAPI.update()
                is GetStateRequest -> request.response.complete(gameAPI.getAllEntities())
                is ShotRequest -> gameAPI.makeShot(request.id, request.type)
                is ChangeAngleRequest -> gameAPI.setPlayerAngle(request.angle, request.id)
                is AccelerateRequest -> gameAPI.accelerate(request.id, request.isForward)
                is SetNicknameRequest -> gameAPI.setName(request.id, request.string)
                is AddSkillRequest -> gameAPI.addSkill(request.playerid, request.id)
            }
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

    fun setNickname(id: Int, string: String) = GlobalScope.launch {
        requestChannel.send(SetNicknameRequest(id, string))
    }

    fun addSkillAsync(playerid: Int, id: Int) = GlobalScope.launch{
        requestChannel.send(AddSkillRequest(playerid, id))
    }
}
