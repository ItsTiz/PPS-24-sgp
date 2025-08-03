package view

import model.simulation.states.RaceStateModule.RaceState
import model.simulation.weather.WeatherModule.Weather
import model.tracks.TrackModule.Track
import scalafx.application.Platform
import scalafx.scene.Scene
import scalafx.stage.Stage
import scalafx.scene.canvas.Canvas
import scalafx.scene.control.Label
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{BorderPane, HBox, StackPane, VBox}
import scalafx.geometry.{Insets, Pos}
import view.car.CarView
import view.track.{ShowableTrackGenerator, TrackView}

class SimulationView(val viewWidth: Double, val viewHeight: Double, val track: Track) extends SimulationDisplay:

  private val trackCanvas = new Canvas(viewWidth, viewHeight)
  private val carsCanvas = new Canvas(viewWidth, viewHeight)

  private val lapLabel = new Label("Lap: 0 / 0") {
    style = "-fx-font-size: 16pt; -fx-padding: 10;"
  }

  private val weatherIcon = new ImageView() {
    fitWidth = 50
    fitHeight = 50
    preserveRatio = true
    visible = true
  }

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

    val overlay = new BorderPane()
    overlay.setTop(topBar)
    overlay.setRight(weatherBox)

    val rootLayout = new StackPane()
    rootLayout.getChildren.addAll(stackPane, overlay)

    val scene = new Scene(viewWidth, viewHeight) {
      root = rootLayout
    }

    stage.title = "Car Simulation App"
    stage.scene = scene
    stage.show()

  /** Updates the display based on the current race simulation state.
    *
    * @param state
    *   The current state of the race simulation to be rendered or shown
    */
  override def update(state: RaceState): Unit =
    Platform.runLater(() =>
      val currentLap = (state.cars zip state.carStates).map(_._2.currentLaps).maxOption.getOrElse(0)
      lapLabel.text = s"Lap: $currentLap / ${state.laps}"

      val allCarsFinished = (state.cars zip state.carStates).forall(_._2.currentLaps >= state.laps)
      if allCarsFinished then {
        lapLabel.text = "Race Finished!"
      }
      weatherIcon.image = getWeatherIcon(state.weather)

      val ctx = carsCanvas.graphicsContext2D
      ctx.clearRect(0, 0, carsCanvas.width.value, carsCanvas.height.value)

      CarView.drawCars(carsCanvas, state)
    )
