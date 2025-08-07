package view

import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label, Spinner}
import scalafx.scene.layout.VBox
import scalafx.geometry.Pos
import scalafx.stage.Stage

/** The initial view shown before the simulation starts. */
object StartView:

  /** Initializes the stage with a start screen and a callback for when the simulation should begin.
    *
    * @param stage
    *   The primary stage.
    * @param onStart
    *   Callback function executed when the start button is clicked. Receives the selected number of laps and drivers.
    */
  def initializeStage(stage: Stage, onStart: (Int, Int) => Unit): Unit =
    val titleLabel = new Label("ScalaGP") {
      style = "-fx-font-size: 36pt; -fx-font-weight: bold"
    }

    // Spinner for selecting the number of laps (1 to 10)
    val lapSelector = new Spinner[Int](1, 10, 5) // min = 1, max = 10, default = 5
    lapSelector.setEditable(true)
    lapSelector.style = "-fx-font-size: 14pt"

    val lapLabel = new Label("Select Number of Laps:") {
      style = "-fx-font-size: 16pt"
    }

    // Spinner for selecting the number of drivers (2 to 10)
    val driverSelector = new Spinner[Int](2, 10, 6) // min = 2, max = 10, default = 6
    driverSelector.setEditable(true)
    driverSelector.style = "-fx-font-size: 14pt"

    val driverLabel = new Label("Select Number of Drivers:") {
      style = "-fx-font-size: 16pt"
    }

    val startButton = new Button("Start Simulation") {
      style = "-fx-font-size: 16pt"
      onAction = _ => onStart(lapSelector.getValue, driverSelector.getValue)
    }

    val layout = new VBox(30, titleLabel, lapLabel, lapSelector, driverLabel, driverSelector, startButton)
    layout.alignment = Pos.Center

    stage.title = "Start Race"
    stage.width = 800
    stage.height = 600
    stage.scene = new Scene {
      root = layout
    }
