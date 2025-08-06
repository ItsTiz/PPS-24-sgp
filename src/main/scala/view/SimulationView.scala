package view

import model.simulation.states.RaceStateModule.RaceState
import model.simulation.weather.WeatherModule.Weather
import model.tracks.TrackModule.Track
import model.car.CarModule.Car
import scalafx.application.Platform
import scalafx.scene.Scene
import scalafx.stage.{Stage, StageStyle}
import scalafx.scene.canvas.Canvas
import scalafx.scene.control.{Button, Label}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{BorderPane, HBox, StackPane, VBox}
import scalafx.geometry.{Insets, Pos}
import view.car.CarView
import view.track.{ShowableTrackGenerator, TrackView}
import view.scoreboard.ScoreboardView
import view.scoreboard.FinalScoreboardView

/** View class responsible for displaying the car race simulation.
  *
  * It renders the track, cars, lap information, and weather icon.
  *
  * @param viewWidth
  *   the width of the simulation view in pixels
  * @param viewHeight
  *   the height of the simulation view in pixels
  * @param track
  *   the track model to display
  */
class SimulationView(val viewWidth: Double, val viewHeight: Double, val track: Track) extends SimulationDisplay:

  /** Canvas where the track is drawn */
  private val trackCanvas = new Canvas(viewWidth, viewHeight)

  /** Canvas where cars are drawn on top of the track */
  private val carsCanvas = new Canvas(viewWidth, viewHeight)

  /** Scoreboard view displaying current race standings */
  private val scoreboardView = new ScoreboardView()

  private var currentFinalRaceState: RaceState = _

  /** Label showing the current lap information */
  private val lapLabel = new Label("Lap: 0 / 0") {
    style = "-fx-font-size: 16pt; -fx-padding: 10;"
  }

  /** ImageView displaying the current weather icon */
  private val weatherIcon = new ImageView()
  weatherIcon.fitWidth = 50
  weatherIcon.fitHeight = 50
  weatherIcon.preserveRatio = true
  weatherIcon.visible = true

  /** Tracks the last lap count per car to detect lap completions */
  private var previousLaps: Map[Car, Int] = Map.empty

  /** Returns the weather icon image corresponding to the given weather condition.
    *
    * @param weather
    *   the current weather condition
    * @return
    *   the Image for the weather icon; falls back to a placeholder if resource not found
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

  /** Initializes the JavaFX Stage with the simulation view components and scene.
    *
    * This method sets up the track and cars canvases, the lap label, and the weather icon in a layered layout, then
    * shows the stage.
    *
    * @param stage
    *   the primary JavaFX stage to initialize
    */

  def initializeStage(stage: Stage): Unit =
    val stackPane = new StackPane()
    stackPane.getChildren.addAll(trackCanvas, carsCanvas)

    val showableSectors = ShowableTrackGenerator.generateRectangular(track)
    TrackView.drawTrack(trackCanvas, showableSectors)
    CarView.setTrack(showableSectors)

    val topBar = HBox(20, lapLabel)
    topBar.alignment = Pos.CenterLeft
    topBar.padding = Insets(1)

    val weatherBox = new StackPane()
    weatherBox.children = Seq(weatherIcon)
    weatherBox.alignment = Pos.TopRight
    weatherBox.padding = Insets(1)

    val mainHBox = new HBox()
    mainHBox.children.addAll(scoreboardContainer, stackPane)
    mainHBox.spacing = 1
    mainHBox.padding = Insets(1)

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
    stage.onCloseRequest = _ => System.exit(0)
    stage.show()

  /** Updates the simulation display based on the current race state. This includes lap count, weather, drawing cars,
    * and updating the scoreboard.
    *
    * @param state
    *   the current RaceState to render
    */

  override def update(state: RaceState): Unit =
    Platform.runLater(() =>
      updateLapLabel(state)
      updateWeatherIcon(state.weather)
      redrawCars(state)
      updateScoreboard(state)
      putChequeredFlag(state)
    )

  /** Updates the lap label text according to the race progress.
    *
    * Shows the current lap and total laps, or "Race Finished!" if all cars completed the race.
    *
    * @param state
    *   the current race state
    */
  private def updateLapLabel(state: RaceState): Unit =
    val currentLap = (state.cars zip state.carStates).map(_._2.currentLaps).maxOption.getOrElse(0)
    val allCarsFinished = (state.cars zip state.carStates).forall(_._2.currentLaps >= state.laps)

    if allCarsFinished then
      lapLabel.text = "Race Finished!"
      showFinalButton.visible = true
      currentFinalRaceState = state
    else
      lapLabel.text = s"Lap: $currentLap / ${state.laps}"

  /** Updates the weather icon image based on the current weather condition.
    *
    * @param weather
    *   the current weather condition
    */
  private def updateWeatherIcon(weather: Weather): Unit =
    weatherIcon.image = getWeatherIcon(weather)

  /** Clears and redraws all cars on the cars canvas according to the current state.
    *
    * @param state
    *   the current race state containing car positions
    */
  private def redrawCars(state: RaceState): Unit =
    val ctx = carsCanvas.graphicsContext2D
    ctx.clearRect(0, 0, carsCanvas.width.value, carsCanvas.height.value)
    CarView.drawCars(carsCanvas, state)

  /** Creates the Scoreboard view
    *
    * @param state
    *   the current race state containing car positions
    */
  private def updateScoreboard(state: RaceState): Unit =
    scoreboardView.updateScoreboard(state)

  /** Puts the chequered falg on track if the cars are in the final lap
    *
    * @param state
    *   the current race state containing car positions
    */
  private def putChequeredFlag(state: RaceState): Unit =
    val showChequeredFlag = (state.cars zip state.carStates).forall(_._2.currentLaps == state.laps - 1)
    if showChequeredFlag then TrackView.showChequeredFlag()

  private val showFinalButton = new Button("Show Final Scoreboard"):
    visible = false
    maxWidth = Double.MaxValue
    onAction = _ =>
      val finalStage = new Stage:
        title = "Final Scoreboard"
        initStyle(StageStyle.Utility)
        scene = new Scene:
          root = FinalScoreboardView.finalScoreboardView(currentFinalRaceState)
      finalStage.show()

  private val scoreboardContainer = new VBox(10, scoreboardView, showFinalButton):
    prefWidth = 300
    padding = Insets(10)
