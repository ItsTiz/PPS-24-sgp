package view

import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label, Spinner, ComboBox}
import scalafx.scene.layout.VBox
import scalafx.geometry.Pos
import scalafx.stage.Stage
import model.simulation.weather.WeatherModule.Weather

/** The initial view shown before the simulation starts. */
object StartView:

  /** Initializes the stage with a start screen and a callback for when the simulation should begin.
   *
   * @param stage
   *   The primary stage.
   * @param onStart
   *   Callback function executed when the start button is clicked.
   *   Receives the selected number of laps, drivers, and starting weather.
   */
  def initializeStage(stage: Stage, onStart: (Int, Int, Weather) => Unit): Unit =
    val titleLabel = new Label("ScalaGP") {
      style = "-fx-font-size: 36pt; -fx-font-weight: bold"
    }

    // Spinner for selecting the number of laps (1 to 10)
    val lapSelector = new Spinner[Int](1, 10, 5)
    lapSelector.setEditable(true)
    lapSelector.style = "-fx-font-size: 14pt"

    val lapLabel = new Label("Select Number of Laps:") {
      style = "-fx-font-size: 16pt"
    }

    // Spinner for selecting the number of drivers (2 to 10)
    val driverSelector = new Spinner[Int](2, 10, 6)
    driverSelector.setEditable(true)
    driverSelector.style = "-fx-font-size: 14pt"

    val driverLabel = new Label("Select Number of Drivers:") {
      style = "-fx-font-size: 16pt"
    }

    // ComboBox for selecting the starting weather
    val weatherSelector = new ComboBox[Weather](Weather.values.toSeq) {
      value = Weather.Sunny // default
      style = "-fx-font-size: 14pt"
    }

    val weatherLabel = new Label("Select Starting Weather:") {
      style = "-fx-font-size: 16pt"
    }

    val startButton = new Button("Start Simulation") {
      style = "-fx-font-size: 16pt"
      onAction = _ =>
        onStart(
          lapSelector.getValue,
          driverSelector.getValue,
          weatherSelector.value.value
        )
    }

    val layout = new VBox(
      30,
      titleLabel,
      lapLabel, lapSelector,
      driverLabel, driverSelector,
      weatherLabel, weatherSelector,
      startButton
    )
    layout.alignment = Pos.Center

    stage.title = "Start Race"
    stage.width = 800
    stage.height = 600
    stage.scene = new Scene {
      root = layout
    }
