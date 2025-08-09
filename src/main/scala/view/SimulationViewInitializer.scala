package view

import model.tracks.TrackModule.{Track, TrackType}
import scalafx.scene.control.{Button, Label}
import view.controller.RaceStatusController
import view.simulation.SimulationView

object SimulationViewInitializer:
  private val DefaultViewWidth: Double = 1000
  private val DefaultViewHeight: Double = 700
  def createSimulationView(track: Track, trackType: TrackType): SimulationView =
    val lapLabel = new Label("Lap: 0 / 0") {
      style = "-fx-font-size: 16pt; -fx-padding: 10;"
    }
    val showFinalButton = new Button("Show Final Scoreboard") {
      visible = false
      maxWidth = Double.MaxValue
    }

    val raceStatusController = new RaceStatusController(lapLabel, showFinalButton)

    new SimulationView(
      DefaultViewWidth,
      DefaultViewHeight,
      track,
      trackType,
      raceStatusController,
      lapLabel,
      showFinalButton
    )
