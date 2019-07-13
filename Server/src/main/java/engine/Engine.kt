package engine


const val WIDTH = 2000
const val HEIGHT = 2000
const val ACCELERATION = 0.15f

class Engine {
    private val entities: MutableList<Entity> = emptyList<Entity>().toMutableList()

    fun update(): List<CollisionEvent> {
        for (entity in entities) {
            if (entity is MovableEntity) entity.move()
        }
        return checkAllCollisions()
    }

    private fun checkAllCollisions(): List<CollisionEvent> {
        val toReturn = mutableListOf<CollisionEvent>()
        for (i in 0 until entities.size) {
            for (j in i + 1 until entities.size) {
                if (entities[i].hitbox.checkCollision(entities[j].hitbox)) {
                    toReturn.add(CollisionEvent(entities[i], entities[j]))
                    val ent1 = entities[i]
                    val ent2 = entities[j]
                    if (ent1 is Player && ent2 is Player) {
                        val b2 = Vector2f(ent2.pos.x - ent1.pos.x, ent2.pos.y - ent1.pos.y).normalize()
                        val b1 = Vector2f(ent1.pos.x - ent2.pos.x, ent1.pos.y - ent2.pos.y).normalize()
                        val len1 = ent1.velocity.length
                        val len2 = ent2.velocity.length
                        ent1.pos.x += b1.x * 25f
                        ent1.pos.y += b1.y * 25f
                        ent1.velocity = b1.copy() * len2 / 2f
                        ent2.pos.x += b2.x * 25f
                        ent2.pos.y += b2.y * 25f
                        ent2.velocity = b2.copy() * len1 / 2f
                        continue
                    }
                    if (ent1 is Player && ent2 is Island) {
                        ent1.velocity = -ent1.velocity / 2f
                        val b1 = ent1.velocity.copy().normalize()
                        ent1.pos.x += b1.x * 25f
                        ent1.pos.y += b1.y * 25f
                    }
                    if (ent2 is Player && ent1 is Island) {
                        ent2.velocity = -ent2.velocity / 2f
                        val b2 = ent2.velocity.copy().normalize()
                        ent2.pos.x += b2.x * 25f
                        ent2.pos.y += b2.y * 25f
                    }

                }
            }
        }
        return toReturn.toList()
    }

    fun addEntity(entity: Entity) {
        entities.add(entity)
    }

    fun removeEntity(entity: Entity?) {
        if (entity == null) return
        entities.remove(entity)
    }

    fun setPlayerSpeed(player: Player, speed: Float) {
        player.velocity.length = speed
    }

    fun setPlayerAngle(player: Player, angle: Float) {
        player.velocity.angle = angle
    }

    fun setPlayerPos(player: Player, pos: Point) {
        player.pos = pos.copy()
    }

    fun accelerate(player: Player, isForward: Boolean, maxSpeed: Float) {
        when (isForward) {
            true -> {
                if (player.velocity.length < maxSpeed) {
                    player.velocity.length += ACCELERATION
                }
            }
            false -> {
                if (player.velocity.length > ACCELERATION) {
                    val angle = player.velocity.angle
                    player.velocity.length -= ACCELERATION
                    if (player.velocity.length <= 0f) {
                        player.velocity.angle = angle
                        player.velocity.length = 0.01f
                    }
                }
            }

        }
    }

    fun turn(player: Player, side: Int, turnRate: Float) {
        when (side) {
            1 -> player.velocity.angle -= turnRate
            else -> player.velocity.angle += turnRate
        }
    }

    fun getState(): List<Entity> {
        return entities.toList()
    }
}
