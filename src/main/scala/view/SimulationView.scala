package view

import model.simulation.states.RaceStateModule.RaceState
import model.simulation.weather.WeatherModule.Weather
import model.tracks.TrackModule.Track
import model.race.ScoreboardModule.Scoreboard
import model.car.CarModule.Car
import scalafx.application.Platform
import scalafx.scene.Scene
import scalafx.stage.Stage
import scalafx.scene.canvas.Canvas
import scalafx.scene.control.Label
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{BorderPane, HBox, StackPane}
import scalafx.geometry.{Insets, Pos}
import view.car.CarView
import view.track.{ShowableTrackGenerator, TrackView}
import view.scoreboard.ScoreboardView

/**
 * The SimulationView class manages the graphical display for the car race simulation.
 * It shows the track, cars, weather conditions, lap information, and a live scoreboard.
 *
 * @param viewWidth the width of the simulation view area (excluding scoreboard)
 * @param viewHeight the height of the simulation view area
 * @param track the Track instance representing the race track layout
 */
class SimulationView(val viewWidth: Double, val viewHeight: Double, val track: Track) extends SimulationDisplay:

  /** Canvas for rendering the static track background */
  private val trackCanvas = new Canvas(viewWidth, viewHeight)

  /** Canvas for rendering moving cars */
  private val carsCanvas = new Canvas(viewWidth, viewHeight)

  /** Scoreboard view displaying current race standings */
  private val scoreboardView = new ScoreboardView()

  /** Label displaying the current lap progress */
  private val lapLabel = new Label("Lap: 0 / 0") {
    style = "-fx-font-size: 16pt; -fx-padding: 10;"
  }

  /** ImageView for displaying the current weather icon */
  private val weatherIcon = new ImageView() {
    fitWidth = 50
    fitHeight = 50
    preserveRatio = true
    visible = true
  }

  /** Holds the current scoreboard data */
  private var currentScoreboard: Scoreboard = Scoreboard(List.empty)

  /** Tracks the last lap count per car to detect lap completions */
  private var previousLaps: Map[Car, Int] = Map.empty

  /**
   * Returns an Image representing the weather icon based on the current weather.
   *
   * @param weather the current weather condition
   * @return an Image object for the corresponding weather icon
   */
  def getWeatherIcon(weather: Weather): Image =
    val iconPath = weather match
      case Weather.Sunny => "/icons/sunny.png"
      case Weather.Rainy => "/icons/rainy.png"
      case Weather.Foggy => "/icons/foggy.png"
    val stream = getClass.getResourceAsStream(iconPath)
    if stream == null then
      println(s"ERROR: Could not find icon at $iconPath")
      new Image("https://via.placeholder.com/50") // fallback placeholder image URL
    else
      new Image(stream)

  /**
   * Sets up and displays the main application stage with the track view,
   * scoreboard, lap label, and weather icon.
   *
   * @param stage the primary stage of the ScalaFX application
   */
  def initializeStage(stage: Stage): Unit =
    val stackPane = new StackPane()
    stackPane.getChildren.addAll(trackCanvas, carsCanvas)

    val showableSectors = ShowableTrackGenerator.generateRectangular(track)
    TrackView.drawTrack(trackCanvas, showableSectors)
    CarView.setTrack(showableSectors)

    val topBar = new HBox(20, lapLabel) {
      alignment = Pos.CenterLeft
      padding = Insets(10)
    }

    val weatherBox = new StackPane() {
      children = Seq(weatherIcon)
      alignment = Pos.TopRight
      padding = Insets(10)
    }

    val mainHBox = new HBox()
    mainHBox.children.addAll(scoreboardView, stackPane)
    mainHBox.spacing = 10
    mainHBox.padding = Insets(10)

    scoreboardView.prefWidth = 300
    stackPane.prefWidth = viewWidth - scoreboardView.prefWidth.value - 10

    val rootLayout = new BorderPane()
    rootLayout.setCenter(mainHBox)
    rootLayout.setTop(topBar)
    rootLayout.setRight(weatherBox)

    val scene = new Scene(viewWidth + scoreboardView.prefWidth.value + 50, viewHeight) {
      root = rootLayout
    }

    stage.title = "Car Simulation App"
    stage.scene = scene
    stage.sizeToScene()

    stage.show()

  /**
   * Updates the simulation display based on the current race state.
   * This includes lap count, weather, drawing cars, and updating the scoreboard.
   *
   * @param state the current RaceState to render
   */
  override def update(state: RaceState): Unit =
    Platform.runLater(() =>
      val currentLap = (state.cars zip state.carStates).map(_._2.currentLaps).maxOption.getOrElse(0)
      lapLabel.text = s"Lap: $currentLap / ${state.laps}"

      val allCarsFinished = (state.cars zip state.carStates).forall(_._2.currentLaps >= state.laps)
      if allCarsFinished then lapLabel.text = "Race Finished!"

      weatherIcon.image = getWeatherIcon(state.weather)

      val ctx = carsCanvas.graphicsContext2D
      ctx.clearRect(0, 0, carsCanvas.width.value, carsCanvas.height.value)
      CarView.drawCars(carsCanvas, state)

      state.cars.zip(state.carStates).foreach { (car, carState) =>
        val lastLap = previousLaps.getOrElse(car, 0)
        val newLap = carState.currentLaps
        if newLap > lastLap then
          // TODO: Replace hardcoded lap time with actual lap time from carState when available
          currentScoreboard = currentScoreboard.recordLap(car, 90)
          previousLaps += car -> newLap
      }
      scoreboardView.update(currentScoreboard)

      val showChequeredFlag = (state.cars zip state.carStates).forall(_._2.currentLaps == state.laps - 1)
      if showChequeredFlag then TrackView.showChequeredFlag()
    )
