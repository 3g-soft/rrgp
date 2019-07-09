package engine

class Island(position: Point): Entity(position) {
    override val hitbox =  Hitbox(100f, 100f, this)
}