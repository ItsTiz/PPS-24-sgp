package view.track

import scalafx.scene.canvas.Canvas
import scalafx.scene.paint.Color
import scalafx.scene.canvas.GraphicsContext
import model.tracks.TrackSectorModule.TrackSectorType
import model.common.CoordinateModule.Coordinate
import scalafx.scene.shape.ArcType

/** Responsible for rendering the track onto a ScalaFX canvas. Supports drawing straight and curved sectors, as well as
  * marking the start.
  */
object TrackView:

  /** Draws the entire track composed of a list of sectors into the given canvas.
    *
    * @param canvas
    *   The canvas on which the track will be drawn.
    * @param sectors
    *   A list of [[ShowableTrackSector]] representing the track sectors.
    */
  def drawTrack(canvas: Canvas, sectors: List[ShowableTrackSector]): Unit =
    val gc = canvas.graphicsContext2D
    gc.setStroke(Color.Grey)
    gc.setLineWidth(8)

    sectors.foreach { sector =>
      sector.sector.sectorType match
        case TrackSectorType.Straight =>
          drawStraight(gc, sector.start, sector.end)
        case TrackSectorType.Curve =>
          drawCurve(gc, sector)
    }

    sectors.filter(_.isStart).foreach { sector =>
      drawStartMarker(gc, sector.start)
    }

  /** Draws a straight track segment as a line between two points.
    *
    * @param gc
    *   The graphics context used to draw.
    * @param start
    *   The starting coordinate of the segment.
    * @param end
    *   The ending coordinate of the segment.
    */
  def drawStraight(gc: GraphicsContext, start: Coordinate, end: Coordinate): Unit =
    gc.strokeLine(start.x, start.y, end.x, end.y)

  /** Draws a curved track segment as an arc.
    *
    * The curve bulges outward from the line connecting start and end, and can be inverted to draw the curve inward.
    *
    * @param gc
    *   The graphics context used to draw.
    * @param sector
    *   The [[ShowableTrackSector]] containing coordinates and orientation.
    */
  def drawCurve(gc: GraphicsContext, sector: ShowableTrackSector): Unit =
    val start = sector.start
    val end = sector.end
    val dx = end.x - start.x
    val dy = end.y - start.y
    val distance = math.hypot(dx, dy)

    // Midpoint of the segment
    val mx = (start.x + end.x) / 2
    val my = (start.y + end.y) / 2

    // Calculate perpendicular offset for the curve bulge
    val offset = distance / 2
    val normX = -dy / distance
    val normY = dx / distance
    val cx = mx + normX * offset
    val cy = my + normY * offset

    val radius = math.hypot(start.x - cx, start.y - cy)

    val topLeftX = cx - radius
    val topLeftY = cy - radius

    var startAngle = math.toDegrees(math.atan2(start.y - cy, start.x - cx))
    val endAngle = math.toDegrees(math.atan2(end.y - cy, end.x - cx))

    // Calculate sweep angle clockwise
    var sweep = endAngle - startAngle
    if sweep < 0 then sweep += 360

    if sector.invert then startAngle += 180.0

    gc.strokeArc(
      topLeftX,
      topLeftY,
      radius * 2,
      radius * 2,
      startAngle + 90,
      sweep,
      ArcType.Open
    )

  /** Draws a small circular green marker at the given coordinate to indicate the start of the track.
    *
    * @param gc
    *   The graphics context used to draw.
    * @param position
    *   The coordinate where the marker should be placed.
    */
  def drawStartMarker(gc: GraphicsContext, position: Coordinate): Unit =
    gc.setFill(Color.Green)
    gc.fillOval(position.x - 5, position.y - 5, 10, 10)
