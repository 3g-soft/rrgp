package engine

class DamageManager {
    private val entityHPs: MutableMap<Int, Int> = emptyMap<Int, Int>().toMutableMap()
    private val entityMaxHPs: MutableMap<Int, Int> = emptyMap<Int, Int>().toMutableMap()

    val collisionDamage = 30
    val bulletDamage = 50
    private val maxHP = 280

    private fun setHP(id: Int, hp: Int) {
        if (id !in entityHPs.keys) return
        if (hp > entityMaxHPs[id]!!) {
            entityHPs[id] = entityMaxHPs[id]!!
            return
        }
        entityHPs[id] = hp
    }

    fun refreshPlayer(id: Int) {
        if (id !in entityHPs.keys) return
        setHP(id, entityMaxHPs[id]!!)
    }

    fun setMaxHP(id: Int, maxHP: Int) {
        if (id !in entityMaxHPs.keys) return
        entityMaxHPs[id] = maxHP
        entityHPs[id] = entityMaxHPs[id]!!
    }

    fun assignHP(id: Int) {
        if (id in entityHPs.keys) return
        entityMaxHPs[id] = maxHP
        entityHPs[id] = entityMaxHPs[id]!!
    }

    fun dealDamage(id: Int, damage: Int): DeathState {
        if (id !in entityHPs.keys) return DeathState.NONE
        entityHPs[id] = entityHPs[id]!! - damage

        if (entityHPs[id]!! <= 0f) {
            return DeathState.DEAD
        }
        return DeathState.ALIVE
    }

    fun getHPbyId(id: Int): Int {
        if (id !in entityHPs.keys) return -1
        return entityHPs[id]!!
    }

    fun getMaxHPbyId(id: Int): Int {
        if (id !in entityMaxHPs.keys) return -1
        return entityMaxHPs[id]!!
    }

    fun removeEntity(id: Int) {
        if (id !in entityHPs.keys) return
        for (key in entityHPs.keys) {
            if (entityHPs[key] == id) {
                entityHPs.remove(key)
                return
            }
        }
    }
}