package model.car

import model.shared.Constants.TireWearLimit

object TireModule:

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
      * Tires are considered worn out if degrade state is over 80%.
      *
      * @return
      *   `true` if degrade state > 80%, `false` otherwise.
      */
    def needsTireChange: Boolean = degradeState >= TireWearLimit

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
      case TireType.Soft => TireImpl(TireType.Soft, grip = 0.95, degradeState, speedModifier = 1.05)
      case TireType.Medium => TireImpl(TireType.Medium, grip = 0.85, degradeState, speedModifier = 1.00)
      case TireType.Hard => TireImpl(TireType.Hard, grip = 0.75, degradeState, speedModifier = 0.95)
      case TireType.Wet => TireImpl(TireType.Wet, grip = 0.90, degradeState, speedModifier = 0.90)
