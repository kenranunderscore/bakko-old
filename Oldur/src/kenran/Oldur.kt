package kenran

import robocode.AdvancedRobot
import kenran.radar.LockingRadar
import robocode.ScannedRobotEvent

const val RADAR_LOCK_MULTIPLIER: Double = 2.0

class Oldur : AdvancedRobot() {
	private val _radar: LockingRadar

	init {
		_radar = LockingRadar(this, RADAR_LOCK_MULTIPLIER)
	}

	override fun run() {
		isAdjustGunForRobotTurn = true
		isAdjustRadarForGunTurn = true

		setTurnRadarRightRadians(Double.MAX_VALUE)

		while (true) {
			scan()
        }
    }

	override fun onScannedRobot(e: ScannedRobotEvent) {
		_radar.OnScannedRobot(e)
	}
}