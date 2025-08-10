package view.track

import scalafx.scene.canvas.{Canvas, GraphicsContext}
import scalafx.scene.paint.Color
import scalafx.scene.shape.ArcType
import scalafx.scene.image.Image
import model.tracks.TrackSectorModule.TrackSectorType
import model.common.CoordinateModule.Coordinate

/** Responsible for rendering the race track onto a ScalaFX canvas. Supports drawing straight and curved track sectors,
  * the start marker with a checkered pattern, and the chequered flag icon.
  */
object TrackView:

  /** Optional loaded chequered flag image used for rendering the flag icon on the track */
  private var chequeredFlagImage: Option[Image] = None

  /** Optional position on the canvas where the chequered flag should be displayed */
  private var chequeredFlagPosition: Option[Coordinate] = None

  /** Flag indicating whether the chequered flag is currently visible */
  private var chequeredFlagVisible: Boolean = false

  /** Reference to the canvas currently being drawn on */
  private var canvasRef: Option[Canvas] = None

  /** Draws the entire track layout on the given canvas, including straight and curved sectors. Also sets up the hidden
    * chequered flag position and draws the start marker on top.
    *
    * @param canvas
    *   the ScalaFX Canvas to draw the track on
    * @param sectors
    *   list of ShowableTrackSector instances representing the track layout
    */
  def drawTrack(canvas: Canvas, sectors: List[ShowableTrackSector]): Unit =
    canvasRef = Some(canvas)
    val gc = canvas.graphicsContext2D
    gc.setStroke(Color.Grey)
    gc.setLineWidth(8)

    var startMarkerPos: Option[Coordinate] = None

    sectors.foreach { sector =>
      sector.sector.sectorType match
        case TrackSectorType.Straight =>
          drawStraight(gc, sector.start, sector.end)
        case TrackSectorType.Curve =>
          drawCurve(gc, sector)

      if sector.isStart then
        startMarkerPos = Some(sector.start)
        setupChequeredFlag(sector.start)
    }

    startMarkerPos.foreach(pos => drawStartMarker(gc, pos))

  /** Makes the chequered flag icon visible at its previously set position on the canvas. Does nothing if the flag is
    * already visible or if required resources are missing.
    */
  def showChequeredFlag(): Unit =
    if chequeredFlagVisible || chequeredFlagImage.isEmpty || chequeredFlagPosition.isEmpty || canvasRef.isEmpty then
      return
    chequeredFlagVisible = true
    val gc = canvasRef.get.graphicsContext2D
    val pos = chequeredFlagPosition.get
    drawChequeredFlagIcon(gc, pos)

  /** Initializes the chequered flag image and its position on the canvas. Does not draw the flag immediately.
    *
    * @param position
    *   the coordinate on the canvas where the flag will be drawn
    */
  private def setupChequeredFlag(position: Coordinate): Unit =
    chequeredFlagImage = Some(new Image(getClass.getResourceAsStream("/icons/chequered_flag.png")))
    chequeredFlagPosition = Some(position)
    chequeredFlagVisible = false

  /** Draws the chequered flag icon on the canvas at the specified position.
    *
    * @param gc
    *   graphics context of the canvas
    * @param position
    *   coordinate where the flag icon should be drawn
    * @param size
    *   size (width and height) of the icon in pixels, default is 80
    */
  private def drawChequeredFlagIcon(gc: GraphicsContext, position: Coordinate, size: Double = 80): Unit =
    chequeredFlagImage.foreach(image =>
      val x = position.x - size / 2
      val y = position.y - size / 2
      gc.drawImage(image, x, y - 20, size, size)
    )

  /** Draws a straight track sector line between two coordinates.
    *
    * @param gc
    *   graphics context of the canvas
    * @param start
    *   starting coordinate of the straight sector
    * @param end
    *   ending coordinate of the straight sector
    */
  def drawStraight(gc: GraphicsContext, start: Coordinate, end: Coordinate): Unit =
    gc.strokeLine(start.x, start.y, end.x, end.y)

  /** Draws a curved track sector as an arc on the canvas.
    *
    * @param gc
    *   graphics context of the canvas
    * @param sector
    *   ShowableTrackSector representing the curved sector details
    */
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

  /** Draws the start marker on the canvas as a small checkered square composed of four smaller black and white squares.
    * The marker is positioned centered at the given coordinate.
    *
    * @param gc
    *   graphics context of the canvas
    * @param position
    *   center coordinate of the start marker
    * @param size
    *   overall width and height of the start marker square in pixels (default 12)
    */
  def drawStartMarker(gc: GraphicsContext, position: Coordinate, size: Double = 12): Unit =
    val squareSize = size / 2
    val x = position.x - size / 2
    val y = position.y - size / 2

    gc.setFill(Color.Black)
    gc.fillRect(x, y, squareSize, squareSize)

    gc.setFill(Color.White)
    gc.fillRect(x + squareSize, y, squareSize, squareSize)

    gc.setFill(Color.White)
    gc.fillRect(x, y + squareSize, squareSize, squareSize)

    gc.setFill(Color.Black)
    gc.fillRect(x + squareSize, y + squareSize, squareSize, squareSize)
