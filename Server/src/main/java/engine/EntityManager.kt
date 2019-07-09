package engine

const val TEAMSCOUNT = 2
class EntityManager {
    var unique_counter = 0
    val EntityIDs: MutableMap<Entity, Int> = emptyMap<Entity, Int>().toMutableMap()
    val Teams: MutableMap<Int, MutableList<Int>> = emptyMap<Int, MutableList<Int>>().toMutableMap()

    init {
        Teams[0] = emptyList<Int>().toMutableList()
        Teams[1] = emptyList<Int>().toMutableList()
    }

    fun giveTeam(entity: Entity){
        Teams[Teams.size % TEAMSCOUNT]!!.add(EntityIDs[entity]!!)
    }
    fun identify(entity: Entity) {
        if (entity in EntityIDs.keys) return
        EntityIDs[entity] = ++unique_counter
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
    fun getId(entity: Entity): Int {
        if (entity !in EntityIDs) return 0
        return EntityIDs[entity]!!
    }
    fun removeEntity(id: Int) {
        if (id !in EntityIDs.values) return
        for( key in EntityIDs.keys ) {
            if (EntityIDs[key] == id) {
                EntityIDs.remove(key)
                break
            }
        }
        for ( key in Teams.keys ) {
            if ( Teams[key]!!.contains(id) )
                for ( entityid in Teams[key]!! ) {
                    if ( entityid == id ) {
                        Teams[key]!!.remove(id)
                        return
                    }
                }
        }
    }
}