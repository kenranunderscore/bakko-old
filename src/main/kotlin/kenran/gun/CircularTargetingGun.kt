package kenran.gun

import kenran.output.gfx.drawBot
import kenran.util.bulletVelocity
import kenran.util.limit
import kenran.util.project
import robocode.AdvancedRobot
import robocode.ScannedRobotEvent
import robocode.util.Utils
import java.awt.Graphics2D
import java.awt.geom.Point2D

class CircularTargetingGun(bot: AdvancedRobot) {
    private val _bot = bot
    private var _deltaHeading = 0.0
    private var _oldHeading = 0.0
    private var _predictedPosition = Point2D.Double()

    fun onScannedRobot(e: ScannedRobotEvent) {
        val position = Point2D.Double(_bot.x, _bot.y)
        _deltaHeading = e.headingRadians - _oldHeading
        _oldHeading = e.headingRadians
        val power = Math.min(2.0, e.energy)
        val absoluteBearing = _bot.headingRadians + e.bearingRadians
        val enemyPosition = project(position, absoluteBearing, e.distance)
        _predictedPosition = predictPosition(e.headingRadians, e.velocity, power, enemyPosition)
        val theta = Utils.normalAbsoluteAngle(Math.atan2(_predictedPosition.x - _bot.x, _predictedPosition.y - _bot.y))
        _bot.setTurnGunRightRadians(Utils.normalRelativeAngle(theta - _bot.gunHeadingRadians))
        _bot.setFire(power)
    }

    private fun predictPosition(
            enemyHeading: Double,
            enemyVelocity: Double,
            bulletPower: Double,
            enemyPosition: Point2D.Double): Point2D.Double {
        val bulletVelocity = bulletVelocity(bulletPower)
        var dt = 1
        var predictedHeading = enemyHeading
        var predictedPosition = Point2D.Double(enemyPosition.x, enemyPosition.y)
        while (dt * bulletVelocity < predictedPosition.distance(_bot.x, _bot.y)) {
            predictedPosition.x += Math.sin(enemyHeading) * enemyVelocity
            predictedPosition.y += Math.cos(enemyHeading) * enemyVelocity
            predictedHeading += _deltaHeading
            if (predictedPosition.x < 18.0
                    || predictedPosition.y < 18.0
                    || predictedPosition.x > _bot.battleFieldWidth - 18.0
                    || predictedPosition.y > _bot.battleFieldHeight - 18.0) {
                predictedPosition.x = limit(18.0, predictedPosition.x, _bot.battleFieldWidth - 18.0)
                predictedPosition.y = limit(18.0, predictedPosition.y, _bot.battleFieldHeight - 18.0)
                break
            }
            ++dt
        }
        return predictedPosition
    }

    fun onPaint(g: Graphics2D) {
        drawBot(_predictedPosition, g)
    }
}