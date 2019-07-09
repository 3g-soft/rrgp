package engine

class Bullet(velocity: Vector2f, var point: Point) : MovableEntity(velocity, point) {
    val hitbox = Hitbox(5f, 5f, this)
}