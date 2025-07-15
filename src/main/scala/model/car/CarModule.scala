package model.car

import model.shared.Coordinate
import model.car.DriverModule.Driver
import model.shared.Constants.*
import model.car.DriverGenerator.generateDrivers
import model.car.TireModule.Tire

object CarModule:

  /** A racing car in the simulation. */
  trait Car:
    def model: String
    def carNumber: Int
    def weightKg: Double
    def driver: Driver
    def maxFuel: Double
    def fuelLevel: Double
    def degradeState: Double
    def currentSpeed: Double
    def position: Coordinate
    def tire: Tire

    /** Checks whether the car has run out of fuel.
      *
      * @return
      *   `true` if the fuel level is 0 or less, `false` otherwise.
      */
    def isOutOfFuel: Boolean = fuelLevel <= MinFuelLevel

    /** Checks whether the tires need to be changed.
      *
      * Tires are considered worn out if degrade state is over 80%.
      *
      * @return
      *   `true` if degrade state > 80%, `false` otherwise.
      */
    def needsTireChange: Boolean = degradeState >= TireWearLimit

    /** Creates a new [[Car]] instance with updated simulation state.
      *
      * @param speed
      *   the updated speed of the car
      * @param fuelConsumed
      *   the amount of fuel consumed since last update
      * @param degradeIncrease
      *   the additional tire degradation since last update
      * @param newPosition
      *   the updated position of the car on the track
      * @return
      *   a new immutable instance of [[Car]] with updated values
      */
    def withUpdatedState(
        speed: Double,
        fuelConsumed: Double,
        degradeIncrease: Double,
        newPosition: Coordinate,
        tire: Tire
    ): Car

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
      * @param fuelLevel
      *   the current fuel level
      * @param degradeState
      *   the current tire wear level (0 to 100%)
      * @param currentSpeed
      *   the car's speed in km/h
      * @param position
      *   the car's current position on the track
      * @param tire
      *   current tire of the car
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
        maxFuel: Double,
        fuelLevel: Double,
        degradeState: Double,
        currentSpeed: Double,
        position: Coordinate,
        tire: Tire
    ): Car =
      validateCar(model, carNumber, weightKg, driver, maxFuel, fuelLevel, degradeState, currentSpeed, position, tire)
      CarImpl(model, carNumber, weightKg, driver, maxFuel, fuelLevel, degradeState, currentSpeed, position, tire)

    /** Deconstructs a [[Car]] instance into its parameters.
      *
      * @param c
      *   the car to deconstruct
      * @return
      *   a tuple containing all car attributes
      */
    def unapply(c: Car): Option[(String, Int, Double, Driver, Double, Double, Double, Double, Coordinate, Tire)] =
      Some((c.model, c.carNumber, c.weightKg, c.driver, c.maxFuel, c.fuelLevel, c.degradeState, c.currentSpeed,
          c.position, c.tire))

    private def validateCar(
        model: String,
        carNumber: Int,
        weightKg: Double,
        driver: Driver,
        maxFuel: Double,
        fuelLevel: Double,
        degradeState: Double,
        currentSpeed: Double,
        position: Coordinate,
        tire: Tire
    ): Unit =
      require(model != null, "Model cannot be null")
      require(carNumber > 0, "Car number must be a positive int")
      require(driver != null, "Driver cannot be null")
      require(position != null, "Position cannot be null")
      require(tire != null, "Tire cannot be null")
      require(!weightKg.isNaN && !weightKg.isInfinity && weightKg >= 0,
        "Car weight must be a valid non-negative number")
      require(!maxFuel.isNaN && !maxFuel.isInfinity && maxFuel >= 0, "Max fuel must be a valid non-negative number")
      require(
        !fuelLevel.isNaN && !fuelLevel.isInfinity && fuelLevel >= 0 && fuelLevel <= maxFuel,
        "Fuel level must be within 0 and maxFuel"
      )
      require(
        degradeState <= MaxTireLevel && !degradeState.isNaN && !degradeState.isInfinity && degradeState >= 0,
        s"Tire degradation must be between 0 and $MaxTireLevel"
      )
      require(!currentSpeed.isNaN && !currentSpeed.isInfinity && currentSpeed >= 0,
        "Speed must be a valid non-negative number")

  /** Internal implementation of [[Car]]. */
  private case class CarImpl(
      override val model: String,
      override val carNumber: Int,
      override val weightKg: Double,
      override val driver: Driver,
      override val maxFuel: Double,
      override val fuelLevel: Double,
      override val degradeState: Double,
      override val currentSpeed: Double,
      override val position: Coordinate,
      override val tire: Tire
  ) extends Car:

    /** @inheritdoc */
    override def withUpdatedState(
        speed: Double,
        fuelConsumed: Double,
        degradeIncrease: Double,
        newPosition: Coordinate,
        tire: Tire
    ): Car =
      Car(
        model,
        carNumber,
        weightKg,
        driver,
        maxFuel,
        (fuelLevel - fuelConsumed).max(MinFuelLevel),
        (degradeState + degradeIncrease).min(MaxTireLevel),
        speed,
        newPosition,
        tire
      )

import model.car.CarModule.Car
import model.car.TireModule.*
object CarGenerator:

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
        maxFuel = 110.0,
        fuelLevel = 110.0,
        degradeState = 0.0,
        currentSpeed = 0.0,
        position = Coordinate(0, 0),
        Tire(TireModule.TireType.Medium)
      ),
      Car(
        "Mercedes",
        44,
        800.0,
        hamilton,
        maxFuel = 110.0,
        fuelLevel = 110.0,
        degradeState = 0.0,
        currentSpeed = 0.0,
        position = Coordinate(0, 0),
        Tire(TireModule.TireType.Medium)
      ),
      Car("McLaren", 4, 790.0, norris, maxFuel = 110.0, fuelLevel = 110.0, degradeState = 0.0, currentSpeed = 0.0,
        position = Coordinate(0, 0), Tire(TireModule.TireType.Medium)),
      Car(
        "Alpine",
        43,
        805.0,
        colapinto,
        maxFuel = 110.0,
        fuelLevel = 110.0,
        degradeState = 0.0,
        currentSpeed = 0.0,
        position = Coordinate(0, 0),
        Tire(TireModule.TireType.Medium)
      )
    )
