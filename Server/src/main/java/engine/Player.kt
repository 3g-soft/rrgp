package engine

class Player(pos: Point, velocity: Vector2f = Vector2f()) : MovableEntity(velocity, pos) {
    override val hitbox = Hitbox(50f, 10f, this)
}