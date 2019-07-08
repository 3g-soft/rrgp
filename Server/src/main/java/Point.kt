import kotlin.math.pow
import kotlin.math.sqrt

data class Point (var x: Float, var y: Float) {
    fun distance(point: Point): Float = sqrt((x - point.x).pow(2) + (y - point.y).pow(2))
}