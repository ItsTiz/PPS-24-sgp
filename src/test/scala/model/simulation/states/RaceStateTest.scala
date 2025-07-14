package model.simulation.states

import model.car.CarModule.Car
import model.car.DriverModule.Driver
import model.car.DrivingStyleModule.DrivingStyle
import model.shared.Coordinate
import model.simulation.events.EventModule.{Event, TrackSectorEntered}
import model.simulation.states.RaceStateModule.RaceState
import model.tracks.TrackSectorModule.TrackSector
import model.tracks.TrackSectorModule.TrackSector.straight
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.{equal, should}

import scala.collection.immutable.Queue
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
    maxFuel = 100.0,
    fuelLevel = 50.0,
    degradeState = 20.0,
    currentSpeed = 200.0,
    position = Coordinate(0.0, 0.0)
  )
  val carf2: Car = Car(
    model = "Ferrari",
    carNumber = 44,
    weightKg = 750.0,
    driver = louis,
    maxFuel = 100.0,
    fuelLevel = 50.0,
    degradeState = 20.0,
    currentSpeed = 200.0,
    position = Coordinate(0.0, 0.0)
  )
  val carrb: Car = Car(
    model = "RedBull",
    carNumber = 1,
    weightKg = 750.0,
    driver = max,
    maxFuel = 100.0,
    fuelLevel = 50.0,
    degradeState = 20.0,
    currentSpeed = 200.0,
    position = Coordinate(0.0, 0.0)
  )
  val carml: Car = Car(
    model = "McLaren",
    carNumber = 4,
    weightKg = 750.0,
    driver = lando,
    maxFuel = 100.0,
    fuelLevel = 50.0,
    degradeState = 20.0,
    currentSpeed = 200.0,
    position = Coordinate(0.0, 0.0)
  )
  val cars: List[Car] = List(carf, carf2, carrb, carml)

  val trackStraight: TrackSector = straight(320, 200, 4)

  var validRaceState: RaceState = RaceState(cars)
  val events: List[Event] = cars.map(c => TrackSectorEntered(c, trackStraight, 0.1))

  private def populateWithEvents(cars: List[Car]): Unit =
    for event <- events do
      validRaceState = validRaceState.enqueueEvent(event)

  "A RaceState" should "not have empty cars list" in:
    assertThrows[IllegalArgumentException]:
      RaceState(List())

  it should "return a correct RaceState after enqueueing" in:
    val event: Event = TrackSectorEntered(carf, trackStraight, 0.1)

    val newState: RaceState = RaceState(List(carf)).enqueueEvent(event)

    newState should equal(RaceState.withInitialEvents(List(carf), Queue(event)))

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

  it should "update correctly the car" in:
    val newCarf = carf.withUpdatedState(
      250.0,
      50.0,
      20.0,
      Coordinate(0.0, 0.0)
    )
    validRaceState.updateCar(newCarf)
    println(validRaceState)

    validRaceState.car(newCarf).get should equal(newCarf)
