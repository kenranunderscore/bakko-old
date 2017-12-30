package kenran.gun

import kenran.Bakko
import kenran.output.gfx.drawBot
import kenran.util.CyclicArray
import kenran.util.FixedSizeStack
import kenran.util.bulletTravelTime
import kenran.util.project
import robocode.ScannedRobotEvent
import robocode.util.Utils
import java.awt.Color
import java.awt.Graphics2D
import java.awt.geom.Point2D

class PatternMatcher(bot: Bakko) {
    companion object {
        private const val PATTERN_MATCHER_LENGTH = 3000
        private const val RECENT_PATTERN_LENGTH = 20
        private const val MINIMUM_NUMBER_OF_RECORDS = 50
        private val _recordedMovements = CyclicArray<MovementState>(PATTERN_MATCHER_LENGTH)
    }

    private val _bot = bot
    private var _oldEnemyHeading = 0.0
    private val _enemyPosition = Point2D.Double()
    private var _predictedPosition = Point2D.Double()
    private val _recentMovements = FixedSizeStack<MovementState>(RECENT_PATTERN_LENGTH)

    fun onScannedRobot(e: ScannedRobotEvent) {
        val absoluteBearing = _bot.headingRadians + e.bearingRadians
        _enemyPosition.setLocation(project(_bot.position, absoluteBearing, e.distance))
        if (e.energy < 0.1) {
            _bot.setTurnGunRightRadians(Utils.normalRelativeAngle(absoluteBearing - _bot.gunHeadingRadians))
            _bot.setFire(0.1)
            return
        }
        val deltaHeading = e.headingRadians - _oldEnemyHeading
        _oldEnemyHeading = e.headingRadians
        record(deltaHeading, e.velocity)
        val power = 2.5
        if (_recordedMovements.currentSize > MINIMUM_NUMBER_OF_RECORDS && _recentMovements.size == RECENT_PATTERN_LENGTH) {
            _predictedPosition = predictPosition(power)
            val theta = kenran.util.absoluteBearing(_bot.position, _predictedPosition)
            _bot.setTurnGunRightRadians(Utils.normalRelativeAngle(theta - _bot.gunHeadingRadians))
        } else {
            _bot.setTurnGunRightRadians(Utils.normalRelativeAngle(absoluteBearing - _bot.gunHeadingRadians))
        }
        if (_bot.gunTurnRemainingRadians <= 0.1) {
            _bot.setFire(power)
        }
    }

    private fun predictPosition(power: Double): Point2D.Double {
        val matchIndex = lastIndexOfMatchingSeries()
        val predictedPosition = Point2D.Double(_enemyPosition.x, _enemyPosition.y)
        var heading = _oldEnemyHeading
        var travelTime = 0.0
        var turns = 0.0
        var j = matchIndex + 1
        while (j < _recordedMovements.currentSize && turns <= travelTime) {
            val (deltaHeading, velocity) = _recordedMovements.get(j)
            predictedPosition.x += Math.sin(heading) * velocity
            predictedPosition.y += Math.cos(heading) * velocity
            heading += deltaHeading
            travelTime = bulletTravelTime(_bot.position.distance(predictedPosition), power)
            turns += 1.0
            j++
        }
        return predictedPosition
    }

    private fun lastIndexOfMatchingSeries(): Int {
        val iterator = FixedSizeStack<MovementState>(RECENT_PATTERN_LENGTH)
        for (i in 0 until RECENT_PATTERN_LENGTH) {
            iterator.push(_recordedMovements.get(i))
        }
        var minimalDistance = Double.POSITIVE_INFINITY
        var indexOfMinimalDistance = RECENT_PATTERN_LENGTH - 1
        for (k in RECENT_PATTERN_LENGTH until _recordedMovements.currentSize - MINIMUM_NUMBER_OF_RECORDS) {
            val ms = _recordedMovements.get(k)
            iterator.push(ms)
            val d = compare(iterator, _recentMovements)
            if (d < minimalDistance) {
                minimalDistance = d
                indexOfMinimalDistance = k
            }
            if (d <= 0.01) {
                break
            }
        }
        return indexOfMinimalDistance
    }

    private fun compare(s1: FixedSizeStack<MovementState>, s2: FixedSizeStack<MovementState>): Double {
        var distance = 0.0
        for (i in 0 until RECENT_PATTERN_LENGTH) {
            val item1 = s1.peek(i)
            val item2 = s2.peek(i)
            val deltaHeading = item1.deltaHeading - item2.deltaHeading
            val deltaVelocity = item1.velocity - item2.velocity
            distance += Math.sqrt(deltaHeading * deltaHeading + deltaVelocity * deltaVelocity)
        }
        return distance
    }

    private fun record(dh: Double, v: Double) {
        val ms = MovementState(dh, v)
        _recentMovements.push(ms)
        _recordedMovements.push(ms)
    }

    fun onPaint(g: Graphics2D) {
        g.color = Color.GREEN
        drawBot(_predictedPosition, g)
        g.drawLine(_bot.x.toInt(), _bot.y.toInt(), _predictedPosition.x.toInt(), _predictedPosition.y.toInt())
    }
}