import kotlin.math.pow
import kotlin.math.sqrt

data class Vector2f(var x: Float = 0f, var y: Float = 1f) {
    val length: Float
    get() = sqrt(this.x.pow(2) + this.y.pow(2))

    fun normalize(): Vector2f {
        val len = this.length
        x /= len
        y /= len
        return Vector2f(x, y)
    }
}