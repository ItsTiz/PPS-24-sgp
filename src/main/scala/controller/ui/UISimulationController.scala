package controller.ui

import controller.SimulationController
import model.simulation.states.RaceStateModule.RaceState
import model.simulation.states.SimulationModule.Simulation
import controller.assembler.SimulationAssembler
import model.weather.WeatherModule.Weather
import model.tracks.TrackModule.TrackType
import view.simulation.SimulationView

/** UI implementation of [[SimulationController]] with monadic style. */
class UISimulationController(assembler: SimulationAssembler) extends SimulationController, SimulationConfigListener:

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
  override def init(carsNumber: Int, laps: Int, weather: Weather, trackType: TrackType): Unit =
    start(assembler.initSimulationEntities(carsNumber, laps, weather, trackType))

  /** @inheritdoc */
  override def step(): Simulation[Boolean] =
    assembler.executeStep()

  /** Loops through simulation steps until the event queue is empty.
    *
    * @param initState
    *   the initial state to run the simulation on
    * @return
    *   a `Simulation[Unit]` representing the completed simulation.
    */
  def loop(initState: RaceState): Simulation[Unit] =
    assembler.pure(assembler.startSimulation(initState, runStep, updateUI))

  /** Loops through simulation steps until the event queue is empty.
    *
    * @return
    *   a `Simulation[Unit]` representing the completed simulation.
    */
  override def loop(): Simulation[Unit] = assembler.pure(())

  /** @inheritdoc */
  override def onSimulationConfigured(laps: Int, cars: Int, weather: Weather, trackType: TrackType): Unit =
    init(cars, laps, weather, trackType)

  private def runStep(state: RaceState): (RaceState, Boolean) =
    step().run(state).value

  private def updateUI(state: RaceState): Unit =
    displayOpt.foreach(_.update(state))
