package kenran

import kenran.movement.Surfboard
import robocode.AdvancedRobot
import kenran.radar.LockingRadar
import robocode.ScannedRobotEvent

class Oldur : AdvancedRobot() {
    companion object {
        private const val RADAR_LOCK_MULTIPLIER: Double = 2.0
    }

    private lateinit var _radar: LockingRadar
    private lateinit var _surfboard: Surfboard

    override fun run() {
        _radar = LockingRadar(this, RADAR_LOCK_MULTIPLIER)
        _surfboard = Surfboard(this)

        isAdjustGunForRobotTurn = true
        isAdjustRadarForGunTurn = true

		setTurnRadarRightRadians(Double.MAX_VALUE)

        while (true) {
            scan()
        }
    }

	override fun onScannedRobot(e: ScannedRobotEvent) {
		_radar.onScannedRobot(e)
        _surfboard.onScannedRobot(e)
    }
}