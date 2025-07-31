package controller

import model.race.RaceConstants.timeStepUI

/** UI implementation of [[SimulationController]] with monadic style. */
object UISimulationController extends SimulationController:
  import model.race.RaceConstants.logicalTimeStep
  import model.race.RacePhysicsModule.RacePhysics
  import model.simulation.events.EventModule.Event
  import model.simulation.states.SimulationModule.{Simulation, SimulationState}
  import model.simulation.states.RaceStateModule.RaceState
  import model.tracks.TrackModule.Track
  import view.{CLIDisplay, SimulationDisplay}

  given simState: SimulationState = SimulationState()
  given display: SimulationDisplay = CLIDisplay() // TODO replace here with UI
  given simInit: SimulationInitializer = SimulationInitializer()
  given track: Track = simInit.track
  given eventProcessor: EventProcessor = EventProcessor()
  given physics: RacePhysics = RacePhysics()

  init()

  /** @inheritdoc */
  override def init(): Unit =
    val initialState = simInit.initSimulationEntities()
    val simulation: Simulation[Unit] = loop()

    simulation.runS(initialState).value

  /** @inheritdoc */
  override def step(): Simulation[Boolean] =
    for
      currentState <- simState.getState
      (maybeEvent, dequeuedState) = currentState.dequeueEvent
      _ <- simState.setState(dequeuedState.advanceTime(logicalTimeStep))
      continue <- processEvent(maybeEvent)
    yield continue

  /** @inheritdoc */
  override def loop(): Simulation[Unit] =
    for
      shallContinue <- step()
      currentState <- simState.getState
      _ = updateUIThreadSafe(currentState)
      result <- if shallContinue then
        for
          _ <- simState.pure(Thread.sleep(timeStepUI.toLong))
          result <- loop()
        yield result
      else simState.pure(())
    yield result

  private def processEvent(maybeEvent: Option[Event]): Simulation[Boolean] =
    maybeEvent match
      case Some(event) =>
        for
          currentState <- simState.getState
          _ <- simState.setState(eventProcessor.processEvent(currentState)(event))
        yield true
      case None => simState.pure(false)

  private def updateUIThreadSafe(state: RaceState): Simulation[Unit] =
    simState.pure {
      display.update(state)
      // TODO should be like this - ensures UI updates happen on EDT
      // SwingUtilities.invokeLater(() => display.update(state))
    }
