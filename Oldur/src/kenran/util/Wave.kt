package kenran.util

import java.awt.geom.Point2D

data class Wave(
        var firePosition: Point2D.Double,
        var fireTime: Long,
        var bulletVelocity: Double,
        var angle: Double,
        var traveledDistance: Double,
        var direction: Int)