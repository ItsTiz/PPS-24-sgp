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

/** Factory for creating [[EventProcessor]] instances. */
object EventProcessor:
  /** Creates a new EventProcessor instance.
    *
    * @param physics
    *   the physics engine used for race calculations
    * @param track
    *   the track used for the race
    * @return
    *   a new EventProcessor instance
    */
  def apply()(using physics: RacePhysics, track: Track): EventProcessor = new EventProcessorImpl

private class EventProcessorImpl(using val physics: RacePhysics, val track: Track) extends EventProcessor:
  import model.race.RaceConstants.logicalTimeStep
  import model.tracks.TrackSectorModule.TrackSector
  import model.simulation.events.EventModule.*

  given eventScheduler: EventScheduler = EventScheduler()

  /** @inheritdoc */
  override def processEvent(state: RaceState)(event: Event): RaceState = event match
    case CarProgressUpdate(carId, time) => updateCarPosition(state)(carId)
    case CarCompletedLap(carId, time) => updateCarLapCount(state)(carId)
    case TrackSectorEntered(carId, sector, time) => updateCarSector(state)(carId, sector)
    case PitStopRequest(carId, time) => serviceCar(state)(carId)
    case WeatherChanged(newWeather, time) => state.updateWeather(newWeather)

  private def updateCarPosition(state: RaceState)(carId: Int): RaceState =
    state.withCar(carId)((car, carState) =>
      if (!carState.hasCompletedRace(state.laps) && !carState.isOutOfFuel)
        val updatedCarState = physics.advanceCar(car, carState)(state.weather)
        scheduleAndEnqueue(state.updateCar((car, updatedCarState)))(car, updatedCarState)
      else
        state
    )

  private def scheduleAndEnqueue(state: RaceState)(c: Car, updatedCarState: CarState): RaceState =
    val events: List[Event] = eventScheduler.scheduleNextEvents((c, updatedCarState), state.raceTime + logicalTimeStep)
    state.updateCar((c, updatedCarState)).enqueueAll(events)

  private def updateCarSector(state: RaceState)(carId: Int, newSector: TrackSector): RaceState =
    state.withCar(carId)((car, carState) => scheduleAndEnqueue(state)(car, carState.withNewSector(newSector)))

  private def updateCarLapCount(state: RaceState)(carId: Int): RaceState =
    state.withCar(carId)((car, carState) => state.updateCar((car, carState.withUpdatedLaps)))

  private def serviceCar(state: RaceState)(carId: Int): RaceState =
    state.withCar(carId)((car, carState) => state.updateCar((car, carState.withReconditioning)))
