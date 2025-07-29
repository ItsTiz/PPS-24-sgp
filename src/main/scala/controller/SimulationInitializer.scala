package controller

import model.car.CarModule.Car
import model.car.TireModule.Tire
import model.car.TireModule.TireType.*
import model.simulation.events.EventModule.*
import model.simulation.states.CarStateModule.CarState
import model.simulation.states.RaceStateModule.RaceState
import model.simulation.weather.WeatherModule.{Weather, WeatherGenerator}
import model.tracks.TrackModule.Track
import model.tracks.TrackSectorModule.TrackSector

sealed trait SimulationInitializer:

  val track: Track
  protected def initCars(): Map[Car, CarState]
  protected def initEvents(cars: List[Car], initialSector: TrackSector): List[Event]
  protected def initWeather(): Weather
  def initSimulationEntities(): RaceState

object SimulationInitializer:
  def apply(): SimulationInitializer = SimulationInitializerImpl

private object SimulationInitializerImpl extends SimulationInitializer:

  import model.race.RaceConstants.*
  import model.shared.Constants.minTireDegradeState
  import model.car.CarGenerator
  import model.tracks.TrackModule.TrackGenerator

  override val track: Track = TrackGenerator.generateMinimalTrack("Imola")

  // TODO make tires random maybe?
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

  override protected def initEvents(cars: List[Car], initialSector: TrackSector): List[Event] =
    cars.map(c => TrackSectorEntered(c.carNumber, initialSector, simulationTimeStart))

  override protected def initWeather(): Weather =
    WeatherGenerator.getRandomWeather

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
