package engine

import kotlin.math.pow
import kotlin.math.sqrt

data class Point(var x: Float, var y: Float) {
    fun distance(point: Point): Float = sqrt((x - point.x).pow(2) + (y - point.y).pow(2))
    fun getLine(point: Point): List<Float> = listOf(
            this.y - point.y, point.x - this.x,
            this.y * (this.x - point.x) + this.x * (point.y - this.y)
    )
}