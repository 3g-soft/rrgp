class Player(position: Point, var hp: Int, var velocity: Vector2f, teamId: Int, homeIsland: Island) : Entity(position) {
    val isDead: Boolean
        get() = hp <= 0

    init {
        hp = 0
        velocity = Vector2f()
        val shotBullets: MutableList<Bullet>
    }

    fun move() {
        this.position.x += velocity.x
        this.position.y += velocity.y
    }

    fun selectWeapon(wId: Int) {}

    fun checkCollision(entity: Entity): CollisionEvent {
        return CollisionEvent(this, entity)
    }
}