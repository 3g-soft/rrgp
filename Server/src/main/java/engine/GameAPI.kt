package engine

class GameAPI {
    val Engine: Engine = Engine()
    val DamageManager: DamageManager = DamageManager()
    val EntityManager: EntityManager = EntityManager()

    fun update() {
        onCollisionDamage(Engine.update())
    }

    fun setPlayerAngle(angle: Float, id: Int) {
        var player = EntityManager.getById(id)
        if (player is Player) {
            Engine.setPlayerAngle(player, angle)
        }
    }

    fun setPlayerSpeed(speed: Float, id: Int) {
        var player = EntityManager.getById(id)
        if (player is Player) {
            Engine.setPlayerSpeed(player, speed)
        }
    }

    fun setPlayerPos(pos: Point, id: Int) {
        var player = EntityManager.getById(id)
        if (player is Player) {
            Engine.setPlayerPos(player, pos)
        }
    }

    fun setPlayerPos(x: Float, y: Float, id: Int) {
        var player = EntityManager.getById(id)
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
                            entity.velocity.angle
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
                            entity.hitbox.sizey
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

    private fun onCollisionDamage(collisions: List<CollisionEvent>) {
        fun deathCheck(value: Entity) {
            when(DamageManager.dealDamage(
                    EntityManager.getId(value),
                    DamageManager.collisionDamage)) {
                DeathState.NONE -> return
                DeathState.ALIVE -> {}
                DeathState.DEAD -> {
                    when(value) {
                        is Island -> value.onDeath()
                        is Player -> value.onDeath()
                    }
                }
            }
        }

        collisions.forEach { collision ->
            deathCheck(collision.target1)
            deathCheck(collision.target2)
        }
    }
}
