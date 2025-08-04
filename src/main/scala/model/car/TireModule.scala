package model.car

object TireModule:
  import model.shared.Constants.{minTireDegradeState, tireWearLimit}

  /** Tire types used in the simulation */
  enum TireType:
    case Soft, Medium, Hard, Wet

  /** A tire compound used by a car */
  trait Tire:
    def tireType: TireType
    def grip: Double // higher = better grip
    def speedModifier: Double // multiplier for speed (1.0 = neutral)
    def degradeState: Double

    /** Checks whether the tires need to be changed.
      *
      * Tires are considered worn out if degradeState is over 80%.
      *
      * @return
      *   `true` if degrade state > 80%, `false` otherwise.
      */
    def needsTireChange: Boolean = degradeState >= tireWearLimit

    override def toString: String =
      s"""Tire(
         |        tireType: $tireType
         |        grip: $grip
         |        speedModifier: $speedModifier
         |        degradeState: $degradeState
         |    )""".stripMargin

  private case class TireImpl(tireType: TireType, grip: Double, degradeState: Double, speedModifier: Double)
      extends Tire

  /** Factory for creating tires */
  object Tire:

    /** Returns a predefined tire by type
      *
      * @param tireType
      *   the desired tire type
      * @param degradeState
      *   * the current tire wear level (0 to 100%)
      * @return
      *   a [[Tire]] instance with grip and speed values
      */
    def apply(tireType: TireType, degradeState: Double): Tire = tireType match
      case TireType.Soft => TireImpl(TireType.Soft, grip = 0.98, degradeState, speedModifier = 1.05)
      case TireType.Medium => TireImpl(TireType.Medium, grip = 0.94, degradeState, speedModifier = 1.00)
      case TireType.Hard => TireImpl(TireType.Hard, grip = 0.92, degradeState, speedModifier = 0.95)
      case TireType.Wet => TireImpl(TireType.Wet, grip = 0.96, degradeState, speedModifier = 0.90)

  object TireGenerator:
    import scala.util.Random
    import model.simulation.weather.WeatherModule.Weather
    import model.simulation.weather.WeatherModule.Weather.*

    def getNewRandomTire: Tire =
      Tire(TireType.values(new Random().nextInt(TireType.values.length)), minTireDegradeState)

    private def randomDryTireType: TireType =
      List(TireType.Soft, TireType.Medium, TireType.Hard)(new Random().nextInt(3))

    def getNewTireForWeather(weather: Weather): Tire = weather match
      case Sunny => Tire(randomDryTireType, minTireDegradeState)
      case Rainy => Tire(TireType.Wet, minTireDegradeState)
      case Foggy => Tire(TireType.Medium, minTireDegradeState)