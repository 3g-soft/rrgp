package engine

import kotlin.system.measureNanoTime

object Engine {
    private val entities: MutableList<Entity> = emptyList<Entity>().toMutableList()

    fun update() {
        entities.forEach { entity ->
            if (entity is MovableEntity) entity.move()
        }
        checkAllCollisions()
        checkDeaths()

    }
    fun checkAllCollisions(): List<CollisionEvent> {
        val toReturn = mutableListOf<CollisionEvent>()
        for (i in 0 until entities.size) {
            for (j in i + 1 until entities.size) {
                if (entities[i].hitbox.checkCollision(entities[j].hitbox)) {
                    toReturn.add(CollisionEvent(entities[i], entities[j]))
                    var ent = entities[i]
                    if (ent is Bullet) {
                        ent.hit(entities[j])
                    }
                    else if (ent is Player) {
                        ent.position.x -= ent.velocity.x
                        ent.position.y -= ent.velocity.y
                        ent.hit(entities[j])
                    }
                    ent = entities[j]
                    if (ent is Bullet) {
                        ent.hit(entities[i])
                    }
                    else if (ent is Player) {
                        ent.position.x -= ent.velocity.x
                        ent.position.y -= ent.velocity.y
                        ent.hit(entities[j])
                    }
                }
            }
        }
        return toReturn.toList()
    }
    fun checkDeaths() {
        for (i in 0 until entities.size) {
            var ent = entities[i]
            when (ent) {
                is Bullet -> {
                    entities.removeAt(i)
                }
                is Player -> {
                    if (ent.homeIsland.initialTeamId == ent.homeIsland.teamId) {
                        ent.respawn()
                    }
                    else {
                        //TODO: die permanenly
                    }
                }
                is Island -> {
                    //TODO: island capturing
                }

            }

        }
    }


    fun addNewPlayer(newPlayer: Player) {}

    fun makeShot(owner: Player, initialVector: Vector2f) {}

    fun setPlayerVelocity(player: Player, velocity: Vector2f) {
        player.velocity = velocity.copy()
    }

    fun getState(): List<Entity> {
        return entities.toList()
    }
}
