package engine

abstract class MovableEntity (var velocity: Vector2f, position: Point) : Entity(position) {
    fun move() {
        this.position.x += velocity.x
        this.position.y += velocity.y
    }
}
