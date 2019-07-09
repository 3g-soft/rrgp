package engine

class Bullet(velocity: Vector2f, pos: Point) : MovableEntity(velocity, pos) {
    override val hitbox = Hitbox(5f, 5f, this)
}