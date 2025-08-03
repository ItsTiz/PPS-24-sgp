package view

import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label}
import scalafx.scene.layout.VBox
import scalafx.geometry.Pos
import scalafx.stage.Stage

/** The initial view shown before the simulation starts. */
object StartView:

  /** Initializes the stage with a start screen and a callback for when the simulation should begin.
   *
   * @param stage The primary stage.
   * @param onStart Callback function executed when the start button is clicked.
   */
  def initializeStage(stage: Stage, onStart: () => Unit): Unit =
    val titleLabel = new Label("ScalaGP") {
      style = "-fx-font-size: 36pt; -fx-font-weight: bold"
    }

    val startButton = new Button("Start Simulation") {
      style = "-fx-font-size: 16pt"
      onAction = _ => onStart()
    }

    val layout = new VBox(30, titleLabel, startButton)
    layout.alignment = Pos.Center

    stage.title = "Start Race"
    stage.width = 800
    stage.height = 600
    stage.scene = new Scene {
      root = layout
    }

