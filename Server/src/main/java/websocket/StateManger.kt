package websocket

import engine.DataTransferEntity
import io.javalin.websocket.WsContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject

class StateManger{
    private var prevState = HashMap<Int, DataTransferEntity>()

    private fun convertState(newState: HashMap<Int, DataTransferEntity>): JSONObject{
        val state = JSONObject()
        for(key in newState.keys){
            val newdata = JSONObject(newState[key])
            if(prevState.containsKey(key)) {
                newdata.remove("id")
                newdata.remove("sizex")
                newdata.remove("sizey")
                newdata.remove("type")
                if (prevState[key]?.hp == newState[key]?.hp) newdata.remove("hp")
            }
            state.put("$key", newdata)
        }
        prevState = newState
        return state
    }

    fun sendFullState(ctx: WsContext, state: HashMap<Int, DataTransferEntity>){
            ctx.send(JSONObject().put("name", "gd").put("response", state).toString())
    }

    fun sendToAll(clients: MutableSet<WsContext>, newState: HashMap<Int, DataTransferEntity>){
        val state = convertState(newState)
        val bs = JSONObject().put("name", "gd").put("response", state).toString()
        for (c in clients) {
            GlobalScope.launch(Dispatchers.Default) { c.send(bs) }
        }
    }
}