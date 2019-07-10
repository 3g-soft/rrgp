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
        val allEvents = Engine.update()
        val deadBullets = allEvents.deadBullets
        for (bullet in deadBullets) {
            removeEntity(EntityManager.getId(bullet))
        }
        onCollisionDamage(allEvents.collisions)
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
            DamageManager.getMaxHPbyId(EntityManager.getId(player)),
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
                            angle=entity.velocity.angle
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
                            DamageManager.getMaxHPbyId(EntityManager.getId(entity)),
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
                            DamageManager.getHPbyId(EntityManager.getId(entity)),
                            DamageManager.getMaxHPbyId(EntityManager.getId(entity))
                        )
                    )
                }

            }
        }
        return toReturn.toList()
    }

    fun removeEntity(id: Int) {
        Engine.removeEntity(EntityManager.getById(id))
        EntityManager.removeEntity(id)
        DamageManager.removeEntity(id)
    }

    fun respawnById(id: Int) {
        EntityManager.respawnPlayer(id)
        DamageManager.refreshPlayer(id)
    }

    fun accelerate(id: Int, isForward: Boolean) {
        val player = EntityManager.getById(id)
        if (player is Player) {
            Engine.accelerate(player, isForward)
        }
    }

    private fun onCollisionDamage(collisions: List<CollisionEvent>) {
        fun deathCheck(entity: Entity, by: Entity) {
            val damage = when (by) {
                is Bullet -> DamageManager.BULLETDAMAGE
                else -> DamageManager.COLLISIONDAMAGE
            }
            when (DamageManager.dealDamage(
                    EntityManager.getId(entity),
                    damage)) {
                DeathState.NONE -> return
                DeathState.ALIVE -> {
                }
                DeathState.DEAD -> {
                    when (entity) {
                        is Island -> {
                            EntityManager.changeTeam(EntityManager.getId(entity),
                                                     EntityManager.getTeamById(EntityManager.getId(by)))
                        }
                        is Player -> {
                            respawnById(EntityManager.getId(entity))
                        }
                    }
                }
            }
        }
        for (collision in collisions) {
            if (collision.target2 is Bullet && collision.target1 is Bullet) {
                removeEntity(EntityManager.getId(collision.target1))
                removeEntity(EntityManager.getId(collision.target2))
                continue
            }
            if (collision.target2 is Bullet){
                deathCheck(collision.target1, collision.target2)
                removeEntity(EntityManager.getId(collision.target2))
            }
            if (collision.target1 is Bullet){
                deathCheck(collision.target2, collision.target1)
                removeEntity(EntityManager.getId(collision.target1))
            }
        }
    }

    fun makeShot(id: Int, side: Int): DataTransferEntity {
        val angle: Float
        val player = EntityManager.getById(id)
        if (player is Player) {
            angle = when (side) {
                1 -> {
                    player.velocity.angle - PI.toFloat() / 2f
                }
                else -> {
                    player.velocity.angle + PI.toFloat() / 2f
                }
                //  ANGLES ARE RIGGED
            }
            val radius = player.hitbox.sizey / 2 + 25f / 2f + 5
            val bullet = Bullet(
                Vector2f(10f, angle, false),
                Point(radius * cos(angle) + player.pos.x, radius * sin(angle) + player.pos.y)
            )
            bullet.velocity += player.velocity
            EntityManager.identify(bullet)
            val bulId = EntityManager.getId(bullet)
            Engine.addEntity(bullet)
            return DataTransferEntity(
                bulId,
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
