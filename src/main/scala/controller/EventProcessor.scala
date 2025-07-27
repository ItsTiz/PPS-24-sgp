package controller

import model.car.CarModule.Car
import model.race.RacePhysicsModule.RacePhysics
import model.simulation.events.EventModule
import model.simulation.events.EventModule.*
import model.simulation.states.CarStateModule.CarState
import model.simulation.states.RaceStateModule.RaceState
import model.tracks.TrackModule.Track
import model.tracks.TrackSectorModule.TrackSector

trait EventProcessor:
  def processEvent(state: RaceState)(event: Event): RaceState
  def scheduleNextEvents(carTuple: (Car, CarState), nextTime: BigDecimal): List[Event]

object EventProcessor:
  def apply()(using track: Track): EventProcessor = new EventProcessorImpl

private class EventProcessorImpl(using val track: Track) extends EventProcessor:

  // TODO does physics need to be here?
  given physics: RacePhysics = RacePhysics()

  override def processEvent(state: RaceState)(event: Event): RaceState = event match
    case CarProgressUpdate(carId, time) => updateState(state)(carId)
    case CarCompletedLap(carId, time) => updateCarLapCount(state)(carId)
    case TrackSectorEntered(carId, sector, time) => updateCarSector(state)(carId, sector)
    case TrackSectorExited(carId, time) => ???
    case PitStopRequest(carId, time) => ???
    case WeatherChanged(weather, time) => ???

  private def updateState(state: RaceState)(carId: Int): RaceState =
    state.findCar(carId) match
      case Some(c, cs) =>
        val updatedCarState = physics.advanceCar(c, cs)(state.weather)
        val events = scheduleNextEvents((c, updatedCarState), state.raceTime + 0.1) // TODO magic numbers!
        state.updateCar((c, updatedCarState)).enqueueAll(events)
      case None => state

  private def updateCarSector(state: RaceState)(carId: Int, newSector: TrackSector): RaceState =
    state.findCar(carId) match
      case Some(c, cs) =>
        val updatedCarState = cs.copyLike(progress = 0, currentSector = newSector)
        val events = scheduleNextEvents((c, updatedCarState), state.raceTime + 0.1) // TODO magic numbers!
        state.updateCar((c, updatedCarState)).enqueueAll(events)
      case None => state

  private def updateCarLapCount(state: RaceState)(carId: Int): RaceState =
    state.findCar(carId) match
      case Some(c, cs) => state.updateCar((c, cs.copyLike(progress = 0, currentLaps = cs.currentLaps + 1)))
      case None => state

  override def scheduleNextEvents(carTuple: (Car, CarState), nextTime: BigDecimal): List[Event] =
    carTuple match
      case (c, CarState(_, _, _, progress, time, laps, sector)) =>
        if progress == 1.0 then // TODO magic numbers!
          Track.nextSector(track)(sector) match
            case Some(nextSector, circleCompleted) =>
              if circleCompleted then
                List(
                  TrackSectorEntered(c.carNumber, nextSector, nextTime),
                  CarCompletedLap(c.carNumber, nextTime)
                )
              else
                List(
                  TrackSectorEntered(c.carNumber, nextSector, nextTime)
                )
            case None => List()
        else
          List(CarProgressUpdate(c.carNumber, nextTime))
