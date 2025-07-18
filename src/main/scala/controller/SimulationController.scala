package controller
import controller.EventProcessorImpl.EventProcessor
import model.simulation.states.StateModule.State
import controller.SimulationModule.{Simulation, SimulationState}
import model.simulation.events.EventModule
import model.simulation.events.EventModule.{Event, TrackSectorEntered}
import model.tracks.TrackSectorModule.TrackSector
import model.tracks.TrackSectorModule.TrackSector.straight

trait SimulationController:

  def init(): Unit
  def step(): Simulation[Boolean]
  def loop(): Simulation[Unit]

  object SimulationController:
    def apply(): SimulationController = SimulationControllerImpl

object SimulationControllerImpl extends SimulationController:

  import model.simulation.states.RaceStateModule.RaceState
  import model.simulation.weather.WeatherModule.Weather.*
  import model.car.CarModule.Car
  import model.car.CarGenerator
  import controller.SimulationModule.simulationMonad
  import view.SimulationDisplay
  import view.CLIDisplay
  import scalaz.Scalaz.*

  init()

  given simState: SimulationState = SimulationState()
  given display: SimulationDisplay = CLIDisplay()
  given eventProcessor: EventProcessor = EventProcessor()

  override def init(): Unit =
    val cars: List[Car] = CarGenerator.generateCars()
    val trackStraight: TrackSector = straight(320, 200, 4)
    val events: List[Event] = cars.map(c => TrackSectorEntered(c.carNumber, trackStraight, 0.1))
    val initialState: RaceState = RaceState.withInitialEvents(cars, events, weather = Sunny)
    val simulation: Simulation[Unit] = loop()
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

  private def dispatchEventProcessing(maybeEvent: Option[Event]): Simulation[Boolean] =
    maybeEvent match
      case Some(event) =>
        for
//          currentState <- simState.getState
//          _ <- simState.setState(eventProcessor.processEvent(currentState)(event))
          s <- simState.getState
          _ = display.update(s)
        yield true
      case None => State(s => (s, false))
