package controller.ui

import controller.SimulationController
import controller.engine.SimulationEngine
import controller.scheduler.SimulationScheduler
import model.race.physics.RacePhysicsModule.RacePhysics
import model.simulation.events.EventModule.Event
import model.simulation.events.logger.{EventContext, EventFilter, EventLogger, Logger}
import model.simulation.events.processor.EventProcessor
import model.simulation.events.scheduler.EventScheduler
import model.simulation.init.SimulationInitializer
import model.simulation.states.RaceStateModule.RaceState
import model.simulation.states.SimulationModule.{Simulation, SimulationState}
import model.tracks.TrackModule.Track
import view.SimulationView

/** UI implementation of [[SimulationController]] with monadic style. */
object UISimulationController extends SimulationController:

  given track: Track = simInit.track
  given RacePhysics = RacePhysics()
  given Logger[Event, EventContext] = EventLogger(EventFilter.skipCarProgress)
  private given EventScheduler = EventScheduler()
  private given eventProcessor: EventProcessor = EventProcessor()
  private given simState: SimulationState = SimulationState()

  private val simInit: SimulationInitializer = SimulationInitializer()
  private val engine: SimulationEngine = SimulationEngine()
  private val scheduler: SimulationScheduler = SimulationScheduler()
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
    * @param state
    *   the initial [[RaceState]] from which the simulation begins
    */
  def start(state: RaceState): Unit =
    loop(state)

  /** @inheritdoc */
  override def init(): Unit =
    start(simInit.initSimulationEntities())

  /** @inheritdoc */
  override def step(): Simulation[Boolean] =
    engine.executeStep()

  /** Loops through simulation steps until the event queue is empty.
    *
    * @param initState
    *   the initial state to run the simulation on
    * @return
    *   a `Simulation[Unit]` representing the completed simulation.
    */
  def loop(initState: RaceState): Simulation[Unit] =
    simState.pure(scheduler.startSimulation(initState, runStep, updateUI))

  /** Loops through simulation steps until the event queue is empty.
    *
    * @return
    *   a `Simulation[Unit]` representing the completed simulation.
    */
  override def loop(): Simulation[Unit] = simState.pure(())

  private def runStep(state: RaceState): (RaceState, Boolean) =
    step().run(state).value

  private def updateUI(state: RaceState): Unit =
    displayOpt.foreach(_.update(state))
