package controller

import model.car.CarModule.Car
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
    *   a list of events that should occur next for the car
    */
  def scheduleNextEvents(carTuple: (Car, CarState), nextTime: BigDecimal): List[Event]

object EventScheduler:
  def apply()(using track: Track): EventScheduler = EventSchedulerImpl()

private class EventSchedulerImpl(using val track: Track) extends EventScheduler:
  import model.simulation.events.EventModule.*

  /** @inheritdoc */
  override def scheduleNextEvents(carTuple: (Car, CarState), nextTime: BigDecimal): List[Event] =
    // TODO add weather change
    carTuple match
      case (c, carState @ CarState(_, _, _, progress, _, _, sector)) =>
        if (carState.isOutOfFuel || carState.needsTireChange)
          List(PitStopRequest(c.carNumber, nextTime), CarProgressUpdate(c.carNumber, nextTime))
        else if carState.hasCompletedSector then
          Track.nextSector(track)(sector) match
            case Some(nextSector, circleCompleted) =>
              if circleCompleted then
                List(TrackSectorEntered(c.carNumber, nextSector, nextTime), CarCompletedLap(c.carNumber, nextTime))
              else
                List(TrackSectorEntered(c.carNumber, nextSector, nextTime))
            case None => Nil
        else
          List(CarProgressUpdate(c.carNumber, nextTime))
