import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import java.*
//import oop.execute
//import oop.value
import org.junit.jupiter.api.Test
import kotlin.math.abs

fun floatEquals(a:Float, b:Float):Boolean
{
    return abs(a - b) < 0.0001
}

//import kotlin.test.assertEquals
class PointTest() {
    @Test
    fun distanceTest() {
        val point1 = Point(2f, 3f)
        val point2 = Point(5f, 7f)
        assertTrue(floatEquals(point1.distance(point2) , 5f))
    }
}
/*
class EngineTest() {
    @Test
    fun updateTest() {
        val posPlayer = Point(1f, 3f)
        val posIsland = Point(1f, 2f)
        val vel = Vector2f(2f, 2f)
        val island1 = Island(1, 100, posIsland)
        val player1 = Player(posPlayer, 100, vel, 1, island1)
        val engine1 = engine
        engine1.addNewPlayer(player1)
        engine1.update()
    }
}*/

class PlayerTest() {
    @Test
    fun moveTest() {
        val posPL = Point(1f, 3f)
        val posIS = Point(1f, 2f)
        val vel = Vector2f(2f, 2f)
        val island1 = Island(1, 100, posIS, 2)
        val player1 = Player(100, 1, island1, vel, posPL, 1)
        player1.move()
        assertTrue(floatEquals(player1.position.x ,3f))
        assertTrue(floatEquals(player1.position.y ,5f))
    }
}

/*class Vector2fTest()
{
    @Test
    fun  normalizeTest(){
        var vector1 = Vector2f(3f, 4f)
        vector1 = vector1.normalize()
        assertEquals(vector1.length, 5f)
    }
}*/
/*


class EngineTest()
{
    @Test
    fun Test(){

    }
}
*/