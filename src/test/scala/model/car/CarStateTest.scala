package model.car

import org.scalatest.funsuite.AnyFunSuite
import model.driver.DrivingStyleModule.*
import model.car.TireModule.Tire
import model.simulation.states.CarStateModule.CarState
import model.tracks.TrackSectorModule.TrackSector

class CarStateTest extends AnyFunSuite:

  val testSector: TrackSector = TrackSector.straight(0, 500, 300, 200, 0.8)

  val carState = CarState(
    maxFuel = 100.0,
    fuelLevel = 50.0,
    currentSpeed = 200.0,
    progress = 0.0,
    tire = Tire(TireModule.TireType.Medium, 10.0),
    currentLaps = 0,
    currentSector = testSector
  )

  test("Car basic properties should match values") {
    assert(carState.maxFuel == 100.0)
    assert(carState.fuelLevel == 50.0)
    assert(carState.tire.degradeState == 10.0)
    assert(carState.currentSpeed == 200.0)
    assert(carState.progress == 0.0)
    assert(carState.tire == Tire(TireModule.TireType.Medium, 10.0))
  }

  test("Car isOutOfFuel and needsTireChange should work correctly") {
    val outOfFuelCar = CarState(
      maxFuel = carState.maxFuel,
      fuelLevel = 0,
      currentSpeed = carState.currentSpeed,
      progress = carState.progress,
      Tire(TireModule.TireType.Medium, 10.0),
      currentLaps = 0,
      currentSector = testSector
    )

    val wornCarState = CarState(
      maxFuel = carState.maxFuel,
      fuelLevel = carState.fuelLevel,
      currentSpeed = carState.currentSpeed,
      progress = carState.progress,
      Tire(TireModule.TireType.Medium, 90.0),
      currentLaps = 0,
      currentSector = testSector
    )

    assert(!carState.isOutOfFuel)
    assert(outOfFuelCar.isOutOfFuel)

    assert(!carState.tire.needsTireChange)
    assert(wornCarState.tire.needsTireChange)
  }

  test("Car withUpdatedState should update values immutably") {
    val newProgress = 0.2
    val updated = carState.withUpdatedState(
      speed = 220.0,
      fuelConsumed = 10.0,
      degradeIncrease = 25.0,
      newProgress = newProgress,
      Tire(TireModule.TireType.Soft, 20.0),
      currentLaps = 1,
      currentSector = testSector
    )

    assert(updated.currentSpeed == 220.0)
    assert(updated.fuelLevel == 40.0)
    assert(updated.tire.degradeState == 45.0)
    assert(updated.progress == newProgress)
    assert(updated.tire == Tire(TireModule.TireType.Soft, 45.0))

    // Original car unchanged
    assert(carState.currentSpeed == 200.0)
    assert(carState.fuelLevel == 50.0)
    assert(carState.progress == 0.0)
    assert(carState.tire == Tire(TireModule.TireType.Medium, 10.0))
  }

  test("CarState creation fails with infinite speed") {
    assertThrows[IllegalArgumentException] {
      CarState(100.0, 50.0, Double.PositiveInfinity, 0.0,
        Tire(TireModule.TireType.Medium, 10.0), 0, testSector)
    }
  }

  test("CarState creation fails when fuel level is higher than max fuel") {
    assertThrows[IllegalArgumentException] {
      CarState(100.0, 150.0, 200.0, 0.0, Tire(TireModule.TireType.Medium, 10.0), 0, testSector)
    }
  }

  test("CarState creation fails with progress > 1") {
    assertThrows[IllegalArgumentException] {
      CarState(100.0, 50.0, 200.0, 4, Tire(TireModule.TireType.Medium, 10.0), 0, testSector)
    }
  }

  test("CarState creation succeeds with valid values") {
    val car =
      CarState(100.0, 50.0, 200.0, 0.0, Tire(TireModule.TireType.Medium, 10.0), 0, testSector)
    assert(car.maxFuel == 100.0)
  }
