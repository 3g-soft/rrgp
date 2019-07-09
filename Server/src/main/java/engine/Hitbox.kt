package engine

import kotlin.math.*

data class Hitbox(var sizex: Float, var sizey: Float, val owner: Entity) {
    fun checkCollision(hitbox: Hitbox): Boolean {
        var ownerAngle = if (owner is MovableEntity) {
            owner.velocity.angle
        } else {
            0f
        }
        val radius1 = sqrt((this.sizex / 2).pow(2) + (this.sizey / 2).pow(2))
        val angle1 = atan(this.sizey / this.sizex)
        val angles1 = listOf(angle1, PI.toFloat() - angle1, angle1 - PI.toFloat(), -angle1)
        val points1 = listOf(
            Point(
                cos(angles1[0] + ownerAngle) * radius1 + owner.position.x,
                sin(angles1[0] + ownerAngle) * radius1 + owner.position.y
            ),
            Point(
                cos(angles1[1] + ownerAngle) * radius1 + owner.position.x,
                sin(angles1[1] + ownerAngle) * radius1 + owner.position.y
            ),
            Point(
                cos(angles1[2] + ownerAngle) * radius1 + owner.position.x,
                sin(angles1[2] + ownerAngle) * radius1 + owner.position.y
            ),
            Point(
                cos(angles1[3] + ownerAngle) * radius1 + owner.position.x,
                sin(angles1[3] + ownerAngle) * radius1 + owner.position.y
            )
        )
        val lines1 = listOf(
            points1[0].getLine(points1[1]), points1[1].getLine(points1[2]),
            points1[2].getLine(points1[3]), points1[3].getLine(points1[0])
        )


        var otherAngle = if (hitbox.owner is MovableEntity) {
            hitbox.owner.velocity.angle
        } else {
            0f
        }
        val radius2 = sqrt((hitbox.sizex / 2).pow(2) + (hitbox.sizey / 2).pow(2))
        val angle2 = atan(hitbox.sizey / hitbox.sizex)
        val angles2 = listOf(angle2, PI.toFloat() - angle2, angle2 - PI.toFloat(), -angle2)
        val points2 = listOf(
            Point(
                cos(angles2[0] + otherAngle) * radius2 + owner.position.x,
                sin(angles2[0] + otherAngle) * radius2 + owner.position.y
            ),
            Point(
                cos(angles2[1] + otherAngle) * radius2 + owner.position.x,
                sin(angles2[1] + otherAngle) * radius2 + owner.position.y
            ),
            Point(
                cos(angles2[2] + otherAngle) * radius2 + owner.position.x,
                sin(angles2[2] + otherAngle) * radius2 + owner.position.y
            ),
            Point(
                cos(angles2[3] + otherAngle) * radius2 + owner.position.x,
                sin(angles2[3] + otherAngle) * radius2 + owner.position.y
            )
        )
        val lines2 = listOf(
            points2[0].getLine(points2[1]), points2[1].getLine(points2[2]),
            points2[2].getLine(points2[3]), points2[3].getLine(points2[0])
        )
        var flag = false
        for (point in points1) {
            for (i in 0..1) {
                var a1 = lines2[i][0]
                var b1 = lines2[i][1]
                var c1 = lines2[i][2]
                var a2 = lines2[i + 2][0]
                var b2 = lines2[i + 2][1]
                var c2 = lines2[i + 2][2]
                if (a1 * point.x + b1 * point.y + c1 != a2 * point.x + b2 * point.y + c2) {
                    if (flag) {
                        return true
                    }
                    flag = true
                }
            }
        }
        flag = false
        for (point in points2) {
            for (i in 0..1) {
                var a1 = lines1[i][0]
                var b1 = lines1[i][1]
                var c1 = lines1[i][2]
                var a2 = lines1[i + 2][0]
                var b2 = lines1[i + 2][1]
                var c2 = lines1[i + 2][2]
                if (a1 * point.x + b1 * point.y + c1 != a2 * point.x + b2 * point.y + c2) {
                    if (flag) {
                        return true
                    }
                    flag = true
                }
            }
        }
        return false
    }


}