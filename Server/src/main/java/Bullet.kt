abstract class Bullet(var damage: Int, var velocity: Vector2f, var point: Point, var range: Float): Entity(point) {
    val start = point.copy()
    var currentPos = point.copy()
    val hitbox = Hitbox(5f, 5f, this)
    var distanceTraveled = 0f

    fun move(): Boolean {
        currentPos.x += this.velocity.x
        currentPos.y += this.velocity.y
        distanceTraveled += this.velocity.length
        return distanceTraveled >= range
    }
}