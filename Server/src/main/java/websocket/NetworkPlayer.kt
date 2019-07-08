package websocket

import Vector2f
import Point

data class NetworkPlayer(val velocity: Vector2f, val pos: Point, val angle: Float, val hp: Int, val id: Int, val size: Point)