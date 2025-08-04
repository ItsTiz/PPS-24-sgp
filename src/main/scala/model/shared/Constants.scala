package model.shared

object Constants:
  // Driving style modifiers
  val aggressiveSpeedIncrease = 0.12
  val aggressiveTireDegradation = 0.09
  val aggressiveFuelConsumption = 0.15

  val defensiveSpeedIncrease = 0.05
  val defensiveTireDegradation = 0.07
  val defensiveFuelConsumption = 0.08

  val balancedSpeedIncrease = 0.08
  val balancedTireDegradation = 0.08
  val balancedFuelConsumption = 0.10

  // Car values
  val averageCarWeight: Double = 800.0 // kg

  // Tire values
  val minTireDegradeState = 0.0
  val tireWearLimit = 40.0
  val maxTireLevel = 100.0

  // Fuel values
  val maxFuelLevel = 100.0
  val minFuelLevel = 0.0
