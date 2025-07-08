package model.car

import model.car.DrivingStyleModule.DrivingStyle

object DriverModule:

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
