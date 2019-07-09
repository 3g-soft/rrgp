package engine

class Player(teamId: Int, homeIsland: Island,
             velocity: Vector2f, pos: Point) : MovableEntity(velocity, pos) {
    init {
        val shotBullets: MutableList<Bullet>
    }

    fun selectWeapon(wId: Int) {}

    fun checkCollision(entity: Entity): CollisionEvent {
        return CollisionEvent(this, entity)
    }
}