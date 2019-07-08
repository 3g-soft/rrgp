package engine

abstract class MovableEntity (var velocity: Vector2f, position: Point, id: Int, hp: Int) : Entity(position, id, hp) {
    fun move() {
        this.position.x += velocity.x
        this.position.y += velocity.y
    }
}
