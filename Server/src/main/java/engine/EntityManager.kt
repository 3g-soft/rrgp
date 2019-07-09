package engine

class EntityManager {
    val EntityIDs: MutableMap<Entity, Int> = emptyMap<Entity, Int>().toMutableMap()

    fun identify(entity: Entity) {
        EntityIDs[entity] = EntityIDs.size
    }
}