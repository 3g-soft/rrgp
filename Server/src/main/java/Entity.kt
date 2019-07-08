package engine

abstract class Entity(var position: Point, var id: Int, var hp: Int) {
    abstract val hitbox: Hitbox
}