package view.simulation

/** The initial view shown before the simulation starts. */
object StartView:
  import scalafx.scene.control.{Button, Label, Spinner, ComboBox}
  import scalafx.stage.Stage
  import controller.ui.SimulationConfigListener
  import scalafx.scene.Scene
  import scalafx.scene.layout.{VBox, HBox}
  import scalafx.geometry.Pos
  import model.weather.WeatherModule.Weather
  import model.tracks.TrackModule.TrackType

  /** Initializes the stage with a start screen and a callback for when the simulation should begin.
    *
    * @param stage
    *   The primary stage.
    * @param listener
    *   The SimulationConfigListener that will handle the configuration
    * @param onStart
    *   Callback function executed when the start button is clicked. Receives the track type.
    */
  def initializeStage(stage: Stage, listener: SimulationConfigListener, onStart: TrackType => Unit): Unit =
    val titleLabel = new Label("ScalaGP") {
      style = "-fx-font-size: 36pt; -fx-font-weight: bold"
    }

    // Spinner for selecting the number of laps (1 to 10)
    val lapSelector = new Spinner[Int](1, 10, 5) {
      style = "-fx-font-size: 14pt"
    }
    val lapLabel = new Label("Laps:") {
      style = "-fx-font-size: 16pt"
    }

    // Spinner for selecting the number of drivers (2 to 10)
    val driverSelector = new Spinner[Int](2, 10, 6) {
      style = "-fx-font-size: 14pt"
    }
    val driverLabel = new Label("Drivers:") {
      style = "-fx-font-size: 16pt"
    }

    // Weather selector
    val weatherSelector = new ComboBox[Weather](Weather.values.toSeq) {
      value = Weather.Sunny
      style = "-fx-font-size: 14pt"
    }
    val weatherLabel = new Label("Weather:") {
      style = "-fx-font-size: 16pt"
    }

    // Track selector
    val trackSelector = new ComboBox[TrackType](TrackType.values.toSeq) {
      value = TrackType.Simple
      style = "-fx-font-size: 14pt"
    }
    val trackLabel = new Label("Track:") {
      style = "-fx-font-size: 16pt"
    }

    // Start button
    val startButton = new Button("Start Simulation") {
      style = "-fx-font-size: 16pt"
      onAction = _ =>
        listener.onSimulationConfigured(
          lapSelector.getValue,
          driverSelector.getValue,
          weatherSelector.value.value,
          trackSelector.value.value
        )
        onStart(
          trackSelector.value.value
        )
    }

    // Row 1: Laps + Drivers
    val row1 = new HBox(20,
      new VBox(5, lapLabel, lapSelector),
      new VBox(5, driverLabel, driverSelector)
    ) { alignment = Pos.Center }

    // Row 2: Weather + Track
    val row2 = new HBox(20,
      new VBox(5, weatherLabel, weatherSelector),
      new VBox(5, trackLabel, trackSelector)
    ) { alignment = Pos.Center }

    val layout = new VBox(
      30,
      titleLabel,
      row1,
      row2,
      startButton
    )
    layout.alignment = Pos.Center

    stage.title = "Start Race"
    stage.width = 800
    stage.height = 600
    stage.scene = new Scene {
      root = layout
    }
