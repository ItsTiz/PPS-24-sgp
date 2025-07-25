package model.simulation.states

import model.car.TireModule.Tire
import model.shared.Constants.{MaxTireLevel, MinFuelLevel, TireWearLimit}
import model.tracks.TrackSectorModule.TrackSector

object CarStateModule:

  // TODO to expose - export - tireNeedsChange from Tires? most likely.

  /** A racing car in the simulation. */
  trait CarState:
    def maxFuel: Double
    def fuelLevel: Double
    def currentSpeed: Double
    def progress: Double // number from 0 to 1
    def tire: Tire
    def currentLaps: Int
    def currentSector: TrackSector

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
        tire: Tire,
        currentLaps: Int,
        currentSector: TrackSector
    ): CarState

    def copyLike(
        fuelLevel: Double = this.fuelLevel,
        currentSpeed: Double = this.currentSpeed,
        progress: Double = this.progress,
        tire: Tire = this.tire,
        currentLaps: Int = this.currentLaps,
        currentSector: TrackSector = this.currentSector
    ): CarState

    override def toString: String =
      s"""CarState(
         |    maxFuel: $maxFuel l
         |    fuelLevel: $fuelLevel l
         |    currentSpeed: $currentSpeed km/h
         |    progress: $progress
         |    tire: $tire
         |    currentLaps: $currentLaps
         |    currentSector: $currentSector
         |)""".stripMargin

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
    def apply(
        maxFuel: Double,
        fuelLevel: Double,
        currentSpeed: Double,
        progress: Double,
        tire: Tire,
        currentLaps: Int,
        currentSector: TrackSector
    ): CarState =
      validateCar(maxFuel, fuelLevel, currentSpeed, progress, tire, currentLaps, currentSector)
      CarStateImpl(maxFuel, fuelLevel, currentSpeed, progress, tire, currentLaps, currentSector)

    /** Deconstructs a [[Car]] instance into its parameters.
      *
      * @param c
      *   the car state to deconstruct
      * @return
      *   a tuple containing all car attributes
      */
    def unapply(c: CarState): Option[(Double, Double, Double, Double, Tire, Int, TrackSector)] =
      Some((c.maxFuel, c.fuelLevel, c.currentSpeed, c.progress, c.tire, c.currentLaps, c.currentSector))

    private def validateCar(maxFuel: Double,
        fuelLevel: Double,
        currentSpeed: Double,
        progress: Double,
        tire: Tire,
        currentLaps: Int,
        currentSector: TrackSector
    ): Unit =

      require(!progress.isNaN && !progress.isInfinity && progress >= 0 && progress <= 1,
        "The progress must be from 0 to 1.")
      require(tire != null, "Tire cannot be null")
      require(currentSector != null, "Current sector cannot be null")
      require(!maxFuel.isNaN && !maxFuel.isInfinity && maxFuel >= 0, "Max fuel must be a valid non-negative number")
      require(
        !fuelLevel.isNaN && !fuelLevel.isInfinity && fuelLevel >= 0 && fuelLevel <= maxFuel,
        "Fuel level must be within 0 and maxFuel"
      )
      require(!currentSpeed.isNaN && !currentSpeed.isInfinity && currentSpeed >= 0,
        "Speed must be a valid non-negative number")
      require(currentLaps >= 0, "Current laps must be a non-negative number")

  /** Internal implementation of [[CarState]]. */
  private case class CarStateImpl(
    
      override val maxFuel: Double,
      override val fuelLevel: Double,
      override val currentSpeed: Double,
      override val progress: Double,
      override val tire: Tire,
      override val currentLaps: Int,
      override val currentSector: TrackSector
  ) extends CarState:

    /** @inheritdoc */
    override def withUpdatedState(
        speed: Double,
        fuelConsumed: Double,
        degradeIncrease: Double,
        progress: Double,
        tire: Tire,
        currentLaps: Int,
        currentSector: TrackSector
    ): CarState =
      CarState(
        maxFuel,
        (fuelLevel - fuelConsumed).max(MinFuelLevel),
        speed,
        progress,
        Tire(tire.tireType, (tire.degradeState + degradeIncrease).min(MaxTireLevel)),
        currentLaps,
        currentSector
      )

    override def copyLike(
        fuelLevel: Double = this.fuelLevel,
        currentSpeed: Double = this.currentSpeed,
        progress: Double = this.progress,
        tire: Tire = this.tire,
        currentLaps: Int = this.currentLaps,
        currentSector: TrackSector = this.currentSector
    ): CarState =
      CarState(
        maxFuel,
        fuelLevel,
        currentSpeed,
        progress,
        tire,
        currentLaps,
        currentSector
      )
