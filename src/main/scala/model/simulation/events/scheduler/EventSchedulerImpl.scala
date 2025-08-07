package model.simulation.events.scheduler

import model.car.CarModule.Car
import model.simulation.events.EventModule.*
import model.simulation.events.logger.{EventContext, Logger}
import model.simulation.states.CarStateModule.CarState
import model.simulation.weather.WeatherModule.WeatherGenerator
import model.tracks.TrackModule.Track
import model.race.RaceConstants.{pitStopDuration, weatherChangeDuration}

private[scheduler] class EventSchedulerImpl(using val track: Track, Logger: Logger[Event, EventContext])
    extends EventScheduler:

  /** @inheritdoc */
  override def scheduleNextCarEvents(carTuple: (Car, CarState), nextTime: BigDecimal): List[Event] =
    carTuple match
      case (c, carState) if carState.isOutOfFuel || carState.needsTireChange => pitStopEvents(c.carNumber, nextTime)
      case (c, carState) if carState.hasCompletedSector => sectorTransitionEvents(c, carState, nextTime)
      case (c, _) => carProgressEvent(c.carNumber, nextTime)

  /** @inheritdoc */
  override def scheduleNextWeatherEvent(nextTime: BigDecimal): Event =
    WeatherChanged(WeatherGenerator.getRandomWeather, nextTime + weatherChangeDuration)

  private def pitStopEvents(carNumber: Int, time: BigDecimal): List[Event] =
    PitStopRequest(carNumber, time) :: CarProgressUpdate(carNumber, time + pitStopDuration) :: Nil

  private def sectorTransitionEvents(car: Car, carState: CarState, time: BigDecimal): List[Event] =
    val sector = carState.currentSector
    Track.nextSector(track)(carState.currentSector) match
      case Some((nextSector, completedLap)) =>
        val enterSectorEvent = TrackSectorEntered(car.carNumber, nextSector, time)
        val lapCompletedEvent = if completedLap then Some(CarCompletedLap(car.carNumber, time)) else None
        enterSectorEvent :: lapCompletedEvent.toList
      case None =>
        Logger.warn(s"Car ${car.carNumber} in unknown sector: $sector — no transition possible.")
        Nil

  private def carProgressEvent(carNumber: Int, nextTime: BigDecimal) =
    CarProgressUpdate(carNumber, nextTime) :: Nil
