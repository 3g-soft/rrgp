package engine

import kotlin.random.Random

class GameAPI {
    val Engine: Engine               = Engine()
    val DamageManager: DamageManager = DamageManager()
    val EntityManager: EntityManager = EntityManager()

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

    fun createPlayer(): Player {
        var r = Random(System.currentTimeMillis())
        var player = Player(Point(500f, 500f))
        return player
    }



}