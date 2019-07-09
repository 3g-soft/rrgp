package engine

data class DataTransferEntity(val uid: Int, val pos: Point, val type: String) {
    constructor(uid: Int, pos: Point, type: DataTransferEntityType): this(uid, pos, type.toString())
}


enum class DataTransferEntityType {
    Player, Bullet, Island;
}

