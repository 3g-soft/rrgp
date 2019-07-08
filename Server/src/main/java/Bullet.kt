class Bullet(var damage: Int, velocity: Vector2f, var point: Point, id: Int) : MovableEntity(velocity, point, id) {
    val hitbox = Hitbox(5f, 5f, this)
}