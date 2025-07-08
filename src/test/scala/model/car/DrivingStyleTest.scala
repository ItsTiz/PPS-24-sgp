package model.car

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.must.Matchers.*
import org.scalatest.matchers.should.Matchers.*

import DrivingStyleModule.*
import DrivingStyleModule.DrivingStyle.*
import model.shared.Constants.*

class DrivingStyleTest extends AnyFunSuite:

  test("Aggressive driving style should have correct parameters") {
    val style = aggressive
    style.styleType shouldBe "Aggressive"
    style.speedIncreasePercent shouldBe AggressiveSpeedIncrease
    style.tireDegradationRate shouldBe AggressiveTireDegradation
    style.fuelConsumptionRate shouldBe AggressiveFuelConsumption
  }

  test("Defensive driving style should have correct parameters") {
    val style = defensive
    style.styleType shouldBe "Defensive"
    style.speedIncreasePercent shouldBe DefensiveSpeedIncrease
    style.tireDegradationRate shouldBe DefensiveTireDegradation
    style.fuelConsumptionRate shouldBe DefensiveFuelConsumption
  }

  test("Balanced driving style should have correct parameters") {
    val style = balanced
    style.styleType shouldBe "Balanced"
    style.speedIncreasePercent shouldBe BalancedSpeedIncrease
    style.tireDegradationRate shouldBe BalancedTireDegradation
    style.fuelConsumptionRate shouldBe BalancedFuelConsumption
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
