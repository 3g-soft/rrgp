import websocket.NetworkPlayer

class Engine{
    val MOVES: MutableList<MovableEntity> = emptyList<MovableEntity>().toMutableList()
    var nextid = 0
    var angle = 0f
//    val players = mutableMapOf<Int, Player>()

    fun update() {
        MOVES.forEach { it.move() }
        angle += 0.05f
    }
    fun checkAllCollisions(): List<CollisionEvent> {
        return listOf()
    }
    fun addNewPlayer(): Int {
        return nextid++
    }

    fun makeShot(owner: Player, initialVector: Vector2f) {}

    fun setPlayerVelocity(player: Player, velocity: Vector2f) {
        player.velocity = velocity.copy()
    }

    fun getState(): List<Entity> {

        return listOf(NetworkPlayer(Vector2f(0f, 0f), Point(1f, 1f), angle , 1, nextid - 1, Point(200f, 100f)))
//        TODO("must return copy of players")
//        return emptyList()
    }

    fun removePlayer(id: Int){

    }
}
