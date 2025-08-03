package view.car

import scalafx.scene.canvas.Canvas
import scalafx.scene.paint.Color
import scalafx.scene.text.Font
import model.car.CarModule.Car
import model.simulation.states.CarStateModule.CarState
import model.simulation.states.RaceStateModule.RaceState
import model.tracks.TrackSectorModule.TrackSectorType.Curve
import scala.collection.mutable
import scala.util.Random
import view.track.ShowableTrackSector

/** Utility object responsible for rendering cars onto a JavaFX canvas during a simulation.
 * Handles color assignment, positioning, and drawing of cars for both straight and curved sectors.
 */
object CarView:

  /** The track data used to determine car positions on the canvas.
   *
   * Contains sector geometry required to interpolate car positions.
   */
  private var showableTrack: List[ShowableTrackSector] = List.empty

  /** Radius (in pixels) used to draw the circular car representation on the canvas. */
  private val CarRadius = 15.0

  /** Horizontal and vertical offset for positioning (currently unused, reserved for scaling). */
  private val CanvasOffset = 50.0

  /** Scale factor to convert model coordinates to canvas pixels (currently unused, reserved for scaling). */
  private val Scale = 10.0

  /** Predefined colors for known car models for consistent coloring. */
  private val predefinedColors: Map[String, Color] = Map(
    "Ferrari" -> Color.Red,
    "Mercedes" -> Color.Silver,
    "McLaren" -> Color.Orange,
    "Alpine" -> Color.Blue
  )

  /** Mutable map caching assigned colors for car models, initialized with predefined colors. */
  private val carColors: mutable.Map[String, Color] = mutable.Map.from(predefinedColors)

  /** Generates a random visible color to assign to car models without predefined colors.
   *
   * @return
   *   A random [[Color]] with random RGB components.
   */
  private def randomColor(): Color =
    Color.color(Random.nextDouble(), Random.nextDouble(), Random.nextDouble())

  /** Gets the color associated with a car model, assigning a random color if none exists.
   *
   * @param model
   *   The car model name.
   * @return
   *   The [[Color]] assigned to the model.
   */
  private def getColorForModel(model: String): Color =
    carColors.getOrElseUpdate(model, randomColor())

  /** Draws a single car on the canvas at the position computed from its current state.
   *
   * - Interpolates position along the curve if the sector is a curve.
   * - Uses linear interpolation if the sector is straight.
   *
   * @param canvas
   *   The [[Canvas]] on which to draw the car.
   * @param car
   *   The [[Car]] instance to draw.
   * @param carState
   *   The [[CarState]] holding the car's current progress and sector.
   */
  def drawCar(canvas: Canvas, car: Car, carState: CarState): Unit =
    val gc = canvas.graphicsContext2D
    val currentSector = showableTrack.find(_.sector == carState.currentSector)

    currentSector.foreach { sector =>
      val progress = carState.progress

      val (x, y) =
        if sector.sector.sectorType == Curve then
          // Compute position along an arc for curved sectors
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

          val startAngleRad = math.atan2(start.y - cy, start.x - cx)
          val endAngleRad = math.atan2(end.y - cy, end.x - cx)

          val rawSweep = endAngleRad - startAngleRad
          val sweep = if rawSweep < 0 then rawSweep + 2 * Math.PI else rawSweep

          val angle = startAngleRad + progress * sweep

          val carX = cx + radius * math.cos(angle)
          val carY = cy + radius * math.sin(angle)

          (carX, carY)
        else
          // Linear interpolation for straight sectors
          val start = sector.start
          val end = sector.end
          val x = start.x + (end.x - start.x) * progress
          val y = start.y + (end.y - start.y) * progress
          (x, y)

      // Draw car circle filled with model color
      gc.setFill(getColorForModel(car.model))
      gc.fillOval(x - CarRadius, y - CarRadius, CarRadius * 2, CarRadius * 2)

      // Draw car number centered inside the circle
      gc.setFill(Color.White)
      gc.setFont(Font("Arial", 14))
      val numberStr = car.carNumber.toString
      val textWidth = numberStr.length * 6
      gc.fillText(numberStr, x - textWidth / 2, y + 5)
    }

  /** Draws all cars on the canvas based on the current race state.
   *
   * Clears the canvas before drawing to prevent overlapping artifacts.
   *
   * @param canvas
   *   The [[Canvas]] on which to draw cars.
   * @param raceState
   *   The current [[RaceState]] containing cars and their states.
   */
  def drawCars(canvas: Canvas, raceState: RaceState): Unit =
    val gc = canvas.graphicsContext2D
    gc.clearRect(0, 0, canvas.width.value, canvas.height.value)

    (raceState.cars zip raceState.carStates).foreach { (car, carState) =>
      drawCar(canvas, car, carState)
    }

  /** Sets the track geometry data used to position cars on the canvas.
   *
   * This must be called prior to drawing cars, usually once when initializing the simulation view.
   *
   * @param trackSectors
   *   A list of [[ShowableTrackSector]] containing the geometry and sector info.
   */
  def setTrack(trackSectors: List[ShowableTrackSector]): Unit =
    showableTrack = trackSectors
