package engine

import java.security.InvalidParameterException
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class GameAPI {
    val Engine: Engine = Engine()
    val DamageManager: DamageManager = DamageManager()
    val EntityManager: EntityManager = EntityManager()

    fun update() {
        onCollisionDamage(Engine.update())
    }

    fun setPlayerAngle(angle: Float, id: Int) {
        val player = EntityManager.getById(id)
        if (player is Player) {
            Engine.setPlayerAngle(player, angle)
        }
    }

    fun setPlayerSpeed(speed: Float, id: Int) {
        val player = EntityManager.getById(id)
        if (player is Player) {
            Engine.setPlayerSpeed(player, speed)
        }
    }

    fun setPlayerPos(pos: Point, id: Int) {
        val player = EntityManager.getById(id)
        if (player is Player) {
            Engine.setPlayerPos(player, pos)
        }
    }

    fun setPlayerPos(x: Float, y: Float, id: Int) {
        val player = EntityManager.getById(id)
        if (player is Player) {
            Engine.setPlayerPos(player, Point(x, y))
        }
    }

    fun createPlayer(): DataTransferEntity {
//        var r = Random(System.currentTimeMillis())
        var player = Player(Point(500f, 500f))
        EntityManager.identify(player)
        DamageManager.assignHP(EntityManager.getId(player))
        Engine.addEntity(player)
        return DataTransferEntity(
            EntityManager.getId(player),
            player.pos,
            DataTransferEntityType.Player,
            player.hitbox.sizex,
            player.hitbox.sizey,
            DamageManager.getHPbyId(EntityManager.getId(player)),
            player.velocity.angle
        )
    }

    fun getAllEntities(): List<DataTransferEntity> {
        val toReturn = mutableListOf<DataTransferEntity>()
        val listOfEntities = Engine.getState()
        for (entity in listOfEntities) {
            when (entity) {
                is Bullet -> {
                    toReturn.add(
                        DataTransferEntity(
                            EntityManager.getId(entity),
                            entity.pos,
                            DataTransferEntityType.Bullet,
                            entity.hitbox.sizex,
                            entity.hitbox.sizey,
                            angle = entity.velocity.angle
                        )
                    )
                }
                is Player -> {
                    toReturn.add(
                        DataTransferEntity(
                            EntityManager.getId(entity),
                            entity.pos,
                            DataTransferEntityType.Player,
                            entity.hitbox.sizex,
                            entity.hitbox.sizey,
                            DamageManager.getHPbyId(EntityManager.getId(entity)),
                            entity.velocity.angle
                        )
                    )
                }
                is Island -> {
                    toReturn.add(
                        DataTransferEntity(
                            EntityManager.getId(entity),
                            entity.pos,
                            DataTransferEntityType.Island,
                            entity.hitbox.sizex,
                            entity.hitbox.sizey,
                            DamageManager.getHPbyId(EntityManager.getId(entity))
                        )
                    )
                }

            }
        }
        return toReturn.toList()
    }

    fun removePlayer(id: Int) {
        Engine.removeEntity(EntityManager.getById(id))
        EntityManager.removeEntity(id)
        DamageManager.removeEntity(id)
    }

    fun checkDeath() {

    }

    fun accelerate(id: Int, isForward: Boolean) {
        val player = EntityManager.getById(id)
        if (player is Player) {
            Engine.accelerate(player, isForward)
        }
    }

    private fun onCollisionDamage(collisions: List<CollisionEvent>) {
        fun deathCheck(value: Entity) {
            when (DamageManager.dealDamage(
                EntityManager.getId(value),
                DamageManager.collisionDamage
            )) {
                DeathState.NONE -> return
                DeathState.ALIVE -> {
                }
                DeathState.DEAD -> {
                    when (value) {
                        is Island -> {

                        }
                        is Player -> {

                        }
                    }
                }
            }
        }

        collisions.forEach { collision ->
            deathCheck(collision.target1)
            deathCheck(collision.target2)
        }
    }

    fun makeShot(id: Int, side: Int): DataTransferEntity {
        val angle: Float
        val player = EntityManager.getById(id)
        if (player is Player) {
            when (side) {
                1 -> {
                    angle = player.velocity.angle + PI.toFloat() / 2f
                }
                2 -> {
                    angle = player.velocity.angle - PI.toFloat() / 2f
                }
                else -> {
                    angle = player.velocity.angle
                }
            }
            val radius = player.hitbox.sizey / 2 + 25f / 2f + 5
            val bullet = Bullet(
                Vector2f(10f, angle, false),
                Point(radius * cos(angle) + player.pos.x, radius * sin(angle) + player.pos.y)
            )
            EntityManager.identify(bullet)
            val id = EntityManager.getId(bullet)
            Engine.addEntity(bullet)
            return DataTransferEntity(
                id,
                bullet.pos,
                DataTransferEntityType.Bullet,
                bullet.hitbox.sizex,
                bullet.hitbox.sizey,
                angle = bullet.velocity.angle
            )
        } else {
            throw InvalidParameterException()
        }
    }


}
