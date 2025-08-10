package model.race.physics

import model.race.physics.RacePhysicsModule.*
import model.simulation.states.CarStateModule.*
import model.weather.WeatherModule.*
import model.car.CarModule.*
import model.car.TireModule
import model.car.TireModule.Tire
import model.driver.DriverModule.*
import model.driver.DrivingStyleModule.DrivingStyle
import model.tracks.TrackSectorModule.*
import model.tracks.TrackSectorModule.TrackSector.straight
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class RacePhysicsTest extends AnyFlatSpec with Matchers:

  val testDriver: Driver = Driver("Test Driver", DrivingStyle.balanced)
  val testCar: Car = Car(
    model = "TestCar",
    carNumber = 1,
    weightKg = 750.0,
    driver = testDriver,
    maxFuel = 100.0
  )

  val testSector: TrackSector = straight(
    id = 1,
    sectorLength = 300.0,
    avgSpeed = 200.0,
    maxSpeed = 250.0,
    gripIndex = 1.0
  )

  val initialState: CarState = CarState(
    maxFuel = 100.0,
    fuelLevel = 50.0,
    currentSpeed = 200.0,
    progress = 0.0,
    tire = Tire(TireModule.TireType.Medium, 10.0),
    currentLaps = 0,
    currentSector = testSector
  )

  val racePhysics: RacePhysics = RacePhysics()

  "advanceCar" should "increase progress and reduce fuel/tire level under balanced/normal conditions" in:
    val weather = Weather.Sunny

    val newState = racePhysics.advanceCar(testCar, initialState)(weather)

    newState should not be initialState
    newState.progress should be > initialState.progress
    newState.fuelLevel should be < initialState.fuelLevel
    newState.tire.degradeState should be > initialState.tire.degradeState
    newState.currentSpeed should be > 0.0
    newState.currentSector shouldBe initialState.currentSector
    newState.currentLaps shouldBe initialState.currentLaps

  it should "cap progress at maxSectorProgress" in:
    val weather = Weather.Sunny
    val nearLimitState = initialState.copyLike(progress = 0.999)

    val newState = racePhysics.advanceCar(testCar, nearLimitState)(weather)

    newState.progress should be <= 1.0
