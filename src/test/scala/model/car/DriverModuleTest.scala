package model.car

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import model.car.DriverModule.*

import model.car.DrivingStyleModule.*

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
