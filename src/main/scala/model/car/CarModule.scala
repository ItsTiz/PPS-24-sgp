package model.car

import model.driver.DriverModule.Driver
import model.common.Constants.*
import model.driver.DriverGenerator.generateDrivers

object CarModule:

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
    val List(leclerc, hamilton, norris, colapinto) = generateDrivers()

    List(
      Car(
        "Ferrari",
        16,
        795.0,
        leclerc,
        maxFuel = 110.0
      ),
      Car(
        "Mercedes",
        44,
        800.0,
        hamilton,
        maxFuel = 110.0
      ),
      Car("McLaren", 4, 790.0, norris, maxFuel = 110.0),
      Car(
        "Alpine",
        43,
        805.0,
        colapinto,
        maxFuel = 110.0
      )
    )

  def generateSingleCar(): List[Car] =
    val List(leclerc, hamilton, norris, colapinto) = generateDrivers()

    List(
      Car(
        "Ferrari",
        16,
        795.0,
        leclerc,
        maxFuel = 110.0
      )
    )
