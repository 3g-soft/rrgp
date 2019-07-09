package websocket

import Vector2f
import Point
import Entity

data class NetworkPlayer(val velocity: Vector2f, val pos: Point, val angle: Float, val hp: Int, val idd: Int, val size: Point): Entity(pos, idd)