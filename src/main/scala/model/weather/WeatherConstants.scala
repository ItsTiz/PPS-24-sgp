package model.weather

object WeatherConstants:

  /** Grip values for different weather conditions.
    *
    * These values represent the grip coefficient that affects car handling. Higher values indicate better grip, while
    * lower values indicate reduced grip.
    */
  final val sunnyGrip: Double = 1.0
  final val rainyGrip: Double = 0.95
  final val foggyGrip: Double = 0.97

  /** Tire wear modifiers for different weather conditions.
    *
    * These values represent how quickly tires wear out in different weather conditions. Higher values indicate faster
    * tire wear, while lower values indicate slower tire wear.
    */
  final val sunnyTireModifier: Double = 1.0
  final val rainyTireModifier: Double = 1.1
  final val foggyTireModifier: Double = 1.05
