package engine

import kotlin.math.*

data class Vector2f(var x: Float = 0f, var y: Float = 5f) {
    constructor(length: Float = 100f, angle: Float = PI.toFloat() / 2, dummy: Boolean): this(length * cos(angle), length * sin(angle))

    var length: Float
        get() = sqrt(this.x.pow(2) + this.y.pow(2))
        set(value) {
            val len = this.length
            this.x *= value/len
            this.y *= value/len
        }
    var angle: Float
        get() {
            if (abs(this.x) <= 0.0000001 && abs(this.y) <= 0.000001) {
                return 0f
            }
            val angle = atan2(this.y, this.x)
            return if (angle < 0) {
                angle + 2 * PI.toFloat()
            }
            else {
                angle
            }
        }
        set(value) {
            val len = this.length
            this.x = len * cos(value)
            this.y = len * sin(value)
        }

    fun normalize(): Vector2f {
        val len = this.length
        x /= len
        y /= len
        return Vector2f(x, y)
    }

    fun plus(vec: Vector2f): Vector2f {
        return Vector2f(this.x + vec.x, this.y + vec.y)
    }
    fun minus(vec: Vector2f): Vector2f {
        return Vector2f(this.x - vec.x, this.y - vec.y)
    }
    fun times(float: Float): Vector2f {
        return Vector2f(this.x * float, this.y * float)
    }
    fun div(float: Float): Vector2f {
        return Vector2f(this.x / float, this.y / float)
    }
}