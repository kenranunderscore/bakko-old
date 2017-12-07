package kenran.radar

import robocode.AdvancedRobot
import robocode.ScannedRobotEvent
import robocode.util.Utils

class LockingRadar(bot: AdvancedRobot, lockMultiplier: Double) {
	private val _bot = bot
    private val _lockMultiplier = lockMultiplier

    fun onScannedRobot(e: ScannedRobotEvent) {
		val angleToEnemy = _bot.headingRadians + e.bearingRadians - _bot.radarHeadingRadians
		val radarTurn = Utils.normalRelativeAngle(angleToEnemy)
		_bot.setTurnRadarRightRadians(_lockMultiplier * radarTurn)
	}
}