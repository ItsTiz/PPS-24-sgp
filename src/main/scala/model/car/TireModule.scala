package model.car

object TireModule:

  /** Tire types used in the simulation */
  enum TireType:
    case Soft, Medium, Hard, Wet

  /** A tire compound used by a car */
  trait Tire:
    def tireType: TireType
    def grip: Double // higher = better grip
    def speedModifier: Double // multiplier for speed (1.0 = neutral)

  private case class TireImpl(tireType: TireType, grip: Double, speedModifier: Double) extends Tire

  /** Factory for creating tires */
  object Tire:

    /** Returns a predefined tire by type
      *
      * @param tireType
      *   the desired tire type
      * @return
      *   a [[Tire]] instance with grip and speed values
      */
    def apply(tireType: TireType): Tire = tireType match
      case TireType.Soft => TireImpl(TireType.Soft, grip = 0.95, speedModifier = 1.05)
      case TireType.Medium => TireImpl(TireType.Medium, grip = 0.85, speedModifier = 1.00)
      case TireType.Hard => TireImpl(TireType.Hard, grip = 0.75, speedModifier = 0.95)
      case TireType.Wet => TireImpl(TireType.Wet, grip = 0.90, speedModifier = 0.90)
