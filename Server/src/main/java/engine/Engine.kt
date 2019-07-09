package engine

class Engine {
    val moves: MutableList<MovableEntity> = emptyList<MovableEntity>().toMutableList()

    fun update() {
        moves.forEach { entity ->
            entity.move()
        }
    }
    fun checkAllCollisions(): List<CollisionEvent> {
        return listOf()
    }
    fun addNewPlayer() {}
    fun makeShot(owner: Player, initialVector: Vector2f) {}

    fun setPlayerVelocity(player: Player, velocity: Vector2f) {
        player.velocity = velocity.copy()
    }

    fun getState(): List<Entity> {
        return listOf()
    }
}
