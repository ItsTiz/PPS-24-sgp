package model.driver

import DrivingStyleModule.DrivingStyle

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
      * @throws IllegalArgumentException
      *   if name is empty/null or style is null
      */
    def apply(name: String, style: DrivingStyle): Driver =
      validateDriver(name, style)
      DriverImpl(name, style)

  private def validateDriver(name: String, style: DrivingStyle): Unit =
    require(name != null && name.trim.nonEmpty, "Driver name cannot be null or blank")
    require(style != null, "Driver style cannot be null")

import DriverModule.Driver
object DriverGenerator:

  /** Generates 4 predefined drivers:
    *   - Leclerc (Balanced)
    *   - Hamilton (Aggressive)
    *   - Norris (Aggressive)
    *   - Colapinto (Defensive)
    *
    * @return
    *   a list of 4 unique Driver instances
    */
  def generateDrivers(): List[Driver] =
    List(
      Driver("Leclerc", DrivingStyle.balanced),
      Driver("Hamilton", DrivingStyle.aggressive),
      Driver("Norris", DrivingStyle.aggressive),
      Driver("Colapinto", DrivingStyle.defensive)
    )
