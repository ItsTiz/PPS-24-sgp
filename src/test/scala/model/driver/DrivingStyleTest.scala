package model.driver

import model.driver.DrivingStyleModule.*
import model.driver.DrivingStyleModule.DrivingStyle.*
import model.driver.DrivingStyleConstants.*
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.must.Matchers.*
import org.scalatest.matchers.should.Matchers.*

class DrivingStyleTest extends AnyFunSuite:

  test("Aggressive driving style should have correct parameters") {
    val style = aggressive
    style.styleType shouldBe "Aggressive"
    style.speedIncreasePercent shouldBe aggressiveSpeedIncrease
    style.tireDegradationRate shouldBe aggressiveTireDegradation
    style.fuelConsumptionRate shouldBe aggressiveFuelConsumption
  }

  test("Defensive driving style should have correct parameters") {
    val style = defensive
    style.styleType shouldBe "Defensive"
    style.speedIncreasePercent shouldBe defensiveSpeedIncrease
    style.tireDegradationRate shouldBe defensiveTireDegradation
    style.fuelConsumptionRate shouldBe defensiveFuelConsumption
  }

  test("Balanced driving style should have correct parameters") {
    val style = balanced
    style.styleType shouldBe "Balanced"
    style.speedIncreasePercent shouldBe balancedSpeedIncrease
    style.tireDegradationRate shouldBe balancedTireDegradation
    style.fuelConsumptionRate shouldBe balancedFuelConsumption
  }

  test("DrivingStyle.apply should return correct singleton instance") {
    apply("aggressive") mustBe aggressive
    apply("Aggressive") mustBe aggressive
    apply("DEFENSIVE") mustBe defensive
    apply("balanced") mustBe balanced
  }

  test("DrivingStyle.unapply should return the style name") {
    unapply(aggressive) shouldBe Some("Aggressive")
    unapply(defensive) shouldBe Some("Defensive")
    unapply(balanced) shouldBe Some("Balanced")
  }

  test("DrivingStyle.apply should throw exception on unknown name") {
    val invalidName = "reckless"
    val thrown = intercept[IllegalArgumentException] {
      apply(invalidName)
    }
    org.scalatest.matchers.should.Matchers.include("Unknown driving style")
  }
