package controller

import model.car.CarModule.Car
import model.race.RaceConstants.weatherChangeInterval
import model.simulation.events.EventModule.Event
import model.simulation.states.CarStateModule.CarState
import model.tracks.TrackModule.Track

trait EventScheduler:

  /** Schedules the next events for a car based on its current state.
    *
    * @param carTuple
    *   a tuple containing the car and its current state
    * @param nextTime
    *   the time at which the next events should occur
    * @return
    *   a list of [[Event]]s that should occur next for the car
    */
  def scheduleNextCarEvents(carTuple: (Car, CarState), nextTime: BigDecimal): List[Event]

  /** Generates and schedules the next weather event in the simulation timeline.
    *
    * @param nextTime
    *   the time at which the next events should occur
    *
    * @return
    *   a [[Event]] representing the next scheduled weather event
    */
  def scheduleNextWeatherEvent(nextTime: BigDecimal): Event

object EventScheduler:

  def apply()(using track: Track): EventScheduler = EventSchedulerImpl()

private class EventSchedulerImpl(using val track: Track) extends EventScheduler:
  import model.simulation.events.EventModule.*
  import model.simulation.weather.WeatherModule.WeatherGenerator

  /** @inheritdoc */
  override def scheduleNextCarEvents(carTuple: (Car, CarState), nextTime: BigDecimal): List[Event] =
    carTuple match
      case (c, carState) if carState.isOutOfFuel || carState.needsTireChange =>
        PitStopRequest(c.carNumber, nextTime) :: CarProgressUpdate(c.carNumber, nextTime) :: Nil

      case (c, carState @ CarState(_, _, _, _, _, _, sector)) if carState.hasCompletedSector =>
        Track.nextSector(track)(sector) match
          case Some(nextSector, circleCompleted) =>
            if circleCompleted then
              TrackSectorEntered(c.carNumber, nextSector, nextTime) :: CarCompletedLap(c.carNumber, nextTime) :: Nil
            else
              TrackSectorEntered(c.carNumber, nextSector, nextTime) :: Nil
          case None => Nil

      case (c, _) => CarProgressUpdate(c.carNumber, nextTime) :: Nil

  /** @inheritdoc */
  override def scheduleNextWeatherEvent(nextTime: BigDecimal): Event =
    WeatherChanged(WeatherGenerator.getRandomWeather, nextTime + weatherChangeInterval)
