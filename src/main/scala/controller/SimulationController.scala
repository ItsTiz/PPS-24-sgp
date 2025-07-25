package controller

import controller.EventProcessor
import model.simulation.states.StateModule.State
import controller.SimulationModule.{Simulation, SimulationState}
import model.simulation.events.EventModule.Event
import model.tracks.TrackModule.Track

trait SimulationController:

  def init(): Unit
  def step(): Simulation[Boolean]
  def loop(): Simulation[Unit]

  object SimulationController:

    def apply(): SimulationController = SimulationControllerImpl

object SimulationControllerImpl extends SimulationController:

  import controller.SimulationModule.simulationMonad
  import view.SimulationDisplay
  import view.CLIDisplay
  import scalaz.Scalaz.*

  init()

  given simState: SimulationState = SimulationState()
  given display: SimulationDisplay = CLIDisplay()
  given simInit: SimulationInitializer = SimulationInitializer()
  given track: Track = simInit.track
  given eventProcessor: EventProcessor = EventProcessor()

  override def init(): Unit =
    val initialState = simInit.initSimulationEntities()
    val simulation: Simulation[Unit] = loopWithSteps(20)
    val (finalState, _) = simulation.run(initialState)

  override def step(): Simulation[Boolean] =
    for
      currentState <- simState.getState
      (maybeEvent, dequeuedState) = currentState.dequeueEvent
      _ <- simState.setState(dequeuedState.advanceTime(0.1))
      isEventQueueEmpty <- dispatchEventProcessing(maybeEvent)
    yield isEventQueueEmpty

  override def loop(): Simulation[Unit] =
    for
      shallContinue <- step()
      result <- if shallContinue then loop() else State.empty
    yield result

  private def loopWithSteps(steps: Int): Simulation[Unit] =
    for
      shallContinue <- step()
      result <- if shallContinue && steps > 0 then loopWithSteps(steps - 1) else State.empty
    yield result

  private def dispatchEventProcessing(maybeEvent: Option[Event]): Simulation[Boolean] =
    maybeEvent match
      case Some(event) =>
        for
          currentState <- simState.getState
          _ <- simState.setState(eventProcessor.processEvent(currentState)(event))
          s <- simState.getState
          _ = display.update(s)
        yield true
      case None => State(s => (s, false))
