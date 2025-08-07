package controller.engine

import controller.UISimulationController.{eventProcessor, simState}
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
  def apply()(using stateManager: SimulationState, eventProcessor: EventProcessor): SimulationEngine =
    SimulationEngineImpl()

private class SimulationEngineImpl(
    using
    stateManager: SimulationState,
    eventProcessor: EventProcessor
) extends SimulationEngine:

  def executeStep(): Simulation[Boolean] =
    for
      currentState <- stateManager.getState
      (eventsToProcess, dequeuedState) = currentState.dequeueAllAtCurrentTime(currentState.raceTime)
      _ <- stateManager.setState(dequeuedState.advanceTime(logicalTimeStep))
      isEventQueueEmpty <- processEvents(eventsToProcess)
    yield isEventQueueEmpty

  private def processEvents(events: List[Event]): Simulation[Boolean] =
    for
      _ <- updateStateWithEvents(events)
      finalState <- stateManager.getState
    yield !finalState.isEventQueueEmpty

  private def updateStateWithEvents(events: List[Event]): Simulation[Unit] =
    events.foldLeft(stateManager.pure(()))((acc, event) =>
      for
        _ <- acc
        state <- stateManager.getState
        updated = applyEvent(state)(event)
        _ <- stateManager.setState(updated)
      yield ()
    )

  private def applyEvent(state: RaceState)(event: Event): RaceState =
    eventProcessor.processEvent(state)(event)
