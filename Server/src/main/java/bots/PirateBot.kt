package bots

import engine.Engine
import engine.Player
import engine.Point

class PirateBot (val player: Player){
    var entities = Engine.getState()

    fun getNextTurn() {
        entities = Engine.getState()
        if (entities.isEmpty()) return
        var aim: Point = Point()
        entities.forEach { entity ->
            if (entity is Player && aim.distance(entity.pos) < aim.distance(player.pos))
                aim = entity.pos.copy()
        }
        player.velocity.directTo(aim)
    }
}