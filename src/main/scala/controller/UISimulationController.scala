package controller

import controller.CLISimulationController.{processEvents, simState}
import model.race.RaceConstants.timeStepUI
import view.SimulationView

import java.util.{Timer, TimerTask}

/** UI implementation of [[SimulationController]] with monadic style. */
object UISimulationController extends SimulationController:
  import model.race.RaceConstants.logicalTimeStep
  import model.race.RacePhysicsModule.RacePhysics
  import model.simulation.events.EventModule.Event
  import model.simulation.states.SimulationModule.{Simulation, SimulationState}
  import model.simulation.states.RaceStateModule.RaceState
  import model.tracks.TrackModule.Track

  given simState: SimulationState = SimulationState()
  given simInit: SimulationInitializer = SimulationInitializer()
  given track: Track = simInit.track
  given eventProcessor: EventProcessor = EventProcessor()
  given physics: RacePhysics = RacePhysics()

  private var displayOpt: Option[SimulationView] = None

  /** Sets the simulation display component that will be used to render the simulation state.
    *
    * This method must be called before the simulation starts if UI updates are expected.
    *
    * @param view
    *   the [[SimulationView]] responsible for rendering the simulation visually
    */
  def setDisplay(view: SimulationView): Unit =
    displayOpt = Some(view)

  /** Starts the simulation from the given initial state.
    *
    * @param initialState
    *   the initial [[RaceState]] from which the simulation begins
    */
  def start(state: RaceState): Unit =
    val simulation = loop(state)

  /** @inheritdoc */
  override def init(): Unit =
    val initState = simInit.initSimulationEntities()
    start(initState)

  /** @inheritdoc */
  override def step(): Simulation[Boolean] =
    for
      currentState <- simState.getState
      (eventsToProcess, dequeuedState) = currentState.dequeueAllAtCurrentTime(currentState.raceTime)
      _ <- simState.setState(dequeuedState.advanceTime(logicalTimeStep))
      isEventQueueEmpty <- processEvents(eventsToProcess)
    yield isEventQueueEmpty

  /** Loops through simulation steps until the event queue is empty.
    *
    * @param initState
    *   the initial state to run the simulation on
    * @return
    *   a `Simulation[Unit]` representing the completed simulation.
    */
  def loop(initState: RaceState): Simulation[Unit] =
    simState.pure(loopR(initState, new Timer()))

  /** Loops through simulation steps until the event queue is empty.
    *
    * @return
    *   a `Simulation[Unit]` representing the completed simulation.
    */
  override def loop(): Simulation[Unit] = simState.pure(())

  private def loopR(newState: RaceState, timer: Timer): Unit =
    val task = new TimerTask:
      override def run(): Unit =
        val (nextState, continue) = stepAndUpdate(newState)
        if (continue) loopR(nextState, timer) else timer.cancel()
    timer.schedule(task, timeStepUI.toLong)

  private def stepAndUpdate(newState: RaceState) =
    val (nextState, continue) = step().run(newState).value
    updateUI(nextState)
    (nextState, continue || !nextState.isRaceFinished)

  private def updateStateWithEvents(events: List[Event]): Simulation[Unit] =
    events.foldLeft(simState.pure(()))((acc, event) =>
      for
        _ <- acc
        state <- simState.getState
        updated = applyEvent(state)(event)
        _ <- simState.setState(updated)
      yield ()
    )

  private def applyEvent(state: RaceState)(event: Event): RaceState =
    eventProcessor.processEvent(state)(event)

  private def processEvents(events: List[Event]): Simulation[Boolean] =
    for
      _ <- updateStateWithEvents(events)
      finalState <- simState.getState
    yield !finalState.isEventQueueEmpty

  private def updateUI(state: RaceState): Simulation[Unit] = displayOpt match
    case Some(display) => simState.pure(display.update(state))
    case None => simState.pure(())
