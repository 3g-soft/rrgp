package engine

abstract class Entity(var pos: Point) {
    abstract val hitbox: Hitbox
}

abstract class MovableEntity(var velocity: Vector2f, pos: Point) : Entity(pos) {
    open fun move() {
        if (velocity.length > 0.01f) {
            this.pos.x += velocity.x
            this.pos.y += velocity.y
        }
    }
}

class Bullet(velocity: Vector2f, pos: Point) : MovableEntity(velocity, pos) {
    override val hitbox = Hitbox(25f, 25f, this)
    var distanceTraveled = 0f
    val maxDistanceTraveled = 1000f
    override fun move() {
        super.move()
        this.distanceTraveled += this.velocity.length
    }
    fun checkDeath() {

    }

}

class Player(pos: Point, velocity: Vector2f = Vector2f()) : MovableEntity(velocity, pos) {
    override val hitbox = Hitbox(200f, 100f, this)
}

class Island(pos: Point) : Entity(pos) {
    override val hitbox = Hitbox(400f, 400f, this)
}
