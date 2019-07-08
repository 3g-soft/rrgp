package engine

class Island(var teamId: Int, hp: Int, position: Point, id: Int): Entity(position, id, hp) {
    var initialTeamId = teamId
    override val hitbox =  Hitbox(100f, 100f, this)
}