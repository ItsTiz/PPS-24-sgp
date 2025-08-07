package model.simulation.events.scheduler

import model.car.CarModule.Car
import model.simulation.events.EventModule.Event
import model.simulation.events.logger.{EventContext, Logger}
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

  def apply()(using track: Track, Logger: Logger[Event, EventContext]): EventScheduler = EventSchedulerImpl()
