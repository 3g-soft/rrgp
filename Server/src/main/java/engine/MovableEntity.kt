package engine

abstract class MovableEntity (var velocity: Vector2f, pos: Point) : Entity(pos) {
    fun move() {
        this.position.x += velocity.x
        this.position.y += velocity.y
    }
}
