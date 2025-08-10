package controller

import controller.assembler.SimulationAssembler
import controller.ui.UISimulationController
import model.simulation.states.SimulationModule.Simulation
import model.weather.WeatherModule.Weather
import model.tracks.TrackModule.TrackType

/** Controller trait for managing the simulation lifecycle. */
trait SimulationController:

  /** Initializes the simulation components and prepares the initial state. */
  def init(carsNumber: Int, laps: Int, weather: Weather, trackType: TrackType): Unit

  /** Advances the simulation by one step.
    *
    * @return
    *   a `Simulation[Boolean]` indicating whether the simulation should continue.
    */
  def step(): Simulation[Boolean]

  /** Loops through simulation steps until the event queue is empty.
    *
    * @return
    *   a `Simulation[Unit]` representing the completed simulation.
    */
  def loop(): Simulation[Unit]

/** Factory method for [[SimulationController]].
  */
object SimulationController:
  def apply(assembler: SimulationAssembler): SimulationController = UISimulationController(assembler)
