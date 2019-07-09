package engine

class DamageManager {
    val EntityHPs: MutableMap<Int, Int> = emptyMap<Int, Int>().toMutableMap()

    val collisionDamage = 30
    val defaultHp = 280

    fun assignHP(id: Int) {
        EntityHPs[id] = defaultHp
    }
    fun dealDamage(id: Int, damage: Int) {
        if (EntityHPs[id] == null) return
        EntityHPs[id] = EntityHPs[id]!! - damage

        if (EntityHPs[id]!! <= 0f) {
            assignHP(id)
            //TODO:tp to island

        }
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