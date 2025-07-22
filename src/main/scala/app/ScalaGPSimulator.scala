package app

import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.canvas.Canvas
import scalafx.scene.layout.Pane
import view.car.CarView
import view.track.{ShowableTrackGenerator, TrackView}
import model.car.CarGenerator
import model.tracks.TrackModule.TrackGenerator

object CarSimulatorApp extends JFXApp3:

  override def start(): Unit =
    val canvasWidth = 1000
    val canvasHeight = 700
    val canvas = new Canvas(canvasWidth, canvasHeight)

    // Generate cars and track
    val cars = CarGenerator.generateCars()
    val track = TrackGenerator.generateTrack()
    val showableSectors = ShowableTrackGenerator.generateRectangular()

    // Draw track and cars on the same canvas
    TrackView.drawTrack(canvas, showableSectors)
    CarView.drawCars(canvas, cars)

    stage = new JFXApp3.PrimaryStage:
      title = "Car Simulator App"
      scene = new Scene(canvasWidth, canvasHeight):
        content = new Pane {
          children = Seq(canvas)
        }
