package view

import model.simulation.states.RaceStateModule.RaceState
import model.tracks.TrackModule.Track
import scalafx.application.Platform
import scalafx.scene.Scene
import scalafx.stage.Stage
import scalafx.scene.canvas.Canvas
import scalafx.scene.layout.StackPane
import view.car.CarView
import view.track.{ShowableTrackGenerator, TrackView}

class SimulationView(val viewWidth: Double, val viewHeight: Double, val track: Track) extends SimulationDisplay:

  val trackCanvas = new Canvas(viewWidth, viewHeight)
  val carsCanvas = new Canvas(viewWidth, viewHeight)

  def initializeStage(stage: Stage): Unit =
    val stackPane = new StackPane()
    stackPane.getChildren.addAll(trackCanvas, carsCanvas)

    val showableSectors = ShowableTrackGenerator.generateRectangular(track)

    TrackView.drawTrack(trackCanvas, showableSectors)
    CarView.setTrack(showableSectors)

    val scene = new Scene(viewWidth, viewHeight):
      root = stackPane

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
      val ctx = carsCanvas.graphicsContext2D
      ctx.clearRect(0, 0, carsCanvas.width.value, carsCanvas.height.value)

      CarView.drawCars(carsCanvas, state)
    )
