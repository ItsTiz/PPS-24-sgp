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

/** Utility object responsible for rendering cars onto a JavaFX canvas during a simulation. Handles color assignment,
  * positioning, and drawing of cars for both straight and curved sectors.
  */
object CarView:

  /** The track used to determine where to draw cars. It contains positional data per sector. */
  private var showableTrack: List[ShowableTrackSector] = List.empty

  /** Radius (in pixels) used to draw the car on the canvas. */
  private val CarRadius = 15.0

  /** Horizontal and vertical offset for positioning (currently unused but kept for future scaling). */
  private val CanvasOffset = 50.0

  /** Scale factor to map model coordinates to canvas pixels (currently unused but kept for scaling logic). */
  private val Scale = 10.0

  /** Predefined colors for well-known car models. */
  private val predefinedColors: Map[String, Color] = Map(
    "Ferrari" -> Color.Red,
    "Mercedes" -> Color.Silver,
    "McLaren" -> Color.Orange,
    "Alpine" -> Color.Blue
  )

  /** Internal map for assigning and caching unique colors to car models. */
  private val carColors: mutable.Map[String, Color] = mutable.Map.from(predefinedColors)

  /** Generates a random RGB color for car models without predefined colors.
    *
    * @return
    *   A random visible [[Color]].
    */
  private def randomColor(): Color =
    Color.color(Random.nextDouble(), Random.nextDouble(), Random.nextDouble())

  /** Retrieves or assigns a unique color to the given car model.
    *
    * @param model
    *   The car model name.
    * @return
    *   A [[Color]] used to draw the car.
    */
  private def getColorForModel(model: String): Color =
    carColors.getOrElseUpdate(model, randomColor())

  /** Draws a single car on the canvas according to its current state.
    *
    *   - If the car is in a curved sector, it interpolates position along an arc.
    *   - If in a straight sector, it uses linear interpolation.
    *
    * @param canvas
    *   The canvas on which the car is drawn.
    * @param car
    *   The [[Car]] being drawn.
    * @param carState
    *   The [[CarState]] containing the car's progress and current sector.
    */
  def drawCar(canvas: Canvas, car: Car, carState: CarState): Unit =
    val gc = canvas.graphicsContext2D
    val currentSector = showableTrack.find(_.sector == carState.currentSector)

    currentSector.foreach { sector =>
      val progress = carState.progress

      val (x, y) =
        if sector.sector.sectorType == Curve then
          // Curve interpolation logic
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
          // Straight line interpolation
          val start = sector.start
          val end = sector.end
          val x = start.x + (end.x - start.x) * progress
          val y = start.y + (end.y - start.y) * progress
          (x, y)

      // Draw the car body
      gc.setFill(getColorForModel(car.model))
      gc.fillOval(x - CarRadius, y - CarRadius, CarRadius * 2, CarRadius * 2)

      // Draw the car number centered inside the circle
      gc.setFill(Color.White)
      gc.setFont(Font("Arial", 14))
      val numberStr = car.carNumber.toString
      val textWidth = numberStr.length * 6
      gc.fillText(numberStr, x - textWidth / 2, y + 5)
    }

  /** Draws all cars on the canvas for the current race state.
    *
    * Clears the canvas before drawing to avoid overlaps.
    *
    * @param canvas
    *   The canvas to draw onto.
    * @param raceState
    *   The full race state including cars and their positions.
    */
  def drawCars(canvas: Canvas, raceState: RaceState): Unit =
    val gc = canvas.graphicsContext2D
    gc.clearRect(0, 0, canvas.width.value, canvas.height.value)

    (raceState.cars zip raceState.carStates).foreach { (car, carState) =>
      drawCar(canvas, car, carState)
    }

  /** Sets the track data used for positioning cars.
    *
    * This should be called before drawing, typically once when the race view is initialized.
    *
    * @param trackSectors
    *   A list of [[ShowableTrackSector]]s containing geometry data.
    */
  def setTrack(trackSectors: List[ShowableTrackSector]): Unit =
    showableTrack = trackSectors
