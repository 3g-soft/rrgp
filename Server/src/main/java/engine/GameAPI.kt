package engine

class GameAPI {
    val Engine: Engine               = Engine()
    val DamageManager: DamageManager = DamageManager()
    val EntityManager: EntityManager = EntityManager()

    fun update() {
        Engine.update()
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


    fun createPlayer(): Player {
//        var r = Random(System.currentTimeMillis())
        var player = Player(Point(500f, 500f))
        EntityManager.identify(player)
        DamageManager.assignHP(EntityManager.getId(player))
        Engine.addEntity(player)
        return player
    }
}