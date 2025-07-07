package model.car

import org.scalatest.funsuite.AnyFunSuite
import model.car.DriverModule.*

class DriverModuleTest extends AnyFunSuite:

  test("Driver should correctly store name and style") {
    val driver = Driver("Charles Leclerc", DrivingStyle.Aggressive)
    assert(driver.name == "Charles Leclerc")
    assert(driver.style == DrivingStyle.Aggressive)
  }
