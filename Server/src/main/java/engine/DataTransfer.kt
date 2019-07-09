package engine

data class DataTransferEntity(val id: Int, val pos: Point, val type: String, val angle: Float = 0f) {
    constructor(id: Int, pos: Point, type: DataTransferEntityType, angle: Float=0f): this(id, pos, type.toString(), angle)
}


enum class DataTransferEntityType {
    Player, Bullet, Island;
}

