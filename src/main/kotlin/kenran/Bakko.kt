package kenran

import kenran.gun.CircularTargetingGun
import kenran.movement.Surfboard
import kenran.radar.LockingRadar
import robocode.AdvancedRobot
import robocode.BulletHitBulletEvent
import robocode.HitByBulletEvent
import robocode.ScannedRobotEvent
import java.awt.Graphics2D

class Bakko: AdvancedRobot() {
    companion object {
        private const val RADAR_LOCK_MULTIPLIER: Double = 2.0
    }

    private lateinit var _radar: LockingRadar
    private lateinit var _surfboard: Surfboard
    private lateinit var _gun: CircularTargetingGun

    override fun run() {
        _radar = LockingRadar(this, RADAR_LOCK_MULTIPLIER)
        _surfboard = Surfboard(this)
        _gun = CircularTargetingGun(this)

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
        _gun.onScannedRobot(e)
    }

    override fun onHitByBullet(e: HitByBulletEvent) {
        _surfboard.onHitByBullet(e)
    }

    override fun onBulletHitBullet(e: BulletHitBulletEvent) {
        _surfboard.onBulletHitBullet(e)
    }

    override fun onPaint(g: Graphics2D) {
        _gun.onPaint(g)
    }
}