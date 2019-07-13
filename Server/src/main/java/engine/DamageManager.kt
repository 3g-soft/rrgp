package engine

import kotlin.math.min

data class Profile(
    var maxSpeed:      Float = 10f,
    var turnRate:      Float = 0.2f,
    var bulRange:      Float = 500f,
    var curHP:          Int  = 110,
    var maxHP:          Int  = 110,
    var hpRegen:        Int  = 1,
    var escapeTimer:    Int  = -1,
    var hpTimer:        Int  = 0,
    var damage:         Int  = 50,
    var shotCooldown:   Int  = 40,
    var leftShotTimer:  Int  = 0,
    var rightShotTimer: Int  = 0,
    var immuneTimer:    Int  = IMMUNETICKS,
    var respawnTimer:   Int  = -1,
    var gold:           Int  = 0,
    var resetTicks:     Int  = 0
)

data class IslandProfile(
    var curHP: Int = 350,
    var maxHP: Int = 350
//    var damage: Int = 30,
//    var shotCooldown: Int = 60,
//    var shotTimer: Int = 0
)

const val RESETTICKS     = 300
const val IMMUNETICKS    = 150
const val MAXESCAPETICKS = 150
const val MAXHPTICKS = 60
const val RESPAWNTICKS = 150
const val MAXGOLD = 20

data class Events(val deadPlayers: List<Int>, val respawnedPlayers: List<Int>)
class DamageManager {
    private var gameEnd:         Boolean                        = false
    private val profiles:        MutableMap<Int, Profile>       = emptyMap<Int, Profile>().toMutableMap()
    private val bulletToShooter: MutableMap<Int, Int>           = emptyMap<Int, Int>().toMutableMap()
    private val islandProfiles:  MutableMap<Int, IslandProfile> = emptyMap<Int, IslandProfile>().toMutableMap()


    fun update(escapedPlayers: List<Int>): Events {
        val deadPlayers = mutableListOf<Int>()
        val respawnedPlayers = mutableListOf<Int>()

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
            if (profile.immuneTimer != 0) profile.immuneTimer--
            if (profile.respawnTimer != -1) {
                profile.respawnTimer--
                if (profile.respawnTimer == 0) {
                    respawnedPlayers.add(id)
                    profile.respawnTimer = -1
                }
            }
        }
        return Events(deadPlayers.toList(), respawnedPlayers.toList())
    }

    fun reset() {
        for (player_id in profiles.keys) {
            profiles[player_id] = Profile()
        }
        for (island_id in islandProfiles.keys) {
            islandProfiles[island_id] = IslandProfile()
        }
        bulletToShooter.clear()
    }

    private fun setHP(id: Int, hp: Int) {
        if (id !in profiles.keys) return
        if (hp > profiles[id]!!.maxHP) {
            profiles[id]!!.curHP = profiles[id]!!.maxHP
            return
        }
        profiles[id]!!.curHP = hp
    }

    fun getProfileById(id: Int): Profile {
        if (id !in profiles.keys) return Profile()
        return profiles[id]!!
    }

    fun getTickTimer(): Int {
        var ret = -1
        for (value in profiles.values){
            ret = value.resetTicks
            break
        }
        return ret
    }

    fun getShotRange(id: Int): Float {
        if (id in profiles.keys) return profiles[id]!!.bulRange
        if (id in bulletToShooter && bulletToShooter[id] in profiles.keys) return profiles[bulletToShooter[id]]!!.bulRange
        return -1f
    }

    fun refreshPlayer(id: Int) {
        if (id in profiles.keys) {
            val gold = profiles[id]!!.gold
            profiles[id] = Profile(gold = gold)
        }
        if (id in islandProfiles.keys) islandProfiles[id] = IslandProfile()
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
            if (profiles[id]!!.immuneTimer != 0) return DeathState.ALIVE
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

    fun goOnRespawn(id: Int) {
        if (id !in profiles.keys) return
        profiles[id]!!.respawnTimer = RESPAWNTICKS
    }


    fun getHPbyId(id: Int): Int {
        if (id in profiles.keys) return profiles[id]!!.curHP
        if (id in islandProfiles.keys) return islandProfiles[id]!!.curHP
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

    fun getShooterId(bulId: Int): Int {
        if (bulId !in bulletToShooter.keys) return -1
        return bulletToShooter[bulId]!!
    }

    fun getPlayerProfile(id: Int): Profile {
        if (id !in profiles.keys) return Profile()
        return profiles[id]!!
    }

    fun getRespawnTimer(id: Int): Int {
        if (id !in profiles.keys) return -1
        return profiles[id]!!.respawnTimer
    }

    fun isRespawning(id: Int): Boolean {
        if (id !in profiles.keys) return false
        return profiles[id]!!.respawnTimer != -1
    }

    fun getGold(id: Int): Int {
        if (id !in profiles.keys) return 0
        return profiles[id]!!.gold
    }

    fun getKill(id: Int) {
        if (id !in profiles.keys) return
        profiles[id]!!.gold++
    }

    fun spendGold(id: Int, gold: Int) {
        if (id !in profiles.keys) return
        profiles[id]!!.gold -= gold
    }

    fun canSpendGold(id: Int, gold: Int): Boolean {
        if (id !in profiles.keys) return false
        return profiles[id]!!.gold >= gold
    }


    fun getIds(): List<Int> {
        return profiles.keys.toList()
    }

}