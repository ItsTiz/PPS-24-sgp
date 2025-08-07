package model.simulation.init

import model.car.CarModule.Car
import model.car.CarGenerator
import model.car.TireModule.TireGenerator
import model.simulation.states.CarStateModule.CarState
import model.simulation.states.RaceStateModule.RaceState
import model.simulation.weather.WeatherModule.{Weather, WeatherGenerator}
import model.simulation.events.EventModule.{Event, WeatherChanged, TrackSectorEntered}
import model.tracks.TrackModule.{Track, TrackGenerator}
import model.tracks.TrackSectorModule.TrackSector
import model.race.RaceConstants.*

private[init] object SimulationInitializerImpl extends SimulationInitializer:

  /** @inheritdoc */
  override val track: Track = TrackGenerator.generateSimpleTrack("Imola")

  /** @inheritdoc */
  override protected def initCars(carsNumber: Int, weather: Weather): Map[Car, CarState] =
    val cars: List[Car] = CarGenerator.generateCars().take(carsNumber)
    getFirstTrackSector match
      case Some(initialSector) =>
        val carStates: List[CarState] = cars.map(c =>
          CarState(
            maxFuel = c.maxFuel,
            fuelLevel = c.maxFuel, // cars start from max fuel
            currentSpeed = 0.0,
            progress = minSectorProgress,
            tire = TireGenerator.getNewTireForWeather(weather),
            currentLaps = lapsStartCount,
            currentSector = initialSector
          )
        )
        Map from (cars zip carStates)
      case None => Map.empty

  /** @inheritdoc */
  override protected def initEvents(cars: List[Car], initialSector: TrackSector, weather: Weather): List[Event] =
    cars.map(c => TrackSectorEntered(c.carNumber, initialSector, simulationTimeStart))
      ++ (WeatherChanged(weather, simulationTimeStart + weatherChangeDuration) :: Nil)

  /** @inheritdoc */
  override protected def initWeather(): Weather =
    WeatherGenerator.getRandomWeather

  /** @inheritdoc */
  override def initSimulationEntities(carsNumber: Int, laps: Int = totalLaps, weather: Weather): RaceState =

    val cars = initCars(carsNumber: Int, weather)
    getFirstTrackSector match
      case Some(initSector) =>
        RaceState.withInitialEvents(
          cars,
          initEvents(cars.keys.toList, initSector, weather),
          weather,
          laps
        )
      case None => RaceState(cars, weather, laps)

  private def getFirstTrackSector: Option[TrackSector] =
    Track.getSectorAt(track, 0)
