package kenran.util

import robocode.AdvancedRobot
import robocode.util.Utils
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D

fun bulletTravelTime(distance: Double, bulletPower: Double): Double {
    return distance / bulletVelocity(bulletPower)
}

fun wallSmoothing(field: Rectangle2D.Double, botPosition: Point2D.Double, currentAngle: Double, orientation: Int, stickLength: Double): Double {
    var targetAngle = currentAngle
    while (!field.contains(project(botPosition, targetAngle, stickLength)))
    {
        targetAngle += orientation * 0.05
    }

    return targetAngle
}

fun project(position: Point2D.Double, angle: Double, distance: Double): Point2D.Double {
    val x = position.x + Math.sin(angle) * distance
    val y = position.y + Math.cos(angle) * distance
    return Point2D.Double(x, y)
}

fun absoluteBearing(position: Point2D.Double, targetPosition: Point2D.Double): Double {
    return Math.atan2(targetPosition.x - position.x, targetPosition.y - position.y)
}

fun limit(min: Double, value: Double, max: Double): Double {
    return Math.max(min, Math.min(value, max))
}

fun bulletVelocity(power: Double): Double {
    return 20.0 - 3.0 * power
}

fun maxEscapeAngle(speed: Double): Double {
    return Math.asin(8.0 / speed)
}

fun setBackAsFront(bot: AdvancedRobot, targetAngle: Double) {
    val angle = Utils.normalRelativeAngle(targetAngle - bot.headingRadians)
    if (Math.abs(angle) > Math.PI / 2.0)
    {
        if (angle < 0)
        {
            bot.setTurnRightRadians(Math.PI + angle)
        }
        else
        {
            bot.setTurnLeftRadians(Math.PI - angle)
        }

        bot.setBack(100.0)
    }
    else
    {
        if (angle < 0)
        {
            bot.setTurnLeftRadians(-angle)
        }
        else
        {
            bot.setTurnRightRadians(angle)
        }

        bot.setAhead(100.0)
    }
}

fun rollingAverage(currentAverage: Double, newValue: Double, n: Double, weight: Double): Double {
    return (currentAverage * n + newValue * weight) / (n + weight)
}