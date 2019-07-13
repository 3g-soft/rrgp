package engine

import org.json.JSONArray


class SkillManager(val dm: DamageManager){
    private val skillsJSON = JSONArray("""[
    {
        name: "+SPEED +RANGE -HP", id: 1,
        childs: [{
            name: "+DAMAGE -RELOAD",  id: 3,
            childs: [
                { name: "+TURN -RELOAD", id: 4},
                { name: "+DAMAGE -HP", id: 5}
            ]
        },
        {
            name: "+SPEED -DAMAGE", id: 6,
            childs: [
                { name: "+SPEED -HP", id: 7 },
                { name: "+TURN -DAMAGE", id: 8 }
            ]
        }
        ]
    },
    {
        name: "+BODY DAMAGE +SPEED -DAMAGE", id: 2,
        childs: [{
            name: "+BODY DAMAGE -TURN",  id: 9,
            childs: [
                { name: "+SPEED -DAMAGE", id: 10},
                { name: "+BODY DAMAGE -DAMAGE", id: 11}
            ]
        },
        {
            name: "+SPEED -TURN", id: 12,
            childs: [
                { name: "+BODY DAMAGE -TURN", id: 13},
                { name: "+SPEED -RELOAD", id: 14}
            ]
        }
        ]
    },
]""")
    private val balance = mapOf<String, Float>(
            Pair("SPEED", 1f),
            Pair("TURN", 0.03f),
            Pair("HP", 10f),
            Pair("DAMAGE", 5f),
            Pair("RELOAD", 15f)
    )
    private val playersSkills = mutableMapOf<Int, MutableMap<Int, Skill>>()
    private val skills = mutableMapOf<Int, Skill>()
    private val statRefexp = Regex("""[+-][^+-]*""")
    init {
        fun parseSkills(arr: JSONArray, parent: Int = -1){
            for(i in 0 until arr.length()){
                val skill = arr.getJSONObject(i)
                skills[skill.getInt("id")] = Skill(skill.getString("name"), skill.getInt("id"), parent)
                if(skill.has("childs"))parseSkills(skill.getJSONArray("childs"), skill.getInt("id"))
            }
        }
        parseSkills(skillsJSON)
    }

    fun addSkill(playerID: Int, id: Int): Boolean{
        if(!dm.canSpendGold(playerID, 1))return false
        dm.spendGold(playerID, 1)
        if(!skills.containsKey(id))return false
        if(skills[id]!!.dependence != -1){
            if(!playersSkills[playerID]!!.keys.contains(skills[id]!!.dependence))return false
        }else{
            if(!playersSkills[playerID]!!.isEmpty())return false
        }
        playersSkills[playerID]!![id] = skills[id]!!
        println(playersSkills)
        updateProfile(playerID, skills[id]!!)
        return true
    }

    fun addPlayerId(id: Int){
        playersSkills[id] = mutableMapOf()
    }

    fun removePlayerId(id: Int){
        playersSkills.remove(id)
    }
    fun reset(){
        for(player in playersSkills.values){
            player.clear()
        }
    }

    fun updateProfile(id: Int, skill: Skill){
        val profile = dm.getPlayerProfile(id)
        for(match in statRefexp.findAll(skill.name)){
            var stat = match.value.trim()
            val s = stat[0]
            stat = stat.substring(1)
            if(!balance.containsKey(stat))continue
            val modify = balance[stat]!! * (if(s == '-') -1 else 1)
            when(stat){
                "SPEED" -> profile.maxSpeed+=modify
                "TURN" -> profile.turnRate+=modify
                "HP" -> profile.maxHP+=modify.toInt()
                "DAMAGE" -> profile.damage+=modify.toInt()
                "RELOAD" -> profile.shotCooldown+=modify.toInt()
            }
        }
        if(profile.curHP > profile.maxHP)profile.curHP = profile.maxHP
        println(dm.getPlayerProfile(id))
    }

}

data class Skill(val name: String, val id: Int, val dependence: Int)