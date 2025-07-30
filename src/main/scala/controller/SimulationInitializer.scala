package controller

import model.car.CarModule.Car
import model.simulation.events.EventModule.Event
import model.simulation.states.CarStateModule.CarState
import model.simulation.states.RaceStateModule.RaceState
import model.simulation.weather.WeatherModule.Weather
import model.tracks.TrackModule.Track
import model.tracks.TrackSectorModule.TrackSector

/** Trait responsible for initializing all simulation-related entities. */
sealed trait SimulationInitializer:

  /** The track used in the simulation. */
  val track: Track

  /** Initializes the cars and their respective initial states.
    *
    * @return
    *   a map associating each `Car` with its `CarState`
    */
  protected def initCars(): Map[Car, CarState]

  /** Initializes the list of starting simulation events.
    *
    * @param cars
    *   list of participating cars
    * @param initialSector
    *   the track sector where the simulation begins
    * @return
    *   a list of initial simulation events
    */
  protected def initEvents(cars: List[Car], initialSector: TrackSector): List[Event]

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

private object SimulationInitializerImpl extends SimulationInitializer:

  import model.simulation.events.EventModule.TrackSectorEntered
  import model.race.RaceConstants.*
  import model.shared.Constants.minTireDegradeState
  import model.car.CarGenerator
  import model.car.TireModule.Tire
  import model.car.TireModule.TireType.*
  import model.tracks.TrackModule.TrackGenerator
  import model.simulation.weather.WeatherModule.WeatherGenerator

  /** @inheritdoc */
  override val track: Track = TrackGenerator.generateMinimalTrack("Imola")

  /** @inheritdoc */
  override protected def initCars(): Map[Car, CarState] =
    val cars: List[Car] = CarGenerator.generateCars()
    getFirstTrackSector match
      case Some(initialSector) =>
        val carStates: List[CarState] = cars.map(c =>
          CarState(
            maxFuel = c.maxFuel,
            fuelLevel = c.maxFuel, // cars start from max fuel
            currentSpeed = 0.0,
            progress = minSectorProgress,
            tire = Tire(Medium, minTireDegradeState),
            currentLaps = lapsStartCount,
            currentSector = initialSector
          )
        )
        Map from (cars zip carStates)
      case None => Map.empty

  /** @inheritdoc */
  override protected def initEvents(cars: List[Car], initialSector: TrackSector): List[Event] =
    cars.map(c => TrackSectorEntered(c.carNumber, initialSector, simulationTimeStart))

  /** @inheritdoc */
  override protected def initWeather(): Weather =
    WeatherGenerator.getRandomWeather

  /** @inheritdoc */
  override def initSimulationEntities(): RaceState =
    val cars = initCars()
    getFirstTrackSector match
      case Some(sector) =>
        RaceState.withInitialEvents(
          cars,
          initEvents(cars.keys.toList, sector),
          initWeather(),
          totalLaps
        )
      case None => RaceState(cars, initWeather(), totalLaps)

  private def getFirstTrackSector: Option[TrackSector] =
    Track.getSectorAt(track, 0)
