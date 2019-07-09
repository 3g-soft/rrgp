package engine

class GameAPI {
    val Engine: Engine = Engine()
    val DamageManager: DamageManager = DamageManager()
    val EntityManager: EntityManager = EntityManager()

    fun update() {
        onCollisionDamage(Engine.update())
    }

    fun setPlayerAngle(angle: Float, uid: Int) {
        var player = EntityManager.getById(uid)
        if (player is Player) {
            Engine.setPlayerAngle(player, angle)
        }
    }

    fun setPlayerSpeed(speed: Float, uid: Int) {
        var player = EntityManager.getById(uid)
        if (player is Player) {
            Engine.setPlayerSpeed(player, speed)
        }
    }

    fun setPlayerPos(pos: Point, uid: Int) {
        var player = EntityManager.getById(uid)
        if (player is Player) {
            Engine.setPlayerPos(player, pos)
        }
    }

    fun setPlayerPos(x: Float, y: Float, uid: Int) {
        var player = EntityManager.getById(uid)
        if (player is Player) {
            Engine.setPlayerPos(player, Point(x, y))
        }
    }

    fun createPlayer(): DataTransferPlayer {
//        var r = Random(System.currentTimeMillis())
        var player = Player(Point(500f, 500f))
        EntityManager.identify(player)
        DamageManager.assignHP(EntityManager.getId(player))
        Engine.addEntity(player)
        return DataTransferPlayer(EntityManager.getId(player), player.pos)
    }

    fun getAllEntities(): List<Entity> {
        return Engine.getState()
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
