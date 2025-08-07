package controller.scheduler

import model.race.RaceConstants.timeStepUI
import model.simulation.states.RaceStateModule.RaceState
import java.util.{Timer, TimerTask}

/** Manages simulation timing and execution scheduling. */
trait SimulationScheduler:
  /** Starts the simulation with the given step function and update callback. */
  def startSimulation(
      initialState: RaceState,
      stepFunction: RaceState => (RaceState, Boolean),
      updateCallback: RaceState => Unit
  ): Unit

object SimulationScheduler:
  def apply(): SimulationScheduler = new TimerBasedScheduler()

private class TimerBasedScheduler extends SimulationScheduler:
  private var timerOpt: Option[Timer] = None

  /** @inheritdoc */
  def startSimulation(
      initialState: RaceState,
      stepFunction: RaceState => (RaceState, Boolean),
      updateCallback: RaceState => Unit
  ): Unit =
    stopSimulation()
    val timer = new Timer()
    timerOpt = Some(timer)
    scheduleNextStep(timer, initialState, stepFunction, updateCallback)

  private def stopSimulation(): Unit =
    timerOpt.foreach(_.cancel())
    timerOpt = None

  private def scheduleNextStep(
      timer: Timer,
      raceState: RaceState,
      stepFunction: RaceState => (RaceState, Boolean),
      updateCallback: RaceState => Unit
  ): Unit =
    val task = new TimerTask:
      override def run(): Unit =
        val (nextState, continue) = stepFunction(raceState)
        updateCallback(nextState)
        if (continue) scheduleNextStep(timer, nextState, stepFunction, updateCallback) else stopSimulation()
    timer.schedule(task, timeStepUI.toLong)
