package engine

import java.security.InvalidParameterException
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

class GameAPI {
    val engine: Engine = Engine()
    private val damageManager: DamageManager = DamageManager()
    private val entityManager: EntityManager = EntityManager()
    private val skillManager = SkillManager(damageManager)
    private var gameEndTimer = -1
    init {
        val island1 = Island(Point(1600f, 0f))
        val island2 = Island(Point(-1600f, 0f))
        val island3 = Island(Point(0f, 1600f))
        val island4 = Island(Point(0f, -1600f))

        engine.addEntity(island1)
        entityManager.identify(island1)
        damageManager.createIsland(entityManager.getId(island1))

        engine.addEntity(island2)
        entityManager.identify(island2)
        damageManager.createIsland(entityManager.getId(island2))

        engine.addEntity(island3)
        entityManager.identify(island3)
        damageManager.createIsland(entityManager.getId(island3))
        entityManager.changeTeam(entityManager.getId(island3), TEAMS_COUNT - 1)

        engine.addEntity(island4)
        entityManager.identify(island4)
        damageManager.createIsland(entityManager.getId(island4))
        entityManager.changeTeam(entityManager.getId(island4), TEAMS_COUNT - 1)
    }

    fun update() {
        if (gameEndTimer != -1) {
            gameEndTimer--
            for (id in damageManager.getIds()) {
                damageManager.getPlayerProfile(id).resetTicks--
            }
            if (gameEndTimer == 0) {
                gameEndTimer--
                resetGame()
            }
        }

       if (checkWin() && gameEndTimer < 0) {
           gameEndTimer = RESETTICKS
           for (id in damageManager.getIds()) {
               damageManager.getPlayerProfile(id).resetTicks = RESETTICKS
           }

       }


        onCollisionDamage(engine.update())
        val deadBullets = mutableListOf<Bullet>()
        val escapedPlayers = mutableListOf<Int>()
        val entities = engine.getState()
        for (entity in entities) {
            if (entity is Bullet && (entity.distanceTraveled >= damageManager.getShotRange(entityManager.getId(entity))
                        || abs(entity.pos.x) > WIDTH || abs(entity.pos.y) > HEIGHT)
            ) deadBullets.add(entity)
            if (entity is Player && (abs(entity.pos.x) > WIDTH || abs(entity.pos.y) > HEIGHT)) escapedPlayers.add(
                entityManager.getId(entity)
            )
        }
        for (bullet in deadBullets) {
            removeEntity(entityManager.getId(bullet))
        }
        val events = damageManager.update(escapedPlayers)

        val deadPlayers = events.deadPlayers
        for (deadPlayer in deadPlayers) {
            respawnById(deadPlayer)
        }
        val respawnedPlayers = events.respawnedPlayers
        for (respawnedPlayer in respawnedPlayers) {
            entityManager.getById(respawnedPlayer)!!.hitbox.isCollidable = true
        }
    }

    fun getName(id: Int): String {
        return entityManager.getNameById(id)
    }

    fun setName(id: Int, name: String) {
        entityManager.setNameById(id, name)
    }

    fun setPlayerAngle(angle: Float, id: Int) {
        val player = entityManager.getById(id)
        if (player is Player) {
            engine.setPlayerAngle(player, angle)
        }
    }

    fun setPlayerSpeed(speed: Float, id: Int) {
        val player = entityManager.getById(id)
        if (player is Player) {
            engine.setPlayerSpeed(player, speed)
        }
    }

    fun setPlayerPos(pos: Point, id: Int) {
        val player = entityManager.getById(id)
        if (player is Player) {
            engine.setPlayerPos(player, pos)
        }
    }

    fun createPlayer(): DataTransferEntity {
        val player = Player(Point(500f, 500f))
        entityManager.identify(player)
        val id = entityManager.getId(player)
        damageManager.assignHP(id)
        engine.addEntity(player)
        respawnById(id)
        skillManager.addPlayerId(id)
        return DataTransferEntity(
            entityManager.getId(player),
            player.pos,
            DataTransferEntityType.Player,
            player.hitbox.sizex,
            player.hitbox.sizey,
            damageManager.getHPbyId(id),
            damageManager.getMaxHPbyId(id),
            player.velocity.angle,
            entityManager.getTeamById(id),
            damageManager.getShotCooldown(id, 1),
            damageManager.getShotCooldown(id, 2),
            damageManager.getMaxCooldown(id),
            damageManager.isOutside(id),
            entityManager.getNameById(id),
            damageManager.getRespawnTimer(id),
            RESPAWNTICKS,
            damageManager.getGold(id),
            MAXGOLD,
            damageManager.getTickTimer()
        )
    }


    fun getGold(teamId: Int): Int {
        var gold = 0
        for (id in damageManager.getIds()) {
            if (teamId == entityManager.getTeamById(id)) gold += damageManager.getGold(id)
        }
        return gold
    }

    fun checkWin(): Boolean {
        if (getGold(0) >= MAXGOLD) return true
        if (getGold(1) >= MAXGOLD) return true
        return false
    }

    fun getAllEntities(): List<DataTransferEntity> {
        val toReturn = mutableListOf<DataTransferEntity>()
        val listOfEntities = engine.getState()
        for (entity in listOfEntities) {
            val id = entityManager.getId(entity)
            when (entity) {
                is Bullet -> {
                    toReturn.add(
                        DataTransferEntity(
                            id,
                            entity.pos,
                            DataTransferEntityType.Bullet,
                            entity.hitbox.sizex,
                            entity.hitbox.sizey,
                            angle = entity.velocity.angle,
                            team = entityManager.getTeamById(entityManager.getId(entity))
                        )
                    )
                }
                is Player -> {
                    toReturn.add(
                        DataTransferEntity(
                            id,
                            entity.pos,
                            DataTransferEntityType.Player,
                            entity.hitbox.sizex,
                            entity.hitbox.sizey,
                            damageManager.getHPbyId(id),
                            damageManager.getMaxHPbyId(id),
                            entity.velocity.angle,
                            entityManager.getTeamById(id),
                            damageManager.getShotCooldown(id, 1),
                            damageManager.getShotCooldown(id, 2),
                            damageManager.getMaxCooldown(id),
                            damageManager.isOutside(id),
                            entityManager.getNameById(id),
                            damageManager.getRespawnTimer(id),
                            RESPAWNTICKS,
                            damageManager.getGold(id),
                            MAXGOLD,
                            damageManager.getTickTimer()
                        )
                    )
                }
                is Island -> {
                    toReturn.add(
                        DataTransferEntity(
                            id,
                            entity.pos,
                            DataTransferEntityType.Island,
                            entity.hitbox.sizex,
                            entity.hitbox.sizey,
                            damageManager.getHPbyId(id),
                            damageManager.getMaxHPbyId(id),
                            team = entityManager.getTeamById(entityManager.getId(entity))
                        )
                    )
                }
            }
        }
        return toReturn.toList()
    }

    fun removeEntity(id: Int) {
        engine.removeEntity(entityManager.getById(id))
        entityManager.removeEntity(id)
        damageManager.removeEntity(id)
        skillManager.removePlayerId(id)
    }

    private fun respawnById(id: Int) {
        var player = entityManager.getById(id)
        if (player is Player) {
            engine.setPlayerSpeed(player, 0.01f)
            entityManager.respawnPlayer(id)
            damageManager.refreshPlayer(id)
            damageManager.goOnRespawn(id)
            entityManager.getById(id)!!.hitbox.isCollidable = false
            skillManager.removePlayerId(id)
            skillManager.addPlayerId(id)
        }
    }

    fun accelerate(id: Int, isForward: Boolean) {
        if (damageManager.isRespawning(id)) return
        val player = entityManager.getById(id)
        if (player is Player) {
            engine.accelerate(player, isForward, damageManager.getMaxSpeedById(id))
        }
    }

    fun turn(id: Int, side: Int) {
        if (damageManager.isRespawning(id)) return
        val player = entityManager.getById(id)
        if (player is Player) {
            engine.turn(player, side, damageManager.getTurnRateById(id))
        }
    }


    private fun onCollisionDamage(collisions: List<CollisionEvent>) {
        fun deathCheck(entity: Entity, by: Entity) {
            val damage = when (by) {
                is Bullet -> damageManager.getShotDamage(entityManager.getId(by))
                else -> {
                    if (by is MovableEntity && entity is MovableEntity) (entity.velocity.length * 30f).toInt()
                    else if (by is MovableEntity) (by.velocity.length * 30f).toInt()
                    else if (entity is MovableEntity) (entity.velocity.length * 15f).toInt()
                    else 30
                }
            }
            when (damageManager.dealDamage(
                entityManager.getId(entity),
                damage
            )) {
                DeathState.NONE -> return
                DeathState.ALIVE -> {
                }
                DeathState.DEAD -> {
                    when (entity) {
                        is Island -> {
                            val killerId = if (by is Bullet) {
                                damageManager.getShooterId(entityManager.getId(by))
                            } else {
                                entityManager.getId(by)
                            }
                            if (entityManager.getTeamById(entityManager.getId(entity)) != entityManager.getTeamById(
                                    killerId
                                )
                            )
                                damageManager.getKill(killerId)
                            entityManager.changeTeam(
                                entityManager.getId(entity),
                                entityManager.getTeamById(
                                    if (by is Bullet)
                                        damageManager.getShooterId(entityManager.getId(by))
                                    else
                                        entityManager.getId(by)
                                )
                            )


                            damageManager.refreshPlayer(entityManager.getId(entity))

                        }
                        is Player -> {
                            val killerId = if (by is Bullet) {
                                damageManager.getShooterId(entityManager.getId(by))
                            } else {
                                entityManager.getId(by)
                            }
                            if (entityManager.getTeamById(entityManager.getId(entity)) != entityManager.getTeamById(
                                    killerId
                                )
                            )
                                damageManager.getKill(killerId)
                            respawnById(entityManager.getId(entity))
                        }
                    }
                }
            }
        }//ya ub'yu seb9 sey4as
        for (collision in collisions) {
            if (collision.target2 is Bullet && collision.target1 is Bullet) {
                removeEntity(entityManager.getId(collision.target1))
                removeEntity(entityManager.getId(collision.target2))
                continue
            }
            if (collision.target2 is Bullet) {
                deathCheck(collision.target1, collision.target2)
                removeEntity(entityManager.getId(collision.target2))
                continue
            }
            if (collision.target1 is Bullet) {
                deathCheck(collision.target2, collision.target1)
                removeEntity(entityManager.getId(collision.target1))
                continue
            }
            deathCheck(collision.target1, collision.target2)
            deathCheck(collision.target2, collision.target1)
        }
    }

    fun makeShot(id: Int, side: Int) {
        if (damageManager.isRespawning(id)) return
        if (damageManager.checkShotCooldown(id, side)) return
        val angle: Float
        val player = entityManager.getById(id)
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
            entityManager.identify(bullet)
            val bulId = entityManager.getId(bullet)
            damageManager.onShot(bulId, entityManager.getId(player))
            engine.addEntity(bullet)
            damageManager.goOnCooldown(id, side)
        } else {
            throw InvalidParameterException()
        }
    }

    fun resetGame() {
        entityManager.reset()
        damageManager.reset()
        skillManager.reset()
    }

    fun addSkill(playerID: Int, id: Int) {
        skillManager.addSkill(playerID, id)
    }

}
