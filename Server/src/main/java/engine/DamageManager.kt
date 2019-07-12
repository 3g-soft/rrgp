package engine

import kotlin.math.min

data class Profile(
    var maxSpeed: Float = 5f,
    var turnRate: Float = 0.1f,
    var curHP: Int = 280,
    var maxHP: Int = 280,
    var hpRegen: Int = 1,
    var escapeTimer: Int = -1,
    var hpTimer: Int = 0,
    var damage: Int = 30,
    var shotCooldown: Int = 60,
    var leftShotTimer: Int = 0,
    var rightShotTimer: Int = 0
)

data class IslandProfile(
    var curHP: Int = 280,
    var maxHP: Int = 280
//    var damage: Int = 30,
//    var shotCooldown: Int = 60,
//    var shotTimer: Int = 0
)


const val MAXESCAPETICKS = 150
const val MAXHPTICKS = 60

class DamageManager {
    private val profiles: MutableMap<Int, Profile> = emptyMap<Int, Profile>().toMutableMap()
    private val bulletToShooter: MutableMap<Int, Int> = emptyMap<Int, Int>().toMutableMap()
    private val islandProfiles: MutableMap<Int, IslandProfile> = emptyMap<Int, IslandProfile>().toMutableMap()
    val collisionDamage = 30

    fun update(escapedPlayers: List<Int>): List<Int> {
        val deadPlayers = mutableListOf<Int>()
        for (id in profiles.keys) {
            val profile = profiles[id]
            if (id in escapedPlayers) {
                if (profile!!.escapeTimer == -1) profile.escapeTimer = 1
                else profile.escapeTimer++
                if (profile.escapeTimer > MAXESCAPETICKS) deadPlayers.add(id)
            } else if (profile!!.escapeTimer != -1) profile.escapeTimer = -1
            if (profile.hpTimer == 0 && profile.curHP < profile.maxHP) profile.curHP =
                min(profile.curHP + profile.hpRegen, profile.maxHP)
            else profile.hpTimer--
            if (profile.leftShotTimer != 0) profile.leftShotTimer--
            if (profile.rightShotTimer != 0) profile.rightShotTimer--
        }
        return deadPlayers.toList()
    }

    private fun setHP(id: Int, hp: Int) {
        if (id !in profiles.keys) return
        if (hp > profiles[id]!!.maxHP) {
            profiles[id]!!.curHP = profiles[id]!!.maxHP
            return
        }
        profiles[id]!!.curHP = hp
    }

    fun refreshPlayer(id: Int) {
        if (id !in profiles.keys) return
        profiles[id] = Profile()
    }

    fun setMaxHP(id: Int, maxHP: Int) {
        if (id !in profiles.keys) return
        val oldMax = profiles[id]!!.maxHP
        profiles[id]!!.maxHP = maxHP
        profiles[id]!!.curHP = (profiles[id]!!.curHP * maxHP.toFloat() / oldMax.toFloat()).toInt()
    }

    fun assignHP(id: Int) {
        if (id in profiles.keys) return
        profiles[id] = Profile()
    }

    fun createIsland(id: Int) {
        if (id in islandProfiles.keys) return
        islandProfiles[id] = IslandProfile()
    }


    fun dealDamage(id: Int, damage: Int): DeathState {
        if (id in profiles.keys) {
            profiles[id]!!.curHP -= damage
            profiles[id]!!.hpTimer = MAXHPTICKS
            if (profiles[id]!!.curHP <= 0) {
                return DeathState.DEAD
            }
            return DeathState.ALIVE
        }
        if (id in islandProfiles.keys) {
            islandProfiles[id]!!.curHP -= damage
            if (islandProfiles[id]!!.curHP <= 0) {
                return DeathState.DEAD
            }
            return DeathState.ALIVE
        }
        return DeathState.NONE
    }

    fun getHPbyId(id: Int): Int {
        if (id in profiles.keys) return profiles[id]!!.curHP
        if (id in islandProfiles.keys) return islandProfiles[id]!!.maxHP
        return -1
    }

    fun getMaxHPbyId(id: Int): Int {
        if (id in profiles.keys) return profiles[id]!!.maxHP
        if (id in islandProfiles.keys) return islandProfiles[id]!!.maxHP
        return -1
    }

    fun getMaxSpeedById(id: Int): Float {
        if (id !in profiles.keys) return -1f
        return profiles[id]!!.maxSpeed
    }

    fun getTurnRateById(id: Int): Float {
        if (id !in profiles.keys) return -1f
        return profiles[id]!!.turnRate
    }

    fun removeEntity(id: Int) {
        if (id in profiles.keys) profiles.remove(id)
        if (id in bulletToShooter.keys) bulletToShooter.remove(id)
        if (id in islandProfiles.keys) islandProfiles.remove(id)
    }

    fun checkShotCooldown(id: Int, side: Int): Boolean {
        if (id !in profiles.keys) return true
        return when (side) {
            1 -> profiles[id]!!.leftShotTimer != 0
            else -> profiles[id]!!.rightShotTimer != 0
        }
    }

    fun goOnCooldown(id: Int, side: Int) {
        if (id !in profiles.keys) return
        return when (side) {
            1 -> profiles[id]!!.leftShotTimer = profiles[id]!!.shotCooldown
            else -> profiles[id]!!.rightShotTimer = profiles[id]!!.shotCooldown
        }
    }

    fun getShotCooldown(id: Int, side: Int): Int {
        if (id !in profiles.keys) return 0
        return when (side) {
            1 -> profiles[id]!!.leftShotTimer
            else -> profiles[id]!!.rightShotTimer
        }
    }

    fun getMaxCooldown(id: Int): Int {
        if (id !in profiles.keys) return 0
        return profiles[id]!!.shotCooldown
    }

    fun isOutside(id: Int): Boolean {
        if (id !in profiles.keys) return false
        return profiles[id]!!.escapeTimer != -1
    }

    fun onShot(bulId: Int, shooterId: Int) {
        bulletToShooter[bulId] = shooterId
    }

    fun getShotDamage(bulId: Int): Int {
        if (bulId !in bulletToShooter) return -1
        return profiles[bulletToShooter[bulId]]!!.damage
    }

}