package model.car

import model.shared.Coordinate
import model.car.DriverModule.Driver
import model.shared.Constants.*

object CarModule:

  /** A racing car in the simulation. */
  trait Car:
    def model: String
    def weightKg: Double
    def driver: Driver
    def maxFuel: Double
    def fuelLevel: Double
    def degradeState: Double
    def currentSpeed: Double
    def position: Coordinate

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
        newPosition: Coordinate
    ): Car

  /** Factory and extractor for [[Car]] instances. */
  object Car:

    /** Creates a new [[Car]] instance with the given parameters.
      *
      * @param model
      *   the car's model name
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
      * @return
      *   a new instance of [[Car]]
      * @throws IllegalArgumentException
      *   if any validation constraint is violated
      */
    def apply(
        model: String,
        weightKg: Double,
        driver: Driver,
        maxFuel: Double,
        fuelLevel: Double,
        degradeState: Double,
        currentSpeed: Double,
        position: Coordinate
    ): Car =
      validateCar(model, weightKg, driver, maxFuel, fuelLevel, degradeState, currentSpeed, position)
      CarImpl(model, weightKg, driver, maxFuel, fuelLevel, degradeState, currentSpeed, position)

    /** Deconstructs a [[Car]] instance into its parameters.
      *
      * @param c
      *   the car to deconstruct
      * @return
      *   a tuple containing all car attributes
      */
    def unapply(c: Car): Option[(String, Double, Driver, Double, Double, Double, Double, Coordinate)] =
      Some((c.model, c.weightKg, c.driver, c.maxFuel, c.fuelLevel, c.degradeState, c.currentSpeed, c.position))

    private def validateCar(
        model: String,
        weightKg: Double,
        driver: Driver,
        maxFuel: Double,
        fuelLevel: Double,
        degradeState: Double,
        currentSpeed: Double,
        position: Coordinate
    ): Unit =
      require(model != null, "Model cannot be null")

      require(driver != null, "Driver cannot be null")
      require(position != null, "Position cannot be null")
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
      override val weightKg: Double,
      override val driver: Driver,
      override val maxFuel: Double,
      override val fuelLevel: Double,
      override val degradeState: Double,
      override val currentSpeed: Double,
      override val position: Coordinate
  ) extends Car:

    /** @inheritdoc */
    override def withUpdatedState(
        speed: Double,
        fuelConsumed: Double,
        degradeIncrease: Double,
        newPosition: Coordinate
    ): Car =
      Car(
        model,
        weightKg,
        driver,
        maxFuel,
        (fuelLevel - fuelConsumed).max(MinFuelLevel),
        (degradeState + degradeIncrease).min(MaxTireLevel),
        speed,
        newPosition
      )
