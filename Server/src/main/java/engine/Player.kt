package engine

class Player(velocity: Vector2f, pos: Point) : MovableEntity(velocity, pos) {
    override val hitbox = Hitbox(50f, 10f, this)
}