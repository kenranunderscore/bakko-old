package kenran.gun

import kenran.Bakko
import kenran.output.gfx.drawBot
import kenran.util.bulletVelocity
import kenran.util.limit
import kenran.util.project
import robocode.ScannedRobotEvent
import robocode.util.Utils
import java.awt.Color
import java.awt.Graphics2D
import java.awt.geom.Point2D

class CircularTargetingGun(bot: Bakko) {
    private val _bot = bot
    private var _oldHeading = 0.0
    private var _predictedPosition = Point2D.Double()

    fun onScannedRobot(e: ScannedRobotEvent) {
        val bulletPower = Math.min(2.5, _bot.energy)
        val bulletVelocity = bulletVelocity(bulletPower)
        val absoluteBearing = _bot.headingRadians + e.bearingRadians
        var enemyHeading = e.headingRadians
        val deltaHeading = enemyHeading - _oldHeading
        _oldHeading = enemyHeading

        var deltaTime = 0.0
        val predictedPosition = project(_bot.position, absoluteBearing, e.distance)
        while (++deltaTime * bulletVelocity < _bot.position.distance(predictedPosition)) {
            predictedPosition.x += Math.sin(enemyHeading) * e.velocity
            predictedPosition.y += Math.cos(enemyHeading) * e.velocity
            enemyHeading += deltaHeading
            if (predictedPosition.x < 18.0
                    || predictedPosition.y < 18.0
                    || predictedPosition.x > _bot.battleFieldWidth - 18.0
                    || predictedPosition.y > _bot.battleFieldHeight - 18.0) {
                predictedPosition.x = limit(18.0, predictedPosition.x, _bot.battleFieldWidth - 18.0)
                predictedPosition.y = limit(18.0, predictedPosition.y, _bot.battleFieldHeight - 18.0)
                break
            }
        }
        _predictedPosition.setLocation(predictedPosition.x, predictedPosition.y)
        val theta = Utils.normalAbsoluteAngle(Math.atan2(predictedPosition.x - _bot.position.x, predictedPosition.y - _bot.position.y))
        _bot.setTurnGunRightRadians(Utils.normalRelativeAngle(theta - _bot.gunHeadingRadians))
        _bot.setFire(bulletPower)
    }

    fun onPaint(g: Graphics2D) {
        g.color = Color.RED
        drawBot(_predictedPosition, g)
        g.drawLine(_bot.x.toInt(), _bot.y.toInt(), _predictedPosition.x.toInt(), _predictedPosition.y.toInt())
    }
}