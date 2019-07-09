package engine

abstract class MovableEntity (var velocity: Vector2f, pos: Point) : Entity(pos) {
    fun move() {
        this.pos.x += velocity.x
        this.pos.y += velocity.y
    }
}
