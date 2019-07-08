class Player(var hp: Int, teamId: Int, homeIsland: Island,
             velocity: Vector2f, pos: Point, id: Int ) : MoveableEntity(velocity, pos, id) {
    override val moving = true
    val isDead: Boolean
        get() = hp <= 0

    init {
        hp = 0
        val shotBullets: MutableList<Bullet>
    }

    fun selectWeapon(wId: Int) {}

    fun checkCollision(entity: Entity): CollisionEvent {
        return CollisionEvent(this, entity)
    }
}