package model.simulation.states

import model.car.CarModule.Car
import model.car.{CarGenerator, TireModule}
import model.car.TireModule.Tire
import model.car.TireModule.TireType.Medium
import model.simulation.events.EventModule.{Event, TrackSectorEntered}
import model.simulation.states.CarStateModule.CarState
import model.simulation.states.RaceStateModule.RaceState
import model.weather.WeatherModule.Weather.*
import model.tracks.TrackSectorModule.TrackSector
import model.tracks.TrackSectorModule.TrackSector.straight
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers._

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

  it should "return correct values for basic state properties" in:
    validRaceState.raceTime should equal(0)
    validRaceState.weather should equal(Sunny)
    validRaceState.laps should equal(3)
    validRaceState.cars should contain theSameElementsAs cars
    validRaceState.carStates.map(_.currentSpeed) should contain only 0.0
    validRaceState.scoreboard should not be null

  it should "return a correct RaceState after enqueueing" in:
    val event: Event = TrackSectorEntered(cars.last.carNumber, trackStraight, 0.1)

    val newState: RaceState = RaceState(Map from (cars zip carStates), Sunny, 3).enqueueEvent(event)

    newState should equal(RaceState.withInitialEvents(Map from (cars zip carStates), List(event), Sunny, 3))

  it should "dequeue one event correctly" in:
    val stateWithEvents = validRaceState.enqueueEvent(events.head)
    val (maybeEvent, newState) = stateWithEvents.dequeueEvent
    maybeEvent should not be empty
    maybeEvent.get should equal(events.head)
    newState.events shouldBe empty

  it should "return events in reverse order as they were submitted" in:
    val raceStateWithEvents = RaceState(Map from (cars zip carStates), Sunny, 3).enqueueAll(events)
    val (dequeuedEvents, _) = raceStateWithEvents.dequeueAll
    dequeuedEvents should equal(events)

  it should "dequeue all events at current simulation time correctly" in:
    val timestampedEvents = List(
      TrackSectorEntered(cars.head.carNumber, trackStraight, 0),
      TrackSectorEntered(cars(1).carNumber, trackStraight, 0),
      TrackSectorEntered(cars(2).carNumber, trackStraight, 1)
    )
    val withEvents = RaceState.withInitialEvents(Map from (cars zip carStates), timestampedEvents, Sunny, 3)
    val (toProcess, remainingState) = withEvents.dequeueAllAtCurrentTime(0)
    val carNumbersAtTime0 = toProcess.collect {
      case TrackSectorEntered(carNumber, _, _) => carNumber
    }

    carNumbersAtTime0 should contain theSameElementsAs List(cars.head.carNumber, cars(1).carNumber)

    val remainingCarNumbers = remainingState.events.collect {
      case TrackSectorEntered(carNumber, _, _) => carNumber
    }

    remainingCarNumbers should contain only cars(2).carNumber

  it should "return correctly whether the event queue is empty" in:
    val emptyState = RaceState(Map from (cars zip carStates), Sunny, 3)
    emptyState.isEventQueueEmpty shouldBe true

    val nonEmptyState = emptyState.enqueueEvent(events.head)
    nonEmptyState.isEventQueueEmpty shouldBe false

  it should "detect when all cars have finished the race" in:
    val finishedStates = carStates.map(_.copyLike(currentLaps = 3))
    val finishedRace = RaceState(Map from (cars zip finishedStates), Sunny, 3)
    finishedRace.isRaceFinished shouldBe true

    val unfinishedStates = carStates.updated(0, carStates.head.copyLike(currentLaps = 2))
    val unfinishedRace = RaceState(Map from (cars zip unfinishedStates), Sunny, 3)
    unfinishedRace.isRaceFinished shouldBe false

  it should "update correctly the car" in:
    val targetCar = cars.last
    val originalCarState = carStates.last

    val updatedCarState = originalCarState.copyLike(
      currentSpeed = 250.0,
      fuelLevel = 50.0,
      progress = 0.5
    )
    val updatedRaceState = validRaceState.updateCar((targetCar, updatedCarState))

    val foundCarTuple = updatedRaceState.findCar(targetCar.carNumber)
    foundCarTuple should not be None

    val (foundCar, foundCarState) = foundCarTuple.get
    foundCar should equal(targetCar)
    foundCarState.currentSpeed should equal(250.0)
    foundCarState.fuelLevel should equal(50.0)
    foundCarState.progress should equal(0.5)

  it should "apply a function to the correct car using withCar" in:
    val carToUpdate = cars.head
    val updated = validRaceState.withCar(carToUpdate.carNumber)((car, state) =>
      validRaceState.updateCar(car -> state.copyLike(currentSpeed = 999.0))
    )

    val (_, updatedState) = updated.findCar(carToUpdate.carNumber).get
    updatedState.currentSpeed shouldBe 999.0

  it should "record a lap in the scoreboard" in:
    val raceTime = BigDecimal(10)
    val updatedTime = validRaceState.advanceTime(raceTime)
    val car = cars.head
    val newState = updatedTime.updateScoreboard(car)

    newState.scoreboard.lapsByCar(car).headOption should not be empty

  it should "update the weather correctly" in:
    val rainyState = validRaceState.updateWeather(Rainy)
    rainyState.weather should equal(Rainy)

  it should "correctly advance the simulation time" in:
    val advanced = validRaceState.advanceTime(2.5)
    advanced.raceTime should equal(2.5)
