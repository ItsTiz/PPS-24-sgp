package model.driver

import model.driver.DriverGenerator
import model.driver.DriverModule.*
import model.driver.DrivingStyleModule.*
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class DriverModuleTest extends AnyFunSuite with Matchers:

  test("Driver should correctly store name and style") {
    val driver = Driver("Charles Leclerc", DrivingStyle.aggressive)
    assert(driver.name == "Charles Leclerc")
    assert(driver.style == DrivingStyle.aggressive)
  }

  test("Driver creation should throw exception for null name") {
    val exception = intercept[IllegalArgumentException] {
      Driver(null, DrivingStyle.defensive)
    }

    exception.getMessage should include("Driver name cannot be null or blank")
  }

  test("Driver creation should throw exception for blank name") {
    val exception = intercept[IllegalArgumentException] {
      Driver("   ", DrivingStyle.aggressive)
    }
    exception.getMessage should include("Driver name cannot be null or blank")
  }

  test("Driver creation should throw exception for null style") {
    val exception = intercept[IllegalArgumentException] {
      Driver("Charles Leclerc", null)
    }
    exception.getMessage should include("Driver style cannot be null")
  }

  test("DriverGenerator should generate 4 drivers with correct names and styles") {
    val drivers = DriverGenerator.generateDrivers()

    assert(drivers.length == 10)

    val expectedNames = Set(
      "Leclerc", // Ferrari
      "Verstappen", // Red Bull
      "Hamilton", // Mercedes
      "Norris", // McLaren
      "Alonso", // Aston Martin
      "Ocon", // Alpine
      "Bottas", // Kick Sauber
      "Tsunoda", // RB
      "Albon", // Williams
      "Magnussen" // Haas
    )
    val actualNames = drivers.map(_.name).toSet
    assert(actualNames == expectedNames)

    val expectedStyles = Map(
      "Leclerc" -> DrivingStyle.balanced,
      "Verstappen" -> DrivingStyle.aggressive,
      "Hamilton" -> DrivingStyle.aggressive,
      "Norris" -> DrivingStyle.aggressive,
      "Alonso" -> DrivingStyle.defensive,
      "Ocon" -> DrivingStyle.balanced,
      "Bottas" -> DrivingStyle.defensive,
      "Tsunoda" -> DrivingStyle.aggressive,
      "Albon" -> DrivingStyle.balanced,
      "Magnussen" -> DrivingStyle.defensive
    )

    drivers.foreach { driver =>
      assert(expectedStyles(driver.name) == driver.style)
    }
  }

  test("Each driver should have non-null name and style") {
    val drivers = DriverGenerator.generateDrivers()

    drivers.foreach { driver =>
      assert(driver.name != null && driver.name.nonEmpty)
      assert(driver.style != null)
    }
  }
