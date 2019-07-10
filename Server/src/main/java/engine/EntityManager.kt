package engine

const val TEAMSCOUNT = 2

class EntityManager {
    var unique_counter = 0

    val EntityIDs: MutableMap<Entity, Int> = emptyMap<Entity, Int>().toMutableMap()
    val PlayerNames: MutableMap<Player, String> = emptyMap<Player, String>().toMutableMap()
    val Teams: MutableMap<Int, MutableList<Int>> = emptyMap<Int, MutableList<Int>>().toMutableMap()

    init {
        for (team_number in 0 until TEAMSCOUNT) {
            Teams[team_number] = emptyList<Int>().toMutableList()
        }
    }

    fun respawnPlayer(id: Int) {
        if (id !in EntityIDs.values) return
        getById(id)!!.pos = Point(
                (100..(WIDTH-100)).random().toFloat(), (100..(HEIGHT-100)).random().toFloat()
        )
    }
    fun getTeamById(id: Int): Int {
        if (id !in EntityIDs.values) return 0
        for (team_number in 0 until TEAMSCOUNT) {
            if (!Teams[team_number]!!.contains(id)) continue
            return team_number
        }
        return 0
    }
    fun changeTeam(id: Int, team_id: Int) {
        if (team_id !in 0 until TEAMSCOUNT) return
        for(team_number in 0 until TEAMSCOUNT) {
            if (!Teams[team_number]!!.contains(id)) continue
            Teams[team_number]!!.remove(id)
            Teams[team_id]!!.add(id)
        }
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