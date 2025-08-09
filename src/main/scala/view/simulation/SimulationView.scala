package view.simulation

import model.tracks.TrackModule.{Track, TrackType}
import scalafx.scene.control.{Button, Label}
import view.SimulationDisplay
import view.controller.RaceStatusController
import view.simulation.{MainContentBuilder, TopBarBuilder, WeatherBoxBuilder}

/** View responsible for displaying the car race simulation, including track rendering, cars rendering, scoreboard,
  * weather, and race status.
  *
  * @param viewWidth
  *   width of the simulation view area in pixels
  * @param viewHeight
  *   height of the simulation view area in pixels
  * @param track
  *   the track data model to display
  * @param selectedTrackType
  *   the type of track selected for rendering
  * @param raceStatusController
  *   controller to handle race status updates and UI controls
  * @param lapLabel
  *   label control to display current lap information
  * @param showFinalButton
  *   button control for showing final results or options
  */
class SimulationView(
    val viewWidth: Double,
    val viewHeight: Double,
    val track: Track,
    val selectedTrackType: TrackType,
    val raceStatusController: RaceStatusController,
    val lapLabel: Label,
    val showFinalButton: Button
) extends SimulationDisplay:

  import model.simulation.states.RaceStateModule.RaceState
  import scalafx.scene.Scene
  import scalafx.scene.canvas.Canvas
  import scalafx.scene.image.ImageView
  import scalafx.scene.layout.VBox
  import scalafx.stage.Stage
  import view.scoreboard.ScoreboardView

  private val trackCanvas = new Canvas(viewWidth, viewHeight)
  private val carsCanvas = new Canvas(viewWidth, viewHeight)
  private val weatherIcon = new ImageView()

  initWeatherIcon()

  /** Initializes and configures the main stage window for the simulation view. Sets up layout, scene, and event
    * handling.
    *
    * @param stage
    *   the primary stage window to initialize and show
    */
  def initializeStage(stage: Stage): Unit =
    import scalafx.scene.layout.BorderPane
    val rootLayout = new BorderPane()
    rootLayout.setCenter(MainContentBuilder(viewWidth, scoreboardContainer, trackCanvas, carsCanvas, track,
        selectedTrackType))
    rootLayout.setTop(TopBarBuilder(lapLabel))
    rootLayout.setRight(WeatherBoxBuilder(weatherIcon))

    val scene = new Scene(viewWidth + ScoreboardView.prefWidth.value, viewHeight) {
      root = rootLayout
    }

    configureStage(stage, scene)

  /** Updates the view based on the current race state. This includes lap label, weather icon, cars, scoreboard, and
    * chequered flag.
    *
    * @param state
    *   the current race state
    */
  override def update(state: RaceState): Unit =
    import scalafx.application.Platform
    Platform.runLater(() =>
      raceStatusController.updateLapLabel(state)
      updateWeatherIcon(state.weather)
      redrawCars(state)
      ScoreboardView.updateScoreboard(state)
      raceStatusController.putChequeredFlag(state)
    )

  /** Initializes properties of the weather icon (size, visibility).
    */
  private def initWeatherIcon(): Unit =
    weatherIcon.fitWidth = 50
    weatherIcon.fitHeight = 50
    weatherIcon.preserveRatio = true
    weatherIcon.visible = true

  /** Updates the weather icon image based on the current weather.
    *
    * @param weather
    *   current weather to display
    */
  private def updateWeatherIcon(weather: model.weather.WeatherModule.Weather): Unit =
    import view.weather.WeatherView
    weatherIcon.image = WeatherView.getWeatherIcon(weather)

  /** Clears and redraws all cars on the cars canvas based on the current race state.
    *
    * @param state
    *   current race state containing car positions
    */
  private def redrawCars(state: RaceState): Unit =
    import view.car.CarView
    val ctx = carsCanvas.graphicsContext2D
    ctx.clearRect(0, 0, carsCanvas.width.value, carsCanvas.height.value)
    CarView.drawCars(carsCanvas, state)

  /** Container VBox holding the scoreboard and the show final button.
    */
  private def scoreboardContainer = new VBox(10, ScoreboardView, showFinalButton) {
    import scalafx.geometry.Insets
    prefWidth = 300
    padding = Insets(10)
  }

  /** Configures and shows the primary stage window.
    *
    * @param stage
    *   the stage to configure
    * @param scene
    *   the scene to set on the stage
    */
  private def configureStage(stage: Stage, scene: Scene): Unit =
    stage.title = "Car Simulation App"
    stage.scene = scene
    stage.sizeToScene()
    stage.centerOnScreen()
    stage.onCloseRequest = _ => System.exit(0)
    stage.show()

import scalafx.geometry.Insets
import scalafx.scene.canvas.Canvas
import scalafx.scene.image.ImageView
import scalafx.scene.layout.{HBox, StackPane, VBox}
import view.scoreboard.ScoreboardView

/** Builds the top bar HBox containing the lap label for the simulation UI.
  */
object TopBarBuilder:
  def apply(lapLabel: Label): HBox =
    import scalafx.geometry.Pos
    val topBar = HBox(20, lapLabel)
    topBar.alignment = Pos.CenterLeft
    topBar.padding = Insets(1)
    topBar

/** Builds the weather box StackPane containing the weather icon for the simulation UI.
  */
object WeatherBoxBuilder:
  def apply(weatherIcon: ImageView): StackPane =
    import scalafx.geometry.Pos
    val weatherBox = new StackPane()
    weatherBox.children = Seq(weatherIcon)
    weatherBox.alignment = Pos.TopRight
    weatherBox.padding = Insets(1)
    weatherBox

/** Builds the main content HBox containing the scoreboard and the stacked track/cars canvases.
  *
  * @param viewWidth
  *   the total width available for the content area
  * @param scoreboard
  *   the scoreboard VBox container
  * @param trackCanvas
  *   canvas for drawing the track
  * @param carsCanvas
  *   canvas for drawing the cars
  * @param track
  *   the track data model
  * @param trackType
  *   the selected type of track rendering
  * @return
  *   an HBox containing the scoreboard and track/car canvases
  */
object MainContentBuilder:
  def apply(viewWidth: Double, scoreboard: VBox, trackCanvas: Canvas, carsCanvas: Canvas, track: Track,
      trackType: TrackType): HBox =
    import view.car.CarView
    import view.track.{ShowableTrackGenerator, TrackView}

    val stackPane = new StackPane()
    stackPane.getChildren.addAll(trackCanvas, carsCanvas)

    val showableSectors = ShowableTrackGenerator.generate(track, trackType)
    TrackView.drawTrack(trackCanvas, showableSectors)
    CarView.setTrack(showableSectors)

    ScoreboardView.prefWidth = 300
    stackPane.prefWidth = viewWidth - ScoreboardView.prefWidth.value - 10

    val mainHBox = new HBox()
    mainHBox.children.addAll(scoreboard, stackPane)
    mainHBox.spacing = 1
    mainHBox.padding = Insets(1)
    mainHBox
