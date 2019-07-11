package engine

data class DataTransferEntity(
        val id:    Int,
        val pos:   Point,
        val type:  String,
        val sizex: Float,
        val sizey: Float,
        val hp:    Int   = -1,
        val maxHp: Int   = -1,
        val angle: Float = 0f,
        val team:  Int   = -1
) {
    constructor(
            id:    Int,
            pos:   Point,
            type:  DataTransferEntityType,
            sizex: Float,
            sizey: Float,
            hp:    Int   = -1,
            maxHp: Int   = -1,
            angle: Float = 0f,
            team:  Int   = -1
    ) : this(
            id,
            pos,
            type.toString(),
            sizex,
            sizey,
            hp,
            maxHp,
            angle,
            team
    )
}

enum class DataTransferEntityType {
    Player, Bullet, Island;
}
