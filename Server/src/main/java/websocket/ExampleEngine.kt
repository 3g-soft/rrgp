package websocket

import kotlin.math.*

class ExampleEngine: EngineInterface{

    data class Vector2f(var x: Float, var y: Float)
    data class Player(var pos: Vector2f, var vel: Vector2f, var chasing: Int? = null){
        fun copy(): Player{
            return Player(pos.copy(), vel.copy(), chasing)
        }
    }

    val players = mutableMapOf<Int, Player>()
    var targetsPool = setOf<Int>()


    val size = 1000
    val speed = 100f
    val radius = 11f
    val tick = 100f

    private val log = Logger("ENG")

    private var lastPlayerId = -1

    override fun setup() {}

    override fun doTick() {
        for (i in players.keys) {
            val it = players[i]!!
            if (it.chasing == null) {
                selectPlayerToChase(i)
                if(it.chasing == null)continue
            }
//            log.log("moving somebody")
            it.pos.x += it.vel.x * tick / 1000f
            it.pos.y += it.vel.y * tick / 1000f
            if (getDistance(i, it.chasing!!) < radius * 2f) {
                log.log("player $i met player ${it.chasing} at x = ${it.pos.y}, y = ${it.pos.y}")
                players[i]!!.pos = Vector2f((0..size).random().toFloat(), (0..size).random().toFloat())
                stopPlayer(i)
                selectPlayerToChase(i)
            }
            if (it.pos.x > size) it.pos.x -= size
            if (it.pos.y > size) it.pos.y -= size
            if (it.pos.x < 0) it.pos.x += size
            if (it.pos.y < 0) it.pos.y += size
        }
    }

    override fun addPlayer(): Int {
        lastPlayerId++
        players[lastPlayerId] = (Player(Vector2f((0..size).random().toFloat(), (0..size).random().toFloat()), Vector2f(0f, 0f)))
        val id = lastPlayerId
        targetsPool = targetsPool.plus(id)
        return id
    }

    override fun removePlayer(id: Int) {
        if(players[id]!!.chasing != null)targetsPool = targetsPool.plus(players[id]!!.chasing!!)
        players.remove(id)
        for(p in players){
            if(p.value.chasing == id){
                players[p.key]!!.chasing = null
            }
        }
        if(targetsPool.contains(id))targetsPool = targetsPool.minus(id)
    }

    private fun getDistance(id1: Int, id2: Int): Float {
        return (sqrt((players[id1]!!.pos.x - players[id2]!!.pos.x).pow(2) + (players[id1]!!.pos.y - players[id2]!!.pos.y).pow(2)))
    }

    private fun selectPlayerToChase(id: Int) {
        if(players[id]!!.chasing != null)targetsPool = targetsPool.plus(players[id]!!.chasing!!)
        if (players.size < 2 || targetsPool.minus(id).isEmpty()){
            players[id]!!.chasing = null
            return
        }
        var resid = id
        while (resid == id) {
            resid = targetsPool.random()
        }
        targetsPool = targetsPool.minus(resid)
        players[id]!!.chasing = resid
        log.log("player with id $id now chasing id  $resid")
    }

    @Requestable(true)
    fun setAngle(id: Int, a: String) {
        val angle = a.toFloat()
        players[id]!!.vel = Vector2f(cos(angle) * speed, sin(angle) * speed)
        log.log("setted angle");
    }

    @Requestable(true)
    fun stopPlayer(id: Int){
        players[id]!!.vel = Vector2f(0f, 0f)
    }

    @Requestable
    fun getGameState(): MutableMap<Int, Player> {
        val copy = mutableMapOf<Int, Player>()
        players.forEach { it -> copy[it.key] = it.value.copy() }
//        log.log(copy.toString())
        return copy
    }
}
