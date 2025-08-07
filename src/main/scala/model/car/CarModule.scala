package model.car

object CarModule:
  import model.driver.DriverModule.Driver

  /** A racing car in the simulation. */
  trait Car:
    def model: String
    def carNumber: Int
    def weightKg: Double
    def driver: Driver
    def maxFuel: Double

    private def canEqual(other: Any): Boolean = other.isInstanceOf[Car]

    override def equals(other: Any): Boolean = other match
      case other: Car => (other canEqual this) && model == other.model && driver == other.driver
      case _ => false

    override def hashCode(): Int =
      (model, driver).##

  /** Factory and extractor for [[Car]] instances. */
  object Car:

    /** Creates a new [[Car]] instance with the given parameters.
      *
      * @param model
      *   the car's model name
      * @param carNumber
      *   the car's number
      * @param weightKg
      *   the car's weight in kilograms
      * @param driver
      *   the driver assigned to the car
      * @param maxFuel
      *   the maximum fuel capacity
      * @return
      *   a new instance of [[Car]]
      * @throws IllegalArgumentException
      *   if any validation constraint is violated
      */
    def apply(
        model: String,
        carNumber: Int,
        weightKg: Double,
        driver: Driver,
        maxFuel: Double
    ): Car =
      validateCar(model, carNumber, weightKg, driver, maxFuel)
      CarImpl(model, carNumber, weightKg, driver, maxFuel)

    /** Deconstructs a [[Car]] instance into its parameters.
      *
      * @param c
      *   the car to deconstruct
      * @return
      *   a tuple containing all car attributes
      */
    def unapply(c: Car): Option[(String, Int, Double, Driver, Double)] =
      Some((c.model, c.carNumber, c.weightKg, c.driver, c.maxFuel))

    /** Validates the parameters used to create a car.
      *
      * @param model
      *   The model name of the car. Must not be null.
      * @param carNumber
      *   The unique number assigned to the car. Must be a positive integer.
      * @param weightKg
      *   The weight of the car in kilograms. Must be a valid, non-negative number.
      * @param driver
      *   The driver assigned to the car. Must not be null.
      * @param maxFuel
      *   The maximum fuel capacity of the car in kilograms. Must be a valid, non-negative number.
      * @throws IllegalArgumentException
      *   if any of the provided parameters are invalid.
      */
    private def validateCar(
        model: String,
        carNumber: Int,
        weightKg: Double,
        driver: Driver,
        maxFuel: Double
    ): Unit =
      require(model != null, "Model cannot be null")
      require(carNumber > 0, "Car number must be a positive int")
      require(driver != null, "Driver cannot be null")
      require(!weightKg.isNaN && !weightKg.isInfinity && weightKg >= 0,
        "Car weight must be a valid non-negative number")
      require(!maxFuel.isNaN && !maxFuel.isInfinity && maxFuel >= 0, "Max fuel must be a valid non-negative number")

  /** Internal implementation of [[Car]]. */
  private case class CarImpl(
      override val model: String,
      override val carNumber: Int,
      override val weightKg: Double,
      override val driver: Driver,
      override val maxFuel: Double
  ) extends Car

object CarGenerator:

  import model.car.CarModule.Car

  /** Generates 4 racing cars, each with a different model and driver:
    *   - Ferrari driven by Leclerc
    *   - Mercedes driven by Hamilton
    *   - McLaren driven by Norris
    *   - Alpine driven by Colapinto
    *
    * @return
    *   a list of 4 unique Car instances
    */
  def generateCars(): List[Car] =
    import model.driver.DriverGenerator.generateDrivers
    val List(
      leclerc, // Ferrari
      verstappen, // Red Bull
      hamilton, // Mercedes
      norris, // McLaren
      alonso, // Aston Martin
      ocon, // Alpine
      bottas, // Kick Sauber
      tsunoda, // RB
      albon, // Williams
      magnussen // Haas
    ) = generateDrivers()

    List(
      Car("Ferrari", 16, 795.0, leclerc, maxFuel = 110.0),
      Car("Red Bull", 1, 793.0, verstappen, maxFuel = 110.0),
      Car("Mercedes", 44, 800.0, hamilton, maxFuel = 110.0),
      Car("McLaren", 4, 790.0, norris, maxFuel = 110.0),
      Car("Aston Martin", 14, 798.0, alonso, maxFuel = 110.0),
      Car("Alpine", 31, 805.0, ocon, maxFuel = 110.0),
      Car("Kick Sauber", 77, 802.0, bottas, maxFuel = 110.0),
      Car("RB", 22, 794.0, tsunoda, maxFuel = 110.0),
      Car("Williams", 23, 796.0, albon, maxFuel = 110.0),
      Car("Haas", 20, 807.0, magnussen, maxFuel = 110.0)
    )

  def generateSingleCar(): List[Car] =
    import model.driver.DriverGenerator.generateDrivers
    val List(leclerc) = generateDrivers().take(0)

    List(
      Car(
        "Ferrari",
        16,
        795.0,
        leclerc,
        maxFuel = 110.0
      )
    )
