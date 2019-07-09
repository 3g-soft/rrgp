package engine

data class DataTransferEntity(
    val id: Int,
    val pos: Point,
    val type: String,
    val sizex: Float,
    val sizey: Float,
    val angle: Float = 0f
) {
    constructor(
        id: Int,
        pos: Point,
        type: DataTransferEntityType,
        sizex: Float,
        sizey: Float,
        angle: Float = 0f
    ) : this(
        id,
        pos,
        type.toString(),
        sizex,
        sizey,
        angle
    )
}


enum class DataTransferEntityType {
    Player, Bullet, Island;
}

