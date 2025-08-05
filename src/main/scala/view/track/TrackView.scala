package view.track

import scalafx.scene.canvas.{Canvas, GraphicsContext}
import scalafx.scene.paint.Color
import scalafx.scene.shape.ArcType
import scalafx.scene.image.Image
import model.tracks.TrackSectorModule.TrackSectorType
import model.common.CoordinateModule.Coordinate

/** Responsible for rendering the track onto a ScalaFX canvas. Supports drawing straight and curved sectors, start
  * marker, and chequered flag.
  */
object TrackView:

  private var chequeredFlagImage: Option[Image] = None
  private var chequeredFlagPosition: Option[Coordinate] = None
  private var chequeredFlagVisible: Boolean = false
  private var canvasRef: Option[Canvas] = None

  /** Draws the entire track and sets up the canvas and hidden chequered flag. */
  def drawTrack(canvas: Canvas, sectors: List[ShowableTrackSector]): Unit =
    canvasRef = Some(canvas)
    val gc = canvas.graphicsContext2D
    gc.setStroke(Color.Grey)
    gc.setLineWidth(8)

    sectors.foreach { sector =>
      sector.sector.sectorType match
        case TrackSectorType.Straight =>
          drawStraight(gc, sector.start, sector.end)
        case TrackSectorType.Curve =>
          drawCurve(gc, sector)

      if sector.isStart then
        drawStartMarker(gc, sector.start)
        setupChequeredFlag(sector.start)
    }

  /** Shows the chequered flag if position and canvas are set. */
  def showChequeredFlag(): Unit =
    if chequeredFlagVisible || chequeredFlagImage.isEmpty || chequeredFlagPosition.isEmpty || canvasRef.isEmpty then
      return
    chequeredFlagVisible = true
    val gc = canvasRef.get.graphicsContext2D
    val pos = chequeredFlagPosition.get
    drawChequeredFlagIcon(gc, pos)

  /** Sets up the chequered flag image and position (but does not draw it). */
  private def setupChequeredFlag(position: Coordinate): Unit =
    chequeredFlagImage = Some(new Image(getClass.getResourceAsStream("/icons/chequered_flag.png")))
    chequeredFlagPosition = Some(position)
    chequeredFlagVisible = false

  /** Draws the chequered flag icon at the given coordinate. */
  private def drawChequeredFlagIcon(gc: GraphicsContext, position: Coordinate, size: Double = 50): Unit =
    chequeredFlagImage.foreach { image =>
      val x = position.x - size / 2
      val y = position.y - size / 2
      gc.drawImage(image, x, y, size + 15, size)
    }

  def drawStraight(gc: GraphicsContext, start: Coordinate, end: Coordinate): Unit =
    gc.strokeLine(start.x, start.y, end.x, end.y)

  def drawCurve(gc: GraphicsContext, sector: ShowableTrackSector): Unit =
    val start = sector.start
    val end = sector.end
    val dx = end.x - start.x
    val dy = end.y - start.y
    val distance = math.hypot(dx, dy)

    val mx = (start.x + end.x) / 2
    val my = (start.y + end.y) / 2
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
    var sweep = endAngle - startAngle
    if sweep < 0 then sweep += 360
    if sector.invert then startAngle += 180

    gc.strokeArc(topLeftX, topLeftY, radius * 2, radius * 2, startAngle + 90, sweep, ArcType.Open)

  def drawStartMarker(gc: GraphicsContext, position: Coordinate): Unit =
    gc.setFill(Color.Green)
    gc.fillOval(position.x - 5, position.y - 5, 10, 10)
