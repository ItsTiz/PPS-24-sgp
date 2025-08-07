package model.simulation.events.processor

import model.car.CarModule.Car
import model.car.TireModule.TireGenerator
import model.race.physics.RacePhysicsModule.RacePhysics
import model.simulation.events.EventModule.*
import model.simulation.events.logger.{EventContext, Logger}
import model.simulation.events.scheduler.EventScheduler
import model.simulation.states.RaceStateModule.RaceState
import model.simulation.states.CarStateModule.CarState
import model.simulation.weather.WeatherModule.*
import model.tracks.TrackSectorModule.TrackSector

private[processor] class EventProcessorImpl(using Physics: RacePhysics, Scheduler: EventScheduler,
    Logger: Logger[Event, EventContext]) extends EventProcessor:

  /** @inheritdoc */
  override def processEvent(state: RaceState)(event: Event): RaceState =
    Logger.log(event, EventContext.Processed)
    event match
      case CarProgressUpdate(carId, time) => updateCarPosition(state)(carId)
      case CarCompletedLap(carId, time) => updateCarLapCount(state)(carId)
      case TrackSectorEntered(carId, sector, time) => updateCarSector(state)(carId, sector)
      case PitStopRequest(carId, time) => serviceCar(state)(carId)
      case WeatherChanged(newWeather, time) => updateWeatherAndScheduleNext(state)(newWeather)

  private def updateCarPosition(state: RaceState)(carId: Int): RaceState =
    state.withCar(carId)((car, carState) =>
      if (!carState.hasCompletedRace(state.laps) && !carState.isOutOfFuel)
        val updatedCarState = Physics.advanceCar(car, carState)(state.weather)
        scheduleAndEnqueue(state.updateCar((car, updatedCarState)))(car, updatedCarState)
      else state
    )

  private def updateCarSector(state: RaceState)(carId: Int, newSector: TrackSector): RaceState =
    state.withCar(carId)((car, carState) => scheduleAndEnqueue(state)(car, carState.withNewSector(newSector)))

  private def updateCarLapCount(state: RaceState)(carId: Int): RaceState =
    state.withCar(carId)((car, carState) => state.updateCar((car, carState.withUpdatedLaps)).updateScoreboard(car))

  private def serviceCar(state: RaceState)(carId: Int): RaceState =
    val newTires = TireGenerator.getNewTireForWeather(state.weather)
    state.withCar(carId)((car, carState) => state.updateCar((car, carState.withReconditioning(newTires))))

  private def scheduleAndEnqueue(state: RaceState)(c: Car, updatedCarState: CarState): RaceState =
    val events = Scheduler.scheduleNextCarEvents((c, updatedCarState), state.raceTime)
    Logger.logAll(events, EventContext.Scheduled)
    state.updateCar((c, updatedCarState)).enqueueAll(events)

  private def updateWeatherAndScheduleNext(state: RaceState)(newWeather: Weather): RaceState =
    import model.race.RaceConstants.weatherChangeDuration
    val updatedWeather = state.updateWeather(newWeather)
    state.raceTime match
      case time if ((time - time % weatherChangeDuration) % weatherChangeDuration == 0) && !state.isRaceFinished =>
        val weatherEvent = Scheduler.scheduleNextWeatherEvent(time)
        Logger.log(weatherEvent, EventContext.Scheduled)
        updatedWeather.enqueueEvent(weatherEvent)
      case _ => updatedWeather
