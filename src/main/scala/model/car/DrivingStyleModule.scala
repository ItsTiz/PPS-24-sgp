package model.car

import model.shared.Constants.*

/** Module defining different driving styles for drivers. */
object DrivingStyleModule:
  /** A driving style defines how a driver behaves on track, affecting car performance through speed, tire wear, and
    * fuel usage.
    */
  trait DrivingStyle:
    /** The name/type of the driving style. */
    def styleType: String

    /** The percentage increase in speed this style provides (0.0–1.0). */
    def speedIncreasePercent: Double

    /** The rate at which tires degrade using this style (0.0–1.0). */
    def tireDegradationRate: Double

    /** The rate at which fuel is consumed using this style (0.0–1.0). */
    def fuelConsumptionRate: Double

  /** Aggressive driving style: high speed, high degradation, high consumption. */
  private case object Aggressive extends DrivingStyle:
    val styleType = "Aggressive"
    val speedIncreasePercent = AggressiveSpeedIncrease
    val tireDegradationRate = AggressiveTireDegradation
    val fuelConsumptionRate = AggressiveFuelConsumption

  /** Defensive driving style: low speed gain, minimal wear and fuel use. */
  private case object Defensive extends DrivingStyle:
    val styleType = "Defensive"
    val speedIncreasePercent = DefensiveSpeedIncrease
    val tireDegradationRate = DefensiveTireDegradation
    val fuelConsumptionRate = DefensiveFuelConsumption

  /** Balanced driving style: moderate speed and wear values. */
  private case object Balanced extends DrivingStyle:
    val styleType = "Balanced"
    val speedIncreasePercent = BalancedSpeedIncrease
    val tireDegradationRate = BalancedTireDegradation
    val fuelConsumptionRate = BalancedFuelConsumption

  /** Factory and extractor for [[DrivingStyle]] instances. */
  object DrivingStyle:
    /** Creates a predefined driving style by name.
      *
      * @param name
      *   the name of the style (case-insensitive)
      * @return
      *   a [[DrivingStyle]] instance
      * @throws IllegalArgumentException
      *   if the name is invalid
      */
    def apply(name: String): DrivingStyle = name.toLowerCase match
      case "aggressive" => Aggressive
      case "defensive" => Defensive
      case "balanced" => Balanced
      case _ => throw new IllegalArgumentException(s"Unknown driving style: $name")

    /** Extractor for pattern matching on a [[DrivingStyle]].
      *
      * @param style
      *   the style to match
      * @return
      *   the style name as a `String`
      */
    def unapply(style: DrivingStyle): Option[String] = Some(style.styleType)

    /** Shortcut for [[Aggressive]] style. */
    def aggressive: DrivingStyle = Aggressive

    /** Shortcut for [[Defensive]] style. */
    def defensive: DrivingStyle = Defensive

    /** Shortcut for [[Balanced]] style. */
    def balanced: DrivingStyle = Balanced
