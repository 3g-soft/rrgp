class Bullet(var damage: Int, var velocity: Vector2f, var point: Point) : Entity(point) {
    val currentPos = point.copy()
    val hitbox = Hitbox(5f, 5f, this)
    fun move() {
        currentPos.x += this.velocity.x
        currentPos.y += this.velocity.y
    }
}