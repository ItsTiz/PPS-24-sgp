package controller

/** Default implementation of [[SimulationController]].
  *
  * Responsible for initializing the simulation, processing events, updating state, and interfacing with the display.
  */
object CLISimulationController extends SimulationController:
  import model.simulation.states.SimulationModule.{Simulation, SimulationState}
  import model.race.RaceConstants.logicalTimeStep
  import model.tracks.TrackModule.Track
  import model.simulation.events.EventModule.Event
  import model.race.RacePhysicsModule.RacePhysics
  import view.SimulationDisplay
  import view.CLIDisplay

  given simState: SimulationState = SimulationState()
  given display: SimulationDisplay = CLIDisplay()
  given simInit: SimulationInitializer = SimulationInitializer()
  given track: Track = simInit.track
  given eventProcessor: EventProcessor = EventProcessor()
  given physics: RacePhysics = RacePhysics()

  /** @inheritdoc */
  override def init(): Unit =
    val initialState = simInit.initSimulationEntities()
    val simulation: Simulation[Unit] = loop()
    val finalState = simulation.runS(initialState).value

  /** @inheritdoc */
  override def step(): Simulation[Boolean] =
    for
      currentState <- simState.getState
      (eventsToProcess, dequeuedState) = currentState.dequeueAllAtCurrentTime(currentState.raceTime - 1.0)
      _ <- simState.setState(dequeuedState.advanceTime(logicalTimeStep))
      isEventQueueEmpty <- processEvents(eventsToProcess)
    yield isEventQueueEmpty

  /** @inheritdoc */
  override def loop(): Simulation[Unit] =
    for
      shallContinue <- step()
      result <- if shallContinue then loop() else simState.pure(())
    yield result

  private def loopWithSteps(steps: Int): Simulation[Unit] =
    for
      shallContinue <- step()
      result <- if shallContinue && steps > 0 then loopWithSteps(steps - 1) else simState.pure(())
    yield result

  private def processEvents(events: List[Event]): Simulation[Boolean] =
    for
      _ <- events.foldLeft(simState.pure(()))((acc, event) =>
        for
          _ <- acc
          state <- simState.getState
          _ <- simState.setState(eventProcessor.processEvent(state)(event))
        yield ()
      )
      finalState <- simState.getState
      _ = display.update(finalState)
    yield !finalState.isEventQueueEmpty

  private def processSingleEvent(maybeEvent: Option[Event]): Simulation[Boolean] =
    maybeEvent match
      case Some(event) =>
        for
          currentState <- simState.getState
          _ <- simState.setState(eventProcessor.processEvent(currentState)(event))
          s <- simState.getState
          _ = display.update(s)
        yield true
      case None => simState.pure(false)
