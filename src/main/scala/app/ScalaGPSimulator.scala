package app

import scalafx.application.{JFXApp3, Platform}
import scalafx.scene.Scene
import scalafx.scene.canvas.Canvas
import scalafx.scene.layout.StackPane
import view.car.CarView
import view.track.{ShowableTrackGenerator, TrackView}
import model.car.CarGenerator
import model.car.CarModule.Car
import model.simulation.states.RaceStateModule.RaceState
import model.tracks.TrackSectorModule.TrackSector
import model.simulation.states.CarStateModule.CarState
import model.car.TireModule
import model.car.TireModule.Tire
import model.car.TireModule.TireType.Medium
import model.simulation.weather.WeatherModule.Weather.*
import view.track.ShowableTrackSector
import java.util.{Timer, TimerTask}
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.canvas.Canvas
import scalafx.scene.layout.Pane
import view.car.CarView

object CarSimulatorApp extends JFXApp3:

  override def start(): Unit =
    val canvasWidth = 1000
    val canvasHeight = 700
    val trackCanvas = new Canvas(canvasWidth, canvasHeight)
    val carsCanvas = new Canvas(canvasWidth, canvasHeight)

    val stackPane = new StackPane()
    stackPane.getChildren.addAll(trackCanvas, carsCanvas) // Track at back, cars on top

    // Generate cars and track
    val cars = CarGenerator.generateCars()
    val showableSectors = List() // ShowableTrackGenerator.generateRectangular()

    val carStates: List[CarState] = cars.map(c =>
      CarState(
        maxFuel = c.maxFuel,
        fuelLevel = c.maxFuel,
        currentSpeed = 0.0,
        progress = 0.0,
        tire = Tire(Medium, degradeState = 0.0),
        currentLaps = 0,
        currentSector = TrackSector.straight(sectorLength = 200, maxSpeed = 280, avgSpeed = 200, gripIndex = 1.0)
      )
    )
    val initialRaceState = RaceState(Map from (cars zip carStates), Sunny, 3)

    // Draw the static track once
    TrackView.drawTrack(trackCanvas, showableSectors)
    CarView.setTrack(showableSectors)

    stage = new JFXApp3.PrimaryStage:
      title = "Car Simulator App"
      scene = new Scene(canvasWidth, canvasHeight):
        root = stackPane // Use stackPane instead of plain Pane

    // Start simulation with animated cars
    startSimulation(carsCanvas, initialRaceState, showableSectors)

  private val updateTimer = new Timer()

  def startSimulation(carsCanvas: Canvas, raceState: RaceState, showableTrack: List[ShowableTrackSector]): Unit =

    var fakeState: Map[Car, CarState] = raceState.cars.map { car =>
      val firstSector = showableTrack.head.sector
      car -> CarState(
        maxFuel = 100,
        fuelLevel = 80,
        currentSpeed = 90,
        progress = 0.0,
        tire = Tire(Medium, degradeState = 0.0),
        currentLaps = 1,
        currentSector = firstSector
      )
    }.toMap

    updateTimer.scheduleAtFixedRate(
      new TimerTask {
        override def run(): Unit =
          fakeState = fakeState.map { case (car, state) =>
            val increment = Math.random() * 0.1
            val newProgress = (state.progress + increment) % 1.0
            val updatedState = state.withUpdatedState(
              speed = state.currentSpeed,
              fuelConsumed = 1,
              degradeIncrease = 5,
              newProgress = newProgress,
              tire = state.tire,
              currentLaps = state.currentLaps,
              currentSector = state.currentSector
            )
            car -> updatedState
          }

          val newRaceState = RaceState(
            carMap = fakeState,
            weather = raceState.weather,
            laps = raceState.laps
          )

          Platform.runLater {
            // Clear only the car canvas
            val gc = carsCanvas.graphicsContext2D
            gc.clearRect(0, 0, carsCanvas.width.value, carsCanvas.height.value)

            // Draw updated cars
            CarView.drawCars(carsCanvas, newRaceState)
          }
      },
      0, // Initial delay
      2000 // Update every 2 seconds
    )
