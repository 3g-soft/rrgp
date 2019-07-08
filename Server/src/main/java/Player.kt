package engine

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class Player(
    hp: Int, var homeIsland: Island,
    velocity: Vector2f, pos: Point, id: Int
) : MovableEntity(velocity, pos, id, hp) {
    var maxHp = hp
    override val hitbox = Hitbox(50f, 10f, this)
    val shotBullets = mutableListOf<Bullet>()
    val isDead: Boolean
        get() = hp <= 0

    fun selectWeapon(wId: Int) {}

    fun checkCollision(entity: Entity): CollisionEvent {
        return CollisionEvent(this, entity)
    }

    fun hit(entity: Entity) {
        if (entity is Island) {
            if (entity.teamId != this.homeIsland.initialTeamId) {
                this.hp -= 5
                entity.hp -= 5
            }
        }
        if (entity is Player) {
            entity.hp -= 5
        }
    }

    fun respawn() {
        val r = Random(System.currentTimeMillis())
        var angle = r.nextFloat() * PI.toFloat() * 2
        this.hp = maxHp
        this.position.x = 175f * cos(angle)
        this.position.y = 175f * sin(angle)
        this.velocity.x = 0f
        this.velocity.y = 0f
    }
}