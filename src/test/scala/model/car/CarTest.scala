package model.car

import model.shared.Coordinate
import org.scalatest.funsuite.AnyFunSuite
import model.car.DriverModule.*
import model.car.CarModule.*
import model.car.DrivingStyleModule.*

class CarTest extends AnyFunSuite:

  val driver = Driver("Test Driver", DrivingStyle.balanced)
  val car = Car(
    model = "TestCar",
    weightKg = 750.0,
    driver = driver,
    maxFuel = 100.0,
    fuelLevel = 50.0,
    degradeState = 20.0,
    currentSpeed = 200.0,
    position = Coordinate(0.0, 0.0)
  )

  test("Car basic properties should match values") {
    assert(car.model == "TestCar")
    assert(car.weightKg == 750.0)
    assert(car.driver == driver)
    assert(car.maxFuel == 100.0)
    assert(car.fuelLevel == 50.0)
    assert(car.degradeState == 20.0)
    assert(car.currentSpeed == 200.0)
    assert(car.position == Coordinate(0.0, 0.0))
  }

  test("Car isOutOfFuel and needsTireChange should work correctly") {
    val outOfFuelCar = Car(
      model = car.model,
      weightKg = car.weightKg,
      driver = car.driver,
      maxFuel = car.maxFuel,
      fuelLevel = 0,
      degradeState = car.degradeState,
      currentSpeed = car.currentSpeed,
      position = car.position
    )

    val wornCar = Car(
      model = car.model,
      weightKg = car.weightKg,
      driver = car.driver,
      maxFuel = car.maxFuel,
      fuelLevel = car.fuelLevel,
      degradeState = 85.0,
      currentSpeed = car.currentSpeed,
      position = car.position
    )

    assert(!car.isOutOfFuel)
    assert(outOfFuelCar.isOutOfFuel)

    assert(!car.needsTireChange)
    assert(wornCar.needsTireChange)
  }

  test("Car withUpdatedState should update values immutably") {
    val newPosition = Coordinate(100.0, 50.0)
    val updated = car.withUpdatedState(
      speed = 220.0,
      fuelConsumed = 10.0,
      degradeIncrease = 25.0,
      newPosition = newPosition
    )

    assert(updated.currentSpeed == 220.0)
    assert(updated.fuelLevel == 40.0)
    assert(updated.degradeState == 45.0)
    assert(updated.position == newPosition)

    // Original car unchanged
    assert(car.currentSpeed == 200.0)
    assert(car.fuelLevel == 50.0)
    assert(car.position == Coordinate(0.0, 0.0))
  }

  test("Driver should hold name and driving style") {
    val aggressiveDriver = Driver("Speedy", DrivingStyle.aggressive)
    assert(aggressiveDriver.name == "Speedy")
    assert(aggressiveDriver.style == DrivingStyle.aggressive)
  }

  // Check values validity
  val validDriver = Driver("Leclerc", DrivingStyle.balanced)
  val validPosition = Coordinate(0.0, 0.0)

  test("Car creation fails with negative weight") {
    assertThrows[IllegalArgumentException] {
      Car("Ferrari", -1.0, validDriver, 100.0, 50.0, 10.0, 200.0, validPosition)
    }
  }

  test("Car creation fails with NaN weight") {
    assertThrows[IllegalArgumentException] {
      Car("Ferrari", Double.NaN, validDriver, 100.0, 50.0, 10.0, 200.0, validPosition)
    }
  }

  test("Car creation fails with infinite speed") {
    assertThrows[IllegalArgumentException] {
      Car("Ferrari", 700.0, validDriver, 100.0, 50.0, 10.0, Double.PositiveInfinity, validPosition)
    }
  }

  test("Car creation fails when fuel level is higher than max fuel") {
    assertThrows[IllegalArgumentException] {
      Car("Ferrari", 700.0, validDriver, 100.0, 150.0, 10.0, 200.0, validPosition)
    }
  }

  test("Car creation fails with null model") {
    assertThrows[IllegalArgumentException] {
      Car(null, 700.0, validDriver, 100.0, 50.0, 10.0, 200.0, validPosition)
    }
  }

  test("Car creation fails with null driver") {
    assertThrows[IllegalArgumentException] {
      Car("Ferrari", 700.0, null, 100.0, 50.0, 10.0, 200.0, validPosition)
    }
  }

  test("Car creation fails with null position") {
    assertThrows[IllegalArgumentException] {
      Car("Ferrari", 700.0, validDriver, 100.0, 50.0, 10.0, 200.0, null)
    }
  }

  test("Car creation succeeds with valid values") {
    val car = Car("Ferrari", 700.0, validDriver, 100.0, 50.0, 10.0, 200.0, validPosition)
    assert(car.model == "Ferrari")
  }
