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
        val team:  Int   = -1,
        val leftShotTimer: Int = 0,
        val rightShotTimer: Int = 0,
        val shotCooldown: Int = 60,
        val isOutside: Boolean = false,
        val nickName: String = "russian hacker",
        val respTimer: Int = 0,
        val maxRespTimer: Int = 300,
        val gold: Int = 0,
        val maxGold: Int = 30,
        val resetTicks: Int = 0
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
            team:  Int   = -1,
            leftShotTimer: Int = 0,
            rightShotTimer: Int = 0,
            shotCooldown: Int = 60,
            isOutside: Boolean = false,
            nickName: String = "russian hacker",
            respTimer: Int = 0,
            maxRespTimer: Int = 300,
            gold: Int = 0,
            maxGold: Int = 30,
            resetTicks: Int = 0

    ) : this(
            id,
            pos,
            type.toString(),
            sizex,
            sizey,
            hp,
            maxHp,
            angle,
            team,
            leftShotTimer,
            rightShotTimer,
            shotCooldown,
            isOutside,
            nickName,
            respTimer,
            maxRespTimer,
            gold,
            maxGold,
            resetTicks
    )
}

enum class DataTransferEntityType {
    Player, Bullet, Island;
}
