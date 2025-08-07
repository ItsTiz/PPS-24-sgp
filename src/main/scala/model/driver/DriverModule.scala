package model.driver

object DriverModule:
  import DrivingStyleModule.DrivingStyle

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

object DriverGenerator:
  import DriverModule.Driver
  import DrivingStyleModule.DrivingStyle

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
      Driver("Leclerc", DrivingStyle.balanced), // Ferrari
      Driver("Verstappen", DrivingStyle.aggressive), // Red Bull
      Driver("Hamilton", DrivingStyle.aggressive), // Mercedes
      Driver("Norris", DrivingStyle.aggressive), // McLaren
      Driver("Alonso", DrivingStyle.defensive), // Aston Martin
      Driver("Ocon", DrivingStyle.balanced), // Alpine
      Driver("Bottas", DrivingStyle.defensive), // Kick Sauber
      Driver("Tsunoda", DrivingStyle.aggressive), // RB (Visa Cash App RB)
      Driver("Albon", DrivingStyle.balanced), // Williams
      Driver("Magnussen", DrivingStyle.defensive) // Haas
    )
