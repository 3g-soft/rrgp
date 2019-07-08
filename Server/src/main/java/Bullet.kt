class Bullet(var damage: Int, velocity: Vector2f, var point: Point, id: Int) : MoveableEntity(velocity, point, id) {
    override val moving = true
    val hitbox = Hitbox(5f, 5f, this)
}