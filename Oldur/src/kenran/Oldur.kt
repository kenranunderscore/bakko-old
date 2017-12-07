package kenran

import kenran.movement.Surfboard
import robocode.AdvancedRobot
import kenran.radar.LockingRadar
import robocode.ScannedRobotEvent

class Oldur : AdvancedRobot() {
    companion object {
        private const val RADAR_LOCK_MULTIPLIER: Double = 2.0
    }

    private val _radar: LockingRadar
    private var _surfboard: Surfboard? = null

    init {
        _radar = LockingRadar(this, RADAR_LOCK_MULTIPLIER)
    }


	override fun run() {
        _surfboard = Surfboard(this)

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