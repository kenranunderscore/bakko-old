package kenran.movement

import kenran.util.Wave
import kenran.util.bulletVelocity
import kenran.util.project
import robocode.AdvancedRobot
import robocode.HitByBulletEvent
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D
import robocode.ScannedRobotEvent

class Surfboard(bot: AdvancedRobot) {
    companion object {
        private const val BINS: Int = 47
    	private const val WALL_STICK_LENGTH: Double = 160.0
        private var surfStats: DoubleArray = DoubleArray(BINS)
    }
    
    private val _bot: AdvancedRobot = bot
    private var _enemyEnergy: Double = 100.0
    private var _position: Point2D.Double
    private var _enemyPosition: Point2D.Double
    private val _fieldRect: Rectangle2D.Double
    private var _surfDirections = mutableListOf<Int>()
    private var _absoluteBearings = mutableListOf<Double>()
    private var _enemyWaves = mutableListOf<Wave>()
    
    init {
        _position = Point2D.Double(_bot.x, _bot.y)
        _enemyPosition = Point2D.Double()
        _fieldRect = Rectangle2D.Double(18.0, 18.0, 800.0, 600.0)
    }
    
    fun onScannedRobot(e: ScannedRobotEvent) {
        _position = Point2D.Double(_bot.x, _bot.y)
        val lateralVelocity = _bot.velocity * Math.sin(e.bearingRadians)
        val absoluteBearing = e.bearingRadians + _bot.headingRadians
        _surfDirections.add(0, if (lateralVelocity >= 0) 1 else -1)
        _absoluteBearings.add(0, absoluteBearing + Math.PI)
        val bulletPower = _enemyEnergy - e.energy
        if (bulletPower < 3.01 && bulletPower > 0.09 && _surfDirections.size > 2) {
            val bulletVelocity = bulletVelocity(bulletPower)
            val wave = Wave(
                    _enemyPosition,
                    _bot.time - 1,
                    bulletVelocity,
                    _absoluteBearings[2],
                    bulletVelocity,
                    _surfDirections[2])
            _enemyWaves.add(wave)
        }

        _enemyEnergy = e.energy
        _enemyPosition = project(_position, absoluteBearing, e.distance)
        updateWaves()
        surf()
    }

    fun onHitByBullet(e: HitByBulletEvent) {
        //TODO
    }

    fun updateWaves() {
        //TODO
    }

    fun surf() {
        //TODO
    }
}