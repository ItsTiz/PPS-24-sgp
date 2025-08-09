package view.controller

import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label}
import scalafx.stage.{Stage, StageStyle}

class RaceStatusController(lapLabel: Label, showFinalButton: Button):
  import model.simulation.states.RaceStateModule.RaceState
  private var finalRaceStateOpt: Option[RaceState] = None

  private var currentFinalRaceState: RaceState = _

  def updateLapLabel(state: RaceState): Unit =
    val currentLap = (state.cars zip state.carStates).map(_._2.currentLaps).maxOption.getOrElse(0)
    val allFinished = (state.cars zip state.carStates).forall(_._2.currentLaps >= state.laps)

    if allFinished then
      lapLabel.text = "Race Finished!"
      showFinalButton.visible = true
      currentFinalRaceState = state
      finalRaceStateOpt = Some(state)
    else
      lapLabel.text = s"Lap: $currentLap / ${state.laps}"

    showFinalButton.onAction = _ =>
      val finalStage = new Stage:
        title = "Final Scoreboard"
        initStyle(StageStyle.Utility)
        scene = buildFinalScoreboardScene()
      finalStage.show()

  def putChequeredFlag(state: RaceState): Unit =
    state.scoreboard.raceOrder.headOption.foreach { leader =>
      val carToState = (state.cars zip state.carStates).toMap
      carToState.get(leader).foreach { carState =>
        if carState.currentLaps == state.laps - 1 then
          view.track.TrackView.showChequeredFlag()
      }
    }

  private def buildFinalScoreboardScene(): Scene =
    import view.scoreboard.FinalScoreboardView
    new Scene:
      root = FinalScoreboardView.finalScoreboardView(currentFinalRaceState)

  def finalRaceState: Option[RaceState] = finalRaceStateOpt
