package websocket

interface EngineInterface{
    fun update()
    fun addNewPlayer(): Int
    fun removePlayer(id: Int)
    fun getState(): Any
}