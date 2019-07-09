package engine

class Bullet(velocity: Vector2f, var point: Point) : MovableEntity(velocity, point) {
    override val hitbox = Hitbox(5f, 5f, this)
}