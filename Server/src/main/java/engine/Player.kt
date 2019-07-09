package engine

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class Player(velocity: Vector2f, pos: Point) : MovableEntity(velocity, pos) {
    override val hitbox = Hitbox(50f, 10f, this)
}