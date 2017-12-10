package kenran.movement

import kenran.util.*
import robocode.AdvancedRobot
import robocode.HitByBulletEvent
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D
import robocode.ScannedRobotEvent
import robocode.util.Utils

class Surfboard(bot: AdvancedRobot) {
    companion object {
        private const val BINS: Int = 47
    	private const val WALL_STICK_LENGTH: Double = 160.0
        private const val MIDDLE_BIN = (BINS + 1) / 2
        private var surfStats: DoubleArray = DoubleArray(BINS)
    }
    
    private val _bot: AdvancedRobot = bot
    private var _enemyEnergy: Double = 100.0
    private var _position: Point2D.Double
    private var _enemyPosition: Point2D.Double
    private val _field: Rectangle2D.Double
    private var _surfDirections = mutableListOf<Int>()
    private var _absoluteBearings = mutableListOf<Double>()
    private var _enemyWaves = mutableListOf<Wave>()
    
    init {
        _position = Point2D.Double(_bot.x, _bot.y)
        _enemyPosition = Point2D.Double()
        _field = Rectangle2D.Double(18.0, 18.0, 800.0, 600.0)
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
        if (!_enemyWaves.any()) {
            return
        }

        val hitPosition = Point2D.Double(e.bullet.x, e.bullet.y)
        val hitWave = _enemyWaves.firstOrNull {
            Math.abs(it.traveledDistance - _position.distance(it.firePosition)) < 50.0
                    && Math.abs(e.bullet.velocity - it.bulletVelocity) < 0.001 }
        if (hitWave != null) {
            logHit(hitWave, hitPosition)
            _enemyWaves.remove(hitWave)
        }
    }

    private fun getClosestSurfableWave(): Wave? {
        var closestDistance = 50000.0
        var targetWave: Wave? = null
        for (wave in _enemyWaves) {
            val distance = _position.distance(wave.firePosition) - wave.traveledDistance
            if (distance > wave.bulletVelocity && distance < closestDistance) {
                targetWave = wave
                closestDistance = distance
            }
        }
        return targetWave
    }

    private fun logHit(wave: Wave, targetPosition: Point2D.Double) {
        val index = getFactorIndex(wave, targetPosition)
        println(index)
        for (i in 0..46) {
            surfStats[i] += 1.0 / (1.0 + Math.pow(index - i.toDouble(), 2.0))
        }
    }

    private fun getFactorIndex(wave: Wave, targetPosition: Point2D.Double): Int {
        val offsetAngle = absoluteBearing(wave.firePosition, targetPosition) - wave.angle
        val factor = Utils.normalRelativeAngle(offsetAngle) / maxEscapeAngle(wave.bulletVelocity) * wave.direction
        return limit(0.0, (factor + 1) * MIDDLE_BIN, BINS - 1.0).toInt()
    }

    private fun updateWaves() {
        for (wave in  _enemyWaves.toList()) {
            wave.traveledDistance = (_bot.time - wave.fireTime) * wave.bulletVelocity
            if (wave.traveledDistance > _position.distance(wave.firePosition)) {
                _enemyWaves.remove(wave)
            }
        }
    }

    private fun predictPosition(wave: Wave, direction: Int): Point2D.Double {
        var predictedPosition = _position
        var predictedVelocity = _bot.velocity
        var predictedHeading = _bot.headingRadians

        var counter = 0
        var intercepted = false

        do
        {
            var moveAngle = wallSmoothing(
                    _field,
                    predictedPosition,
                    absoluteBearing(wave.firePosition, predictedPosition) + direction * (Math.PI / 2.0),
                    direction,
                    WALL_STICK_LENGTH) - predictedHeading
            var moveDir = 1
            if (Math.cos(moveAngle) < 0.0)
            {
                moveAngle += Math.PI
                moveDir = -1
            }

            moveAngle = Utils.normalRelativeAngle(moveAngle)
            val maxTurning = Math.PI / 720.0 * (40.0 - 3.0 * Math.abs(predictedVelocity))
            predictedHeading = Utils.normalRelativeAngle(predictedHeading + limit(-maxTurning, moveAngle, maxTurning))
            predictedVelocity += (if (predictedVelocity * moveDir < 0.0) 2 * moveDir else moveDir)
            predictedVelocity = limit(-8.0, predictedVelocity, 8.0)
            predictedPosition = project(predictedPosition, predictedHeading, predictedVelocity)
            counter++
            val distance = predictedPosition.distance(wave.firePosition)
            if (distance < wave.traveledDistance + (counter + 1) * wave.bulletVelocity)
            {
                intercepted = true
            }
        } while (!intercepted && counter < 500)

        return predictedPosition
    }

    private fun checkDanger(wave: Wave, direction: Int): Double {
        val index = getFactorIndex(wave, predictPosition(wave, direction))
        return surfStats[index]
    }

    private fun surf() {
        val wave = getClosestSurfableWave() ?: return
        val leftDanger = checkDanger(wave, -1)
        val rightDanger = checkDanger(wave, 1)
        var targetAngle = absoluteBearing(wave.firePosition, _position)
        targetAngle = if (leftDanger < rightDanger) {
            wallSmoothing(_field, _position, targetAngle - Math.PI / 2.0, -1, WALL_STICK_LENGTH)
        } else {
            wallSmoothing(_field, _position, targetAngle + Math.PI / 2.0, 1, WALL_STICK_LENGTH)
        }

        setBackAsFront(_bot, targetAngle)
    }
}