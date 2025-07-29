package model.shared

object Constants:
  // Driving style modifiers
  val aggressiveSpeedIncrease = 0.15
  val aggressiveTireDegradation = 0.05
  val aggressiveFuelConsumption = 0.18

  val defensiveSpeedIncrease = 0.05
  val defensiveTireDegradation = 0.01
  val defensiveFuelConsumption = 0.06

  val balancedSpeedIncrease = 0.10
  val balancedTireDegradation = 0.03
  val balancedFuelConsumption = 0.10

  // Car values
  val minTireDegradeState = 0.0
  val tireWearLimit = 80.0
  val maxTireLevel = 100.0
  val maxFuelLevel = 100.0
  val minFuelLevel = 0.0
