package engine

class EntityManager {
    val EntityIDs: MutableMap<Entity, Int> = emptyMap<Entity, Int>().toMutableMap()
    val IslandBoys: MutableMap<Int, MutableList<Int>> = emptyMap<Int, MutableList<Int>>().toMutableMap()

//    fun refreshIslandTeams() {
//        EntityIDs.forEach { entry: Map.Entry<Entity, Int> ->
//            if (entry.key is Player)
//        }
//    }



    fun identify(entity: Entity) {
        EntityIDs[entity] = EntityIDs.size
    }
    fun getById(id: Int): Entity? {
        if (id !in EntityIDs.values) return null
        for (key in EntityIDs.keys){
            if (EntityIDs[key] == id)
                return key
        }
        return null
    }
}