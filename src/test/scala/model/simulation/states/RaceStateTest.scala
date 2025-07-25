package model.simulation.states

import model.car.CarModule.Car
import model.car.DriverModule.Driver
import model.car.DrivingStyleModule.DrivingStyle
import model.car.TireModule
import model.car.TireModule.Tire
import model.car.TireModule.TireType.Medium
import model.shared.Coordinate
import model.simulation.events.EventModule.{Event, TrackSectorEntered}
import model.simulation.states.CarStateModule.CarState
import model.simulation.states.RaceStateModule.RaceState
import model.simulation.weather.WeatherModule.Weather.*
import model.tracks.TrackSectorModule.TrackSector
import model.tracks.TrackSectorModule.TrackSector.straight
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.{equal, should}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class RaceStateTest extends AnyFlatSpec with BeforeAndAfterAll:
  val charles: Driver = Driver("Charles LeClerc", DrivingStyle.balanced)
  val max: Driver = Driver("Max Verstappen", DrivingStyle.balanced)
  val louis: Driver = Driver("Louis Hamilton", DrivingStyle.balanced)
  val lando: Driver = Driver("Lando Norris", DrivingStyle.balanced)
  val carf: Car = Car(
    model = "Ferrari",
    carNumber = 16,
    weightKg = 750.0,
    driver = charles,
    maxFuel = 100.0
  )
  val carf2: Car = Car(
    model = "Ferrari",
    carNumber = 44,
    weightKg = 750.0,
    driver = louis,
    maxFuel = 100.0
  )
  val carrb: Car = Car(
    model = "RedBull",
    carNumber = 1,
    weightKg = 750.0,
    driver = max,
    maxFuel = 100.0
  )
  val carml: Car = Car(
    model = "McLaren",
    carNumber = 4,
    weightKg = 750.0,
    driver = lando,
    maxFuel = 100.0
  )
  val cars: List[Car] = List(carf, carf2, carrb, carml)

  val trackStraight: TrackSector = straight(500, 320, 200, 1.0)

  val carStates: List[CarState] = cars.map(c =>
    CarState(
      maxFuel = c.maxFuel,
      fuelLevel = c.maxFuel, //cars start from max fuel
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
    val event: Event = TrackSectorEntered(carf.carNumber, trackStraight, 0.1)

    val newState: RaceState = RaceState(Map from (cars zip carStates), Sunny, 3).enqueueEvent(event)

    newState should equal(RaceState.withInitialEvents(Map from (cars zip carStates), List(event), Sunny, 3))

  it should "return events in reverse order as they were submitted" in:
    // TODO maybe revisit the quality of this code
    populateWithEvents(cars)

    val dequeuedEvents: ListBuffer[Event] = ListBuffer()
    var raceState: RaceState = validRaceState
    for event <- events do
      val (deq, state) = raceState.dequeueEvent
      raceState = state
      dequeuedEvents.addOne(deq.get)

    events should equal(dequeuedEvents)

//TODO rewrite this one!

//  it should "update correctly the car" in :
//    val newCarState = carStates.last.copyLike(
//      current
//    )
//
//    withUpdatedState(
//      250.0,
//      50.0,
//      20.0,
//      Coordinate(0.0, 0.0),
//      Tire(TireModule.TireType.Soft)
//    )
//    validRaceState.updateCar(newCarf)
//    println(validRaceState)
//
//    validRaceState.findCar(newCarf.carNumber).get should equal(newCarf)