package model.simulation.states

import model.car.TireModule.Tire
import model.shared.Constants.{MaxTireLevel, MinFuelLevel, TireWearLimit}

object CarModule:

  /** A racing car in the simulation. */
  trait CarState:
    def maxFuel: Double
    def fuelLevel: Double
    def currentSpeed: Double
    def progress: Double // number from 0 to 1
    def tire: Tire

    /** Checks whether the car has run out of fuel.
      *
      * @return
      *   `true` if the fuel level is 0 or less, `false` otherwise.
      */
    def isOutOfFuel: Boolean = fuelLevel <= MinFuelLevel

    /** Creates a new [[CarState]] instance with updated simulation state.
      *
      * @param speed
      *   the updated speed of the car
      * @param fuelConsumed
      *   the amount of fuel consumed since last update
      * @param degradeIncrease
      *   the additional tire degradation since last update
      * @param newProgress
      *   the updated progress of the car on the track sector
      * @return
      *   a new immutable instance of [[CarState]] with updated values
      */
    def withUpdatedState(
        speed: Double,
        fuelConsumed: Double,
        degradeIncrease: Double,
        newProgress: Double,
        tire: Tire
    ): CarState

  /** Factory and extractor for [[CarState]] instances. */
  object CarState:

    /** Creates a new [[CarState]] instance with the given parameters.
      *
      * @param maxFuel
      *   max fuel level
      * @param fuelLevel
      *   the current fuel level
      * @param currentSpeed
      *   the car's speed in km/h
      * @param progress
      *   the car's current progress on the track sector
      * @param tire
      *   current tire of the car
      * @return
      *   a new instance of [[CarState]]
      * @throws IllegalArgumentException
      *   if any validation constraint is violated
      */
    def apply(maxFuel: Double,
        fuelLevel: Double,
        currentSpeed: Double,
        progress: Double,
        tire: Tire
    ): CarState =
      validateCar(maxFuel, fuelLevel, currentSpeed, progress, tire)
      CarImpl(maxFuel, fuelLevel, currentSpeed, progress, tire)

    /** Deconstructs a [[Car]] instance into its parameters.
      *
      * @param c
      *   the car state to deconstruct
      * @return
      *   a tuple containing all car attributes
      */
    def unapply(c: CarState): Option[(Double, Double, Double, Double, Tire)] =
      Some((c.maxFuel, c.fuelLevel, c.currentSpeed, c.progress, c.tire))

    private def validateCar(maxFuel: Double,
        fuelLevel: Double,
        currentSpeed: Double,
        progress: Double,
        tire: Tire
    ): Unit =

      require(!progress.isNaN && !progress.isInfinity && progress >= 0 && progress <= 1,
        "The progress must be from 0 to 1.")
      require(tire != null, "Tire cannot be null")
      require(!maxFuel.isNaN && !maxFuel.isInfinity && maxFuel >= 0, "Max fuel must be a valid non-negative number")
      require(
        !fuelLevel.isNaN && !fuelLevel.isInfinity && fuelLevel >= 0 && fuelLevel <= maxFuel,
        "Fuel level must be within 0 and maxFuel"
      )
      require(!currentSpeed.isNaN && !currentSpeed.isInfinity && currentSpeed >= 0,
        "Speed must be a valid non-negative number")

  /** Internal implementation of [[Car]]. */
  private case class CarImpl(
      override val maxFuel: Double,
      override val fuelLevel: Double,
      override val currentSpeed: Double,
      override val progress: Double,
      override val tire: Tire
  ) extends CarState:

    /** @inheritdoc */
    override def withUpdatedState(
        speed: Double,
        fuelConsumed: Double,
        degradeIncrease: Double,
        progress: Double,
        tire: Tire
    ): CarState =
      CarState(
        maxFuel,
        (fuelLevel - fuelConsumed).max(MinFuelLevel),
        speed,
        progress,
        Tire(tire.tireType, (tire.degradeState + degradeIncrease).min(MaxTireLevel))
      )
