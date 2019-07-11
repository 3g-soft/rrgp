package websocket

import engine.DataTransferEntity
import io.javalin.websocket.WsContext
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject

class StateManger {
    private var prevState: Map<Int, DataTransferEntity> = mapOf()

    private fun convertState(newState: Map<Int, DataTransferEntity>): JSONObject {
        val state = JSONObject()
        for (key in newState.keys) {
            val newData = JSONObject(newState[key])
            if (prevState.containsKey(key)) {
                newData.remove("id")
                newData.remove("sizex")
                newData.remove("sizey")
                newData.remove("type")
                if (prevState[key]?.hp == newState[key]?.hp) newData.remove("hp")
            }
            state.put("$key", newData)
        }
        prevState = newState
        return state
    }

    fun sendFullState(ctx: WsContext, state: Map<Int, DataTransferEntity>) {
        ctx.send(JSONObject().put("name", "gd").put("response", state).toString())
    }

    fun sendToAll(clients: MutableSet<WsContext>, newState: Map<Int, DataTransferEntity>) = GlobalScope.launch {
        val state = convertState(newState)
        val bs = JSONObject().put("name", "gd").put("response", state).toString()
        for (c in clients) {
            c.send(bs)
        }
    }
}