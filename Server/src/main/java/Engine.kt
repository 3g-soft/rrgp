import websocket.EngineInterface
import websocket.Requestable

object Engine: EngineInterface {
    val MOVES: MutableList<MovableEntity> = emptyList<MovableEntity>().toMutableList()
    var nextid = 0
    val players = mutableMapOf<Int, Player>()

    override fun update() {
        for(k in players.values)k.move()
    }
    fun checkAllCollisions(): List<CollisionEvent> {
        return listOf()
    }
    override fun addNewPlayer(): Int {
        val id = ++nextid
        players[id] = Player(0 ,0, Island(0, 0, Point(0f, 0f), 0), Vector2f(0f ,0f), Point(0f, 0f), 0)
        return id

    }

    fun makeShot(owner: Player, initialVector: Vector2f) {}

    fun setPlayerVelocity(player: Player, velocity: Vector2f) {
        player.velocity = velocity.copy()
    }

    override fun getState(): MutableMap<Int, Player> {
        return players
        TODO("must return copy of players")
    }

    override fun removePlayer(id: Int){

    }

    @Requestable(true)
    fun velocityRequest(id: Int, x: String, y: String){
        setPlayerVelocity(players[id]!!, Vector2f(x.toFloat(), y.toFloat()))
    }
}
