package controller

import model.car.CarModule.Car
import model.race.RacePhysicsModule.RacePhysics
import model.simulation.events.EventModule.Event
import model.simulation.states.CarStateModule.CarState
import model.simulation.states.RaceStateModule.RaceState
import model.tracks.TrackModule.Track

trait EventProcessor:
  /** Processes a single event and updates the race state accordingly.
    *
    * @param state
    *   the current state of the race
    * @param event
    *   the event to be processed
    * @return
    *   an updated race state after processing the event
    */
  def processEvent(state: RaceState)(event: Event): RaceState

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

/** Factory for creating [[EventProcessor]] instances. */
object EventProcessor:
  /** Creates a new EventProcessor instance.
    *
    * @param track
    *   the track on which the race is taking place
    * @param physics
    *   the physics engine used for race calculations
    * @return
    *   a new EventProcessor instance
    */
  def apply()(using track: Track)(using physics: RacePhysics): EventProcessor = new EventProcessorImpl

private class EventProcessorImpl(using val track: Track)(using val physics: RacePhysics) extends EventProcessor:
  import model.race.RaceConstants.timeStep
  import model.tracks.TrackSectorModule.TrackSector
  import model.simulation.events.EventModule.*

  override def processEvent(state: RaceState)(event: Event): RaceState = event match
    case CarProgressUpdate(carId, time) => updateCarPosition(state)(carId)
    case CarCompletedLap(carId, time) => updateCarLapCount(state)(carId)
    case TrackSectorEntered(carId, sector, time) => updateCarSector(state)(carId, sector)
    case PitStopRequest(carId, time) => fixUpCar(state)(carId)
    case WeatherChanged(newWeather, time) => state.updateWeather(newWeather)

  private def updateCarPosition(state: RaceState)(carId: Int): RaceState =
    state.withCar(carId)((car, carState) =>
      if (carState.currentLaps != state.laps && !carState.isOutOfFuel)
        val updatedCarState = physics.advanceCar(car, carState)(state.weather)
        scheduleAndEnqueue(state.updateCar((car, updatedCarState)))(car, updatedCarState)
      else
        state
    )

  private def scheduleAndEnqueue(state: RaceState)(c: Car, updatedCarState: CarState): RaceState =
    val events: List[Event] = scheduleNextEvents((c, updatedCarState), state.raceTime + timeStep)
    state.updateCar((c, updatedCarState)).enqueueAll(events)

  private def updateCarSector(state: RaceState)(carId: Int, newSector: TrackSector): RaceState =
    state.withCar(carId)((car, carState) => scheduleAndEnqueue(state)(car, carState.withNewSector(newSector)))

  private def updateCarLapCount(state: RaceState)(carId: Int): RaceState =
    state.withCar(carId)((car, carState) => state.updateCar((car, carState.withUpdatedLaps)))

  private def fixUpCar(state: RaceState)(carId: Int): RaceState =
    state.withCar(carId)((car, carState) => state.updateCar((car, carState.withReconditioning)))

  // TODO evaluate if it should be defined in an another object - SRP
  override def scheduleNextEvents(carTuple: (Car, CarState), nextTime: BigDecimal): List[Event] =
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
