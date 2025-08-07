package controller.engine

import model.simulation.events.EventModule.Event
import model.simulation.states.SimulationModule.{Simulation, SimulationState}
import model.simulation.states.RaceStateModule.RaceState
import model.race.RaceConstants.logicalTimeStep
import model.simulation.events.processor.EventProcessor

/** Core simulation engine responsible for executing simulation steps. */
trait SimulationEngine:
  /** Executes a single simulation step.
    * @return
    *   Simulation[Boolean] indicating if simulation should continue
    */
  def executeStep(): Simulation[Boolean]

object SimulationEngine:
  def apply()(using StateManager: SimulationState, EventProcessor: EventProcessor): SimulationEngine =
    SimulationEngineImpl()

private class SimulationEngineImpl(using StateManager: SimulationState, EventProcessor: EventProcessor)
    extends SimulationEngine:

  /** @inheritdoc */
  def executeStep(): Simulation[Boolean] =
    for
      currentState <- StateManager.getState
      (eventsToProcess, dequeuedState) = currentState.dequeueAllAtCurrentTime(currentState.raceTime)
      _ <- StateManager.setState(dequeuedState.advanceTime(logicalTimeStep))
      queueNotEmpty <- processEvents(eventsToProcess)
      nextState <- StateManager.getState
    yield queueNotEmpty && !nextState.isRaceFinished

  private def processEvents(events: List[Event]): Simulation[Boolean] =
    for
      _ <- updateStateWithEvents(events)
      finalState <- StateManager.getState
    yield !finalState.isEventQueueEmpty

  private def updateStateWithEvents(events: List[Event]): Simulation[Unit] =
    events.foldLeft(StateManager.pure(()))((acc, event) =>
      for
        _ <- acc
        state <- StateManager.getState
        updated = EventProcessor.processEvent(state)(event)
        _ <- StateManager.setState(updated)
      yield ()
    )
