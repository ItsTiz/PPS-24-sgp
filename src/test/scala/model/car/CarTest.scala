package model.car

import org.scalatest.funsuite.AnyFunSuite
import model.car.DriverModule.*
import model.car.CarModule.*
import model.car.DrivingStyleModule.*
import model.car.TireModule.Tire

class CarTest extends AnyFunSuite:

  val driver = Driver("Test Driver", DrivingStyle.balanced)
  val car = Car(
    model = "TestCar",
    carNumber = 10,
    weightKg = 750.0,
    driver = driver,
    maxFuel = 100.0
  )

  test("Car basic properties should match values") {
    assert(car.model == "TestCar")
    assert(car.carNumber == 10)
    assert(car.weightKg == 750.0)
    assert(car.driver == driver)
    assert(car.maxFuel == 100.0)

  }

  test("Driver should hold name and driving style") {
    val aggressiveDriver = Driver("Speedy", DrivingStyle.aggressive)
    assert(aggressiveDriver.name == "Speedy")
    assert(aggressiveDriver.style == DrivingStyle.aggressive)
  }

  // Check values validity
  val validDriver = Driver("Leclerc", DrivingStyle.balanced)

  test("Car creation fails with negative weight") {
    assertThrows[IllegalArgumentException] {
      Car("Ferrari", 16, -1.0, validDriver, 100.0)
    }
  }

  test("Car creation fails with NaN weight") {
    assertThrows[IllegalArgumentException] {
      Car("Ferrari", 16, Double.NaN, validDriver, 100.0)
    }
  }

  test("Car creation fails with null model") {
    assertThrows[IllegalArgumentException] {
      Car(null, 16, 700.0, validDriver, 100.0)
    }
  }

  test("Car creation fails with null driver") {
    assertThrows[IllegalArgumentException] {
      Car("Ferrari", 16, 700.0, null, 100.0)
    }
  }

  test("Car creation succeeds with valid values") {
    val car =
      Car("Ferrari", 16, 700.0, validDriver, 100.0)
    assert(car.model == "Ferrari")
  }

  test("Car creation fails with negative car number") {
    assertThrows[IllegalArgumentException] {
      Car("Ferrari", -10, 700.00, validDriver, 100.0)
    }
  }

  test("CarGenerator should generate 4 cars with correct models and drivers") {
    val cars = CarGenerator.generateCars()

    assert(cars.length == 4)

    val models = cars.map(_.model)
    val expectedModels = Set("Ferrari", "Mercedes", "McLaren", "Alpine")
    assert(models.toSet == expectedModels)

    val drivers = cars.map(_.driver.name)
    val expectedDrivers = Set("Leclerc", "Hamilton", "Norris", "Colapinto")
    assert(drivers.toSet == expectedDrivers)
  }

  test("Each car should have correct default values") {
    val cars = CarGenerator.generateCars()

    cars.foreach { car =>
      assert(car.maxFuel == 110.0)
    }
  }
