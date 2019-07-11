//import oop.execute
//import oop.value
import engine.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.math.abs

class CollisionEntity(pos: Point, hitx:Float, hity: Float) : Entity(pos) {
    override val hitbox = Hitbox(hitx, hity, this)
}

fun floatEquals(a:Float, b:Float):Boolean
{
    return abs(a - b) < 0.0001
}

//import kotlin.test.assertEquals
class PointTest {
    @Test
    fun distanceTest() {
        val point1 = Point(2f, 3f)
        val point2 = Point(5f, 7f)
        assertTrue(floatEquals(point1.distance(point2) , 5f))
    }
}

class EngineTest {
    @Test
    fun updateTest() {
        val posPlayer = Point(1f, 3f)
        val vel = Vector2f(2f, 2f)
        val player1 = Player(posPlayer, vel)
        val engine1 = Engine()
        engine1.addEntity(player1)
        engine1.update()
        assertTrue(floatEquals(engine1.getState()[0].pos.x, 3f))
        assertTrue(floatEquals(engine1.getState()[0].pos.y, 5f))
    }
    @Test
    fun addEntityTest()
    {
        val posPlayer = Point(1f, 3f)
        val posIsland = Point(1f, 2f)
        val vel = Vector2f(2f, 2f)
        val island1 = Island(posIsland)
        val player1 = Player(posPlayer, vel)
        val engine1 = Engine()
        engine1.addEntity(island1)
        engine1.addEntity(player1)
        assertEquals(engine1.getState().size, 2)
    }
    @Test
    fun removeEntityTest()
    {
        val posPlayer = Point(1f, 3f)
        val posIsland = Point(1f, 2f)
        val vel = Vector2f(2f, 2f)
        val island1 = Island(posIsland)
        val player1 = Player(posPlayer, vel)
        val engine1 = Engine()
        engine1.addEntity(island1)
        engine1.addEntity(player1)
        engine1.removeEntity(island1)

        assertEquals(engine1.getState().size, 1)
    }
    @Test
    fun collisionEventTest()
    {
        val pos1 = Point(1f, 4f)
        val pos2 = Point(1f, 2f)
        val pos3 = Point(9f, 9f)
        val pos4 = Point(12f, 12f)
        val pos5 = Point(33f, 3f)
        val pos6 = Point(44f, 2f)
        val col1 = CollisionEntity(pos1, 4f, 4f)
        val col2 = CollisionEntity(pos2, 4f, 4f)
        val col3 = CollisionEntity(pos3, 4f, 4f)
        val col4 = CollisionEntity(pos4, 4f, 4f)
        val col5 = CollisionEntity(pos5, 4f, 4f)
        val col6 = CollisionEntity(pos6, 4f, 4f)
        val engine1 = Engine()
        engine1.addEntity(col1)
        engine1.addEntity(col2)
        engine1.addEntity(col3)
        engine1.addEntity(col4)
        engine1.addEntity(col5)
        engine1.addEntity(col6)
        val col = engine1.update()
        assertEquals(col.size, 2)
    }

}

class PlayerTest {
    @Test
    fun moveTest() {
        val posPL = Point(1f, 3f)
        val vel = Vector2f(2f, 2f)
        val player1 = Player(posPL, vel)
        player1.move()
        assertTrue(floatEquals(player1.pos.x ,3f))
        assertTrue(floatEquals(player1.pos.y ,5f))
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