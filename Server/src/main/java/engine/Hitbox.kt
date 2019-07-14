package engine

import kotlin.math.*

data class Hitbox(var sizex: Float, var sizey: Float, val owner: Entity, var isCollidable: Boolean = true) {
    fun checkCollision(hitbox: Hitbox): Boolean {
        if (!isCollidable or !hitbox.isCollidable) return false
        val ownerAngle = if (owner is MovableEntity) {
            owner.velocity.angle
        } else {
            0f
        }
        val radius1 = sqrt((this.sizex / 2).pow(2) + (this.sizey / 2).pow(2))
        val angle1 = atan(this.sizey / this.sizex)
        val angles1 = listOf(angle1, PI.toFloat() - angle1, angle1 - PI.toFloat(), -angle1)
        val points1 = listOf(
                Point(
                        cos(angles1[0] + ownerAngle) * radius1 + owner.pos.x,
                        sin(angles1[0] + ownerAngle) * radius1 + owner.pos.y
                ),
                Point(
                        cos(angles1[1] + ownerAngle) * radius1 + owner.pos.x,
                        sin(angles1[1] + ownerAngle) * radius1 + owner.pos.y
                ),
                Point(
                        cos(angles1[2] + ownerAngle) * radius1 + owner.pos.x,
                        sin(angles1[2] + ownerAngle) * radius1 + owner.pos.y
                ),
                Point(
                        cos(angles1[3] + ownerAngle) * radius1 + owner.pos.x,
                        sin(angles1[3] + ownerAngle) * radius1 + owner.pos.y
                )
        )
//        println("(${points1[0].x}, ${points1[0].y}); (${points1[1].x}, ${points1[1].y}); (${points1[2].x}, ${points1[2].y}); (${points1[3].x}, ${points1[3].y})")
        val lines1 = listOf(
                points1[0].getLine(points1[1]), points1[1].getLine(points1[2]),
                points1[3].getLine(points1[2]), points1[0].getLine(points1[3])
        )
//        println(lines1)

        val otherAngle = if (hitbox.owner is MovableEntity) {
            hitbox.owner.velocity.angle
        } else {
            0f
        }
        val radius2 = sqrt((hitbox.sizex / 2).pow(2) + (hitbox.sizey / 2).pow(2))
        val angle2 = atan(hitbox.sizey / hitbox.sizex)
        val angles2 = listOf(angle2, PI.toFloat() - angle2, angle2 - PI.toFloat(), -angle2)
        val points2 = listOf(
                Point(
                        cos(angles2[0] + otherAngle) * radius2 + hitbox.owner.pos.x,
                        sin(angles2[0] + otherAngle) * radius2 + hitbox.owner.pos.y
                ),
                Point(
                        cos(angles2[1] + otherAngle) * radius2 + hitbox.owner.pos.x,
                        sin(angles2[1] + otherAngle) * radius2 + hitbox.owner.pos.y
                ),
                Point(
                        cos(angles2[2] + otherAngle) * radius2 + hitbox.owner.pos.x,
                        sin(angles2[2] + otherAngle) * radius2 + hitbox.owner.pos.y
                ),
                Point(
                        cos(angles2[3] + otherAngle) * radius2 + hitbox.owner.pos.x,
                        sin(angles2[3] + otherAngle) * radius2 + hitbox.owner.pos.y
                )
        )
//        println("(${points2[0].x}, ${points2[0].y}); (${points2[1].x}, ${points2[1].y}); (${points2[2].x}, ${points2[2].y}); (${points2[3].x}, ${points2[3].y})")
        val lines2 = listOf(
                points2[0].getLine(points2[1]), points2[1].getLine(points2[2]),
                points2[3].getLine(points2[2]), points2[0].getLine(points2[3])
        )

        for (point in points1) {
            var flag = false
            for (i in 0..1) {
                val a1 = lines2[i][0]
                val b1 = lines2[i][1]
                val c1 = lines2[i][2]
                val a2 = lines2[i + 2][0]
                val b2 = lines2[i + 2][1]
                val c2 = lines2[i + 2][2]
                val res1 = a1 * point.x + b1 * point.y + c1
                val res2 = a2 * point.x + b2 * point.y + c2
                if (res1 * res2 <= 0) {
                    if (flag) {
//                        println("c1")
                        return true
                    }
                    flag = true
                }
            }
        }

        for (point in points2) {
            var flag = false
            for (i in 0..1) {
                val a1 = lines1[i][0]
                val b1 = lines1[i][1]
                val c1 = lines1[i][2]
                val a2 = lines1[i + 2][0]
                val b2 = lines1[i + 2][1]
                val c2 = lines1[i + 2][2]
                val res1 = a1 * point.x + b1 * point.y + c1
                val res2 = a2 * point.x + b2 * point.y + c2
                if (res1 * res2 <= 0) {
                    if (flag) {
//                        println("c2")
                        return true
                    }
                    flag = true
                }
            }
        }
        return false
    }


}