package view.car

import scalafx.scene.canvas.Canvas
import scalafx.scene.paint.Color
import scalafx.scene.text.Font
import model.car.CarModule.Car
import model.simulation.states.CarStateModule.CarState
import model.simulation.states.RaceStateModule.RaceState
import scala.collection.mutable
import scala.util.Random
import view.track.ShowableTrackSector

import java.util.{Timer, TimerTask}

/** Utility object responsible for rendering cars onto a JavaFX canvas.
  */
object CarView:
  private var showableTrack: List[ShowableTrackSector] = List.empty
  private val CarRadius = 15.0
  private val CanvasOffset = 50.0
  private val Scale = 10.0

  // Predefined colors for known car models
  private val predefinedColors: Map[String, Color] = Map(
    "Ferrari" -> Color.Red,
    "Mercedes" -> Color.Silver,
    "McLaren" -> Color.Orange,
    "Alpine" -> Color.Blue
  )

  // Mutable color map used to dynamically assign random colors to unknown models
  private val carColors: mutable.Map[String, Color] = mutable.Map.from(predefinedColors)

  /** Generates a random visible color.
    * @return
    *   A new random Color.
    */
  private def randomColor(): Color =
    Color.color(Random.nextDouble(), Random.nextDouble(), Random.nextDouble())

  /** Assigns a color for the given car model. If the model is not predefined, a random color will be assigned and
    * reused.
    *
    * @param model
    *   The car model string.
    * @return
    *   A `Color` associated with the model.
    */
  private def getColorForModel(model: String): Color =
    carColors.getOrElseUpdate(model, randomColor())

  /** Draws a single car on the provided canvas.
    *
    * @param canvas
    *   The canvas where the car is drawn.
    * @param car
    *   The car to draw.
    */

  def drawCar(canvas: Canvas, car: Car, carState: CarState): Unit =
    val gc = canvas.graphicsContext2D

    // Find the ShowableTrackSector matching the car's current sector
    val currentSector = showableTrack.find(_.sector == carState.currentSector)

    currentSector.foreach { sector =>
      val start = sector.start
      val end = sector.end
      val progress = carState.progress

      // Linearly interpolate X and Y
      val x = start.x + (end.x - start.x) * progress
      val y = start.y + (end.y - start.y) * progress

      // Draw the car as a filled circle
      gc.setFill(getColorForModel(car.model))
      gc.fillOval(x - CarRadius, y - CarRadius, CarRadius * 2, CarRadius * 2)

      // Draw the car number centered in the circle
      gc.setFill(Color.White)
      gc.setFont(Font("Arial", 14))
      val numberStr = car.carNumber.toString
      val textWidth = numberStr.length * 6
      gc.fillText(numberStr, x - textWidth / 2, y + 5)
    }

  /** Draws all cars on the canvas.
    *
    * @param canvas
    *   The canvas on which to draw.
    * @param cars
    *   A list of cars to draw.
    */
  def drawCars(canvas: Canvas, raceState: RaceState): Unit =
    val gc = canvas.graphicsContext2D

    // Clear the entire canvas before redrawing
    gc.clearRect(0, 0, canvas.width.value, canvas.height.value)

    val carsMap: Map[Car, CarState] = raceState.carsMap
    carsMap.foreach { case (car, state) =>
      drawCar(canvas, car, state)
    }

  /** TODO
    */
  def UpdateCarsView(cars: List[Car]): Unit = ???

  /** Sets the current showable track to be used for rendering cars.
    *
    * @param trackSectors
    *   The list of track sectors with positional data.
    */
  def setTrack(trackSectors: List[ShowableTrackSector]): Unit =
    showableTrack = trackSectors
