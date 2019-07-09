package engine

abstract class Entity(var pos: Point) {
    abstract val hitbox: Hitbox
}