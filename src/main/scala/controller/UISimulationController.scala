package controller

import model.race.RaceConstants.timeStepUI
import scalafx.application.{Platform}
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
  private var displayOpt: Option[SimulationView] = None
  given eventProcessor: EventProcessor = EventProcessor()
  given physics: RacePhysics = RacePhysics()

  /** @inheritdoc */
  override def init(): Unit =
    val initialState = simInit.initSimulationEntities()
    start(initialState)

  def start(initialState: RaceState): Unit =
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
    simState.pure(loopR(simInit.initSimulationEntities(), new Timer()))

  private def loopR(newState: RaceState, timer: Timer): Unit =
    val task = new TimerTask:
      override def run(): Unit =
        val (nextState, continue) = step().run(newState).value
        updateUIThreadSafe(nextState)
        if (continue) loopR(nextState, timer) else timer.cancel()
    timer.schedule(task, timeStepUI.toLong)

  private def processEvent(maybeEvent: Option[Event]): Simulation[Boolean] =
    maybeEvent match
      case Some(event) =>
        for
          currentState <- simState.getState
          _ <- simState.setState(eventProcessor.processEvent(currentState)(event))
        yield true
      case None => simState.pure(false)

  def setDisplay(view: SimulationView): Unit =
    displayOpt = Some(view)

  private def updateUIThreadSafe(state: RaceState): Simulation[Unit] = displayOpt match
    case Some(display) => simState.pure(displayOpt.get.update(state))
    case None => simState.pure(())
