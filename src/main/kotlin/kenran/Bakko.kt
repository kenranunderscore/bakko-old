package kenran

import kenran.gun.PatternMatcher
import kenran.movement.Surfboard
import kenran.radar.LockingRadar
import robocode.*
import java.awt.Graphics2D
import java.awt.geom.Point2D

class Bakko: AdvancedRobot() {
    companion object {
        private const val RADAR_LOCK_MULTIPLIER: Double = 2.0
    }

    private lateinit var _radar: LockingRadar
    private lateinit var _surfboard: Surfboard
    private lateinit var _gun: PatternMatcher

    var position: Point2D.Double = Point2D.Double()

    override fun run() {
        _radar = LockingRadar(this, RADAR_LOCK_MULTIPLIER)
        _surfboard = Surfboard(this)
        _gun = PatternMatcher(this)

        isAdjustGunForRobotTurn = true
        isAdjustRadarForGunTurn = true

        setTurnRadarRightRadians(Double.MAX_VALUE)

        while (true) {
            execute()
        }
    }

    override fun onScannedRobot(e: ScannedRobotEvent) {
        position.setLocation(x, y)
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

    override fun onSkippedTurn(event: SkippedTurnEvent?) {
        println("SKIPPED A TURN! DANGER!")
    }
}