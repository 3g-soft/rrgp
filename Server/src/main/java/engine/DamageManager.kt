package engine

class DamageManager {
    val EntityHPs: MutableMap<Int, Int>    = emptyMap<Int, Int>().toMutableMap()
    val EntityMaxHPs: MutableMap<Int, Int> = emptyMap<Int, Int>().toMutableMap()

    val COLLISIONDAMAGE = 30
    val BULLETDAMAGE    = 50
    val MAXHP           = 280

    fun setHP(id: Int, hp: Int) {
        if (id !in EntityHPs.keys) return
        if (hp > EntityMaxHPs[id]!!) {
            EntityHPs[id] = EntityMaxHPs[id]!!
            return
        }
        EntityHPs[id] = hp
    }
    fun refreshPlayer(id: Int) {
        if (id !in EntityHPs.keys) return
        EntityHPs[id] = EntityMaxHPs[id]!!
    }
    fun setMaxHP(id: Int, maxhp: Int) {
        if (id !in EntityMaxHPs.keys) return
        EntityMaxHPs[id] = maxhp
        EntityHPs[id]    = EntityMaxHPs[id]!!
    }
    fun assignHP(id: Int) {
        if (id in EntityHPs.keys) return
        EntityMaxHPs[id] = MAXHP
        EntityHPs[id]    = EntityMaxHPs[id]!!
    }
    fun dealDamage(id: Int, damage: Int): DeathState{
        if (EntityHPs[id] == null) return DeathState.NONE
        EntityHPs[id] = EntityHPs[id]!! - damage

        if (EntityHPs[id]!! <= 0f) {
            return DeathState.DEAD
        }
        return DeathState.ALIVE
    }

    fun getHPbyId(id: Int): Int {
        if (id !in EntityHPs.values) return -1
        return EntityHPs[id]!!
    }
    fun getMaxHPbyId(id: Int): Int {
        if (id !in EntityMaxHPs.values) return -1
        return EntityMaxHPs[id]!!
    }

    fun removeEntity(id: Int) {
        if (id !in EntityHPs.values) return
        for( key in EntityHPs.keys ) {
            if (EntityHPs[key] == id) {
                EntityHPs.remove(key)
                return
            }
        }
    }
}