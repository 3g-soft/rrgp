package engine

class Island(pos: Point): Entity(pos) {
    override val hitbox =  Hitbox(100f, 100f, this)
}