package model.car

object DriverModule:

  /** Driving style of a racing driver. */
  enum DrivingStyle:
    /** Aggressive driving style: higher speed, faster degradation of the tires and faster fuel consuming. */
    case Aggressive

    /** Defensive driving style: slower, prioritizes safety and tire preservation. */
    case Defensive

    /** Balanced driving style: average between aggressive and defensive. */
    case Balanced

  /** A driver with a name and a driving style. */
  trait Driver:
    /** The name of the driver.
      *
      * @return
      *   the driver's name
      */
    def name: String

    /** The driving style of the driver.
      *
      * @return
      *   the driver's [[DrivingStyle]]
      */
    def style: DrivingStyle

  /** Internal concrete implementation of [[Driver]]. */
  private case class DriverImpl(name: String, style: DrivingStyle) extends Driver

  /** Factory for creating [[Driver]] instances. */
  object Driver:
    /** Creates a new driver with the given name and driving style.
      *
      * @param name
      *   the name of the driver
      * @param style
      *   the [[DrivingStyle]] the driver adopts
      * @return
      *   a new [[Driver]] instance
      */
    def apply(name: String, style: DrivingStyle): Driver =
      DriverImpl(name, style)
