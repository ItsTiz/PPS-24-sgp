package model.car

object DriverModule:

  /** Driving style of a racing driver. */
  enum DrivingStyle:
    case Aggressive, Defensive, Balanced

  /** A driver with a name and a driving style. */
  trait Driver:
    def name: String
    def style: DrivingStyle

  private case class DriverImpl(name: String, style: DrivingStyle) extends Driver

  /** Factory for [[Driver]] instances. */
  object Driver:
    def apply(name: String, style: DrivingStyle): Driver =
      DriverImpl(name, style)
