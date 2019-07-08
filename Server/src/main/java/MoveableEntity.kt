open class MoveableEntity (var velocity: Vector2f, position: Point, id: Int) : Entity(position, id) {
    fun move() {
        this.position.x += velocity.x
        this.position.y += velocity.y
    }
}