package engine

abstract class Entity(var position: Point) {
    abstract val hitbox: Hitbox
}