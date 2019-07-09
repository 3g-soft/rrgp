package engine


class Engine {
    private val entities: MutableList<Entity> = emptyList<Entity>().toMutableList()

    fun update(): List<CollisionEvent> {
        entities.forEach { entity ->
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
                    var ent = entities[i]
                    if (ent is Player) {
                        ent.pos.x -= ent.velocity.x
                        ent.pos.y -= ent.velocity.y
                    }
                    ent = entities[j]
                    if (ent is Player) {
                        ent.pos.x -= ent.velocity.x
                        ent.pos.y -= ent.velocity.y
                    }
                }
            }
        }
        return toReturn.toList()
    }

    fun addEntity(entity: Entity) {
        entities.add(entity)
    }

    fun removeEntity(entity: Entity) {
        entities.remove(entity)
    }

    fun setPlayerSpeed(player: Player, speed: Float) {
        player.velocity.length = speed
    }

    fun setPlayerAngle(player: Player, angle: Float) {
        player.velocity.angle = angle
    }

    fun getState(): List<Entity> {
        return entities.toList()
    }
}
