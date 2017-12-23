package kenran.output.gfx

import java.awt.Graphics2D
import java.awt.geom.Point2D

fun drawBot(x: Double, y: Double, g: Graphics2D) {
    g.drawRect(x.toInt() - 18, y.toInt() - 18, 36, 36)
}

fun drawBot(position: Point2D.Double, g: Graphics2D) {
    drawBot(position.x, position.y, g)
}