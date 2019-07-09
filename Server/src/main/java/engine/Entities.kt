package engine

abstract class Entity(var pos: Point) {
    abstract val hitbox: Hitbox
}

abstract class MovableEntity(var velocity: Vector2f, pos: Point) : Entity(pos) {
    fun move() {
        this.pos.x += velocity.x
        this.pos.y += velocity.y
    }
}


class Bullet(velocity: Vector2f, pos: Point) : MovableEntity(velocity, pos) {
    override val hitbox = Hitbox(5f, 5f, this)
}

class Player(pos: Point, velocity: Vector2f = Vector2f()) : MovableEntity(velocity, pos) {
    override val hitbox = Hitbox(50f, 10f, this)
}

class Island(pos: Point) : Entity(pos) {
    override val hitbox = Hitbox(100f, 100f, this)
}


