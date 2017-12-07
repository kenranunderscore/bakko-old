package kenran.movement

import robocode.AdvancedRobot
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D
import robocode.ScannedRobotEvent

class Surfboard(bot: AdvancedRobot) {
    companion object {
        private const val BINS: Int = 47
    	private const val WALL_STICK_LENGTH: Double = 160.0
        private var _surfStats: DoubleArray = DoubleArray(BINS)
    }
    
    private val _bot: AdvancedRobot
    private var _enemyEnergy: Double = 100.0
    private var _position: Point2D.Double
    private var _enemyPosition: Point2D.Double
    private val _fieldRect: Rectangle2D.Double
    private var _surfDirections = listOf<Int>()
    private var _absoluteBearings = listOf<Double>()
    
    init {
        _bot = bot
        _position = Point2D.Double(_bot.getX(), _bot.getY())
        _enemyPosition = Point2D.Double()
        _fieldRect = Rectangle2D.Double(18.0, 18.0, 800.0, 600.0)
    }
    
    fun onScannedRobot(e: ScannedRobotEvent) {
        _position = Point2D.Double(_bot.getX(), _bot.getY())
        val lateralVelocity = _bot.getVelocity() * Math.sin(e.getBearingRadians())
        val absoluteBearing = e.getBearingRadians() + _bot.getHeadingRadians()
        //_surfDirections.
    }
}