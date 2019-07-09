package engine

data class Hitbox(val sizex: Float, val sizey: Float, val owner: Entity) {
    fun checkCollision(hitbox: Hitbox): Boolean {
        return false
    }
}