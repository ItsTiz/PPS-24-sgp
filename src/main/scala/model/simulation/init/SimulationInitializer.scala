package model.simulation.init

import model.car.CarModule.Car
import model.simulation.events.EventModule.{Event, WeatherChanged}
import model.simulation.states.CarStateModule.CarState
import model.simulation.states.RaceStateModule.RaceState
import model.simulation.weather.WeatherModule.Weather
import model.tracks.TrackModule.Track
import model.tracks.TrackSectorModule.TrackSector

/** Trait responsible for initializing all simulation-related entities. */
trait SimulationInitializer:

  /** The track used in the simulation. */
  val track: Track

  /** Initializes the cars and their respective initial states.
    *
    * @return
    *   a map associating each `Car` with its `CarState`
    */
  protected def initCars(weather: Weather): Map[Car, CarState]

  /** Initializes the list of starting simulation events.
    *
    * @param cars
    *   list of participating cars
    * @param initialSector
    *   the track sector where the simulation begins
    * @param weather
    *   the initial weather for the simulation
    * @return
    *   a list of initial simulation events
    */
  protected def initEvents(cars: List[Car], initialSector: TrackSector, weather: Weather): List[Event]

  /** Initializes the starting weather condition.
    *
    * @return
    *   the initial `Weather`
    */
  protected def initWeather(): Weather

  /** Aggregates the initialized cars, weather, and events into the initial `RaceState`.
    *
    * @return
    *   a fully-initialized `RaceState` object representing the start of the simulation
    */
  def initSimulationEntities(): RaceState

/** Factory method for [[SimulationInitializer]]. */
object SimulationInitializer:
  def apply(): SimulationInitializer = SimulationInitializerImpl