package engine

class EntityManager {
    val EntityIDs: MutableMap<Entity, Int> = emptyMap<Entity, Int>().toMutableMap()
    val Teams: MutableMap<Int, MutableList<Int>> = emptyMap<Int, MutableList<Int>>().toMutableMap()

    init {
        Teams[0] = emptyList<Int>().toMutableList()
        Teams[1] = emptyList<Int>().toMutableList()
    }

    fun giveTeam(entity: Entity){
        Teams[Teams.size%2]!!.add(EntityIDs[entity]!!)
    }
    fun identify(entity: Entity) {
        if (entity in EntityIDs.keys) return
        EntityIDs[entity] = EntityIDs.size
        giveTeam(entity)
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