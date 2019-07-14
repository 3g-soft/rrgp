package engine

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random
import kotlin.random.Random.Default.nextFloat

const val TEAMS_COUNT = 3

class EntityManager {
    private var uniqueCounter = 0

    private val entityIDs:   MutableMap<Entity, Int>           = emptyMap<Entity, Int>().toMutableMap()
    private val playerNames: MutableMap<Int, String>           = emptyMap<Int, String>().toMutableMap()
    private val teams:       MutableMap<Int, MutableList<Int>> = emptyMap<Int, MutableList<Int>>().toMutableMap()

    init {
        for (team_number in 0 until TEAMS_COUNT) {
            teams[team_number] = emptyList<Int>().toMutableList()
        }
    }

    fun reset() {
        var teams_count_on_reset = 0
        for (ent in entityIDs.keys) {
            if (teams_count_on_reset == 4)
                break
            if (teams_count_on_reset >= TEAMS_COUNT - 1 && ent is Island) {
                changeTeam(entityIDs[ent]!!, TEAMS_COUNT - 1)
                ++teams_count_on_reset
                continue
            }
            if (ent is Island) {
                changeTeam(entityIDs[ent]!!, teams_count_on_reset%2)
                ++teams_count_on_reset
            }
        }

        for (player_id in playerNames.keys) {
            respawnPlayer(player_id)
        }
    }

    fun setNameById(id: Int, name: String) {
        if(id !in entityIDs.values || name in  playerNames.values) return
        playerNames[id] = name
    }

    fun getNameById(id: Int): String {
        return if (id !in playerNames.keys) "russian hacker" else playerNames[id]!!
    }

    fun respawnPlayer(id: Int) {
        if (id !in entityIDs.values) return

        var island: Entity = Island(Point(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY))
        for (ent_id in teams[getTeamById(id)]!!) {
            if (getById(ent_id) is Island){
                island = getById(ent_id)!!
                break
            }
        }

        if (island.pos.x > WIDTH || island.pos.y > HEIGHT)
            return //something happens

        var angle = nextFloat() * 2 * PI.toFloat()
        getById(id)!!.pos = Point(
                island.pos.x + (island.hitbox.sizex * sqrt(2f) +
                        getById(id)!!.hitbox.sizex)*cos(angle),
                island.pos.y + (island.hitbox.sizey * sqrt(2f) +
                        getById(id)!!.hitbox.sizey)*sin(angle)
        )

    }

    fun getTeamById(id: Int): Int {
        if (id !in entityIDs.values) return -1
        for (team_number in 0 until TEAMS_COUNT) {
            if (!teams[team_number]!!.contains(id)) continue
            return team_number
        }
        return -1
    }

    fun changeTeam(id: Int, team_id: Int) {
        if (team_id !in 0 until TEAMS_COUNT) return
        for (team_number in 0 until TEAMS_COUNT) {
            if (!teams[team_number]!!.contains(id)) continue
            teams[team_number]!!.remove(id)
            teams[team_id]!!.add(id)
        }
    }

    private fun assignTeam(entity: Entity) {
        var playerCount = 0
        for (team in teams.keys) {
            if (team == TEAMS_COUNT - 1) break
            playerCount += teams[team]!!.size
        }
        teams[playerCount % (TEAMS_COUNT - 1)]!!.add(entityIDs[entity]!!)
    }

    fun identify(entity: Entity) {
        if (entity in entityIDs.keys) return
        entityIDs[entity] = ++uniqueCounter
        assignTeam(entity)
    }

    fun getById(id: Int): Entity? {
        if (id !in entityIDs.values) return null
        for (key in entityIDs.keys) {
            if (entityIDs[key] == id)
                return key
        }
        return null
    }

    fun getId(entity: Entity): Int {
        if (entity !in entityIDs) return 0
        return entityIDs[entity]!!
    }

    fun removeEntity(id: Int) {
        if (id !in entityIDs.values) return
        for (key in entityIDs.keys) {
            if (entityIDs[key] == id) {
                entityIDs.remove(key)
                break
            }
        }
        playerNames.remove(id)
        for (key in teams.keys) {
            if (teams[key]!!.contains(id))
                for (entityID in teams[key]!!) {
                    if (entityID == id) {
                        teams[key]!!.remove(id)
                        return
                    }
                }
        }
    }
}