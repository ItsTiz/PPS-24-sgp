package model.car

import org.scalatest.funsuite.AnyFunSuite

class DriverTest extends AnyFunSuite:

  test("Driver should hold name and driving style") {
    val driver = Driver("Charles Leclerc", DrivingStyle.Aggressive)
    assert(driver.name == "Charles Leclerc")
    assert(driver.style == DrivingStyle.Aggressive)
  }
