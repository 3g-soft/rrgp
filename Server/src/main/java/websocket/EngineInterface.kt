package websocket

interface EngineInterface{
    fun setup()
    fun doTick()
    fun addPlayer(): Int
    fun removePlayer(id: Int)
    fun getGameState(): Any
}