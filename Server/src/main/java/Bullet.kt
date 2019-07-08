package engine

class Bullet(var damage: Int, velocity: Vector2f, var point: Point, id: Int) : MovableEntity(velocity, point, id, 1) {
    override val hitbox = Hitbox(5f, 5f, this)

    fun hit(target: Entity) {
        target.hp -= this.damage
    }
}