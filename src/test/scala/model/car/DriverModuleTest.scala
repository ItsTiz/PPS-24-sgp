package model.car

import org.scalatest.funsuite.AnyFunSuite
import model.car.DriverModule.*

import model.car.DrivingStyleModule.*

class DriverModuleTest extends AnyFunSuite:

  test("Driver should correctly store name and style") {
    val driver = Driver("Charles Leclerc", DrivingStyle.aggressive)
    assert(driver.name == "Charles Leclerc")
    assert(driver.style == DrivingStyle.aggressive)
  }
