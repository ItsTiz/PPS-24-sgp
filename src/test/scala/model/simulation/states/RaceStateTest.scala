package model.simulation.states

import model.car.CarModule.Car
import model.car.{CarGenerator, TireModule}
import model.car.TireModule.Tire
import model.car.TireModule.TireType.Medium
import model.simulation.events.EventModule.{Event, TrackSectorEntered}
import model.simulation.states.CarStateModule.CarState
import model.simulation.states.RaceStateModule.RaceState
import model.simulation.weather.WeatherModule.Weather.*
import model.tracks.TrackSectorModule.TrackSector
import model.tracks.TrackSectorModule.TrackSector.straight
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers.not
import org.scalatest.matchers.should.Matchers.{equal, should}

class RaceStateTest extends AnyFlatSpec with BeforeAndAfterAll:
  val cars: List[Car] = CarGenerator.generateCars()

  val trackStraight: TrackSector = straight(0, 500, 320, 200, 1.0)

  val carStates: List[CarState] = cars.map(c =>
    CarState(
      maxFuel = c.maxFuel,
      fuelLevel = c.maxFuel, // cars start from max fuel
      currentSpeed = 0.0,
      progress = 0.0,
      tire = Tire(Medium, degradeState = 0.0),
      currentLaps = 0,
      currentSector = trackStraight
    )
  )

  var validRaceState: RaceState = RaceState(Map from (cars zip carStates), Sunny, 3)
  val events: List[Event] = cars.map(c => TrackSectorEntered(c.carNumber, trackStraight, 0.1))

  private def populateWithEvents(cars: List[Car]): Unit =
    for event <- events do
      validRaceState = validRaceState.enqueueEvent(event)

  "A RaceState" should "not have empty cars list" in:
    assertThrows[IllegalArgumentException]:
      RaceState(Map.empty, Sunny, 3)

  it should "return a correct RaceState after enqueueing" in:
    val event: Event = TrackSectorEntered(cars.last.carNumber, trackStraight, 0.1)

    val newState: RaceState = RaceState(Map from (cars zip carStates), Sunny, 3).enqueueEvent(event)

    newState should equal(RaceState.withInitialEvents(Map from (cars zip carStates), List(event), Sunny, 3))

  it should "return events in reverse order as they were submitted" in:
    val raceStateWithEvents = RaceState(Map from (cars zip carStates), Sunny, 3).enqueueAll(events)
    val (dequeuedEvents, _) = raceStateWithEvents.dequeueAll
    dequeuedEvents should equal(events)

  it should "update correctly the car" in:
    val targetCar = cars.last
    val originalCarState = carStates.last

    val updatedCarState = originalCarState.copyLike(
      currentSpeed = 250.0,
      fuelLevel = 50.0,
      tireDegradeState = 20.0,
      progress = 0.5
    )
    val updatedRaceState = validRaceState.updateCar((targetCar, updatedCarState))

    val foundCarTuple = updatedRaceState.findCar(targetCar.carNumber)
    foundCarTuple should not be None

    val (foundCar, foundCarState) = foundCarTuple.get
    foundCar should equal(targetCar)
    foundCarState.currentSpeed should equal(250.0)
    foundCarState.fuelLevel should equal(50.0)
    foundCarState.tire.degradeState should equal(20.0)
    foundCarState.progress should equal(0.5)
