package kenran.radar

import robocode.AdvancedRobot
import robocode.ScannedRobotEvent

class LockingRadar(bot: AdvancedRobot, lockMultiplier: Double) {
	private val _bot: AdvancedRobot
	private val _lockMultiplier: Double

	init {
		_bot = bot
		_lockMultiplier = lockMultiplier
	}

	fun OnScannedRobot(e: ScannedRobotEvent) {
		var angleToEnemy = _bot.headingRadians + e.bearingRadians - _bot.radarHeadingRadians;
		var radarTurn = robocode.util.Utils.normalRelativeAngle(angleToEnemy);
		_bot.setTurnRadarRightRadians(_lockMultiplier * radarTurn);
	}
}