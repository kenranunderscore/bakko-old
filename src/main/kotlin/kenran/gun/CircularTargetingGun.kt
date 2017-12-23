package kenran.gun

import kenran.util.bulletVelocity
import kenran.util.limit
import kenran.util.project
import robocode.AdvancedRobot
import robocode.ScannedRobotEvent
import robocode.util.Utils
import java.awt.geom.Point2D

class CircularTargetingGun(bot: AdvancedRobot) {
    private val _bot = bot
    private var _deltaHeading = 0.0
    private var _oldHeading = 0.0

    fun onScannedRobot(e: ScannedRobotEvent) {
        val position = Point2D.Double(_bot.x, _bot.y)
        _deltaHeading = e.headingRadians - _oldHeading
        _oldHeading = e.headingRadians
        val power = Math.min(3.0, e.energy)
        val bulletVelocity = bulletVelocity(power)
        val absoluteBearing = _bot.headingRadians + e.bearingRadians
        var predictedPosition = project(position, absoluteBearing, e.distance)
        var dt = 1
        var enemyHeading = e.headingRadians
        while (dt * bulletVelocity < position.distance(predictedPosition)) {
            predictedPosition.x += Math.sin(enemyHeading) * e.velocity
            predictedPosition.y += Math.cos(enemyHeading) * e.velocity
            enemyHeading += _deltaHeading
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

        val theta = Utils.normalAbsoluteAngle(Math.atan2(predictedPosition.x - _bot.x, predictedPosition.y - _bot.y))
        _bot.setTurnGunRightRadians(Utils.normalRelativeAngle(theta - _bot.gunHeadingRadians))
        _bot.setFire(power)
    }
}