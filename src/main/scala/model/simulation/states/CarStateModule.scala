package model.simulation.states

import model.car.TireModule.Tire
import model.tracks.TrackSectorModule.TrackSector

object CarStateModule:

  /** A racing car in the simulation. */
  trait CarState:
    /** The maximum fuel capacity of the car. */
    def maxFuel: Double

    /** The current fuel level of the car. */
    def fuelLevel: Double

    /** The current speed of the car in km/h. */
    def currentSpeed: Double

    /** The current progress on the track sector (number from 0 to 1). */
    def progress: Double // number from 0 to 1

    /** The current tire of the car. */
    val tire: Tire

    /** The number of completed laps by the car. */
    def currentLaps: Int

    /** The current track sector the car is in. */
    def currentSector: TrackSector

    export tire.needsTireChange

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

    /** Creates a copy of this car state with optionally modified fields.
      *
      * @param fuelLevel
      *   the new fuel level (defaults to current fuel level)
      * @param currentSpeed
      *   the new speed (defaults to current speed)
      * @param progress
      *   the new progress (defaults to current progress)
      * @param tireDegradeState
      *   the new tire degradation state (defaults to current tire degradation)
      * @param currentLaps
      *   the new lap count (defaults to current lap count)
      * @param currentSector
      *   the new current sector (defaults to current sector)
      * @return
      *   a new [[CarState]] instance with the specified modifications
      */
    def copyLike(
        fuelLevel: Double = this.fuelLevel,
        currentSpeed: Double = this.currentSpeed,
        progress: Double = this.progress,
        tireDegradeState: Double = this.tire.degradeState,
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
    import model.shared.Constants.{minFuelLevel, minTireDegradeState}
    import model.race.RaceConstants.{maxSectorProgress, minSectorProgress}

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

    extension (carState: CarState)

      /** Checks whether the car has run out of fuel.
        *
        * @return
        *   `true` if the fuel level is 0 or less, `false` otherwise.
        */
      def isOutOfFuel: Boolean = carState match
        case CarState(_, fuelLevel, _, _, _, _, _) => fuelLevel <= minFuelLevel

      /** Checks whether the car has completed the current sector.
        *
        * @return
        *   `true` if the car has reached the end of the current sector, `false` otherwise.
        */
      def hasCompletedSector: Boolean = carState match
        case CarState(_, _, _, progress, _, _, _) => progress >= maxSectorProgress

      /** Checks whether the car has completed the race.
        *
        * @param maxLaps
        *   the maximum number of laps in the race
        * @return
        *   `true` if the car has completed all required laps, `false` otherwise.
        */
      def hasCompletedRace(maxLaps: Int): Boolean = carState match
        case CarState(_, _, _, _, _, laps, _) => laps == maxLaps

      /** Creates a new car state with the car moved to a new sector.
        *
        * @param newSector
        *   the new sector the car is entering
        * @return
        *   a new [[CarState]] with progress reset and the new sector set
        */
      def withNewSector(newSector: TrackSector): CarState =
        carState.copyLike(progress = minSectorProgress, currentSector = newSector)

      /** Creates a new car state with the lap count incremented.
        *
        * @return
        *   a new [[CarState]] with progress reset and lap count increased by 1
        */
      def withUpdatedLaps: CarState =
        carState.copyLike(progress = minSectorProgress, currentLaps = carState.currentLaps + 1)

      /** Creates a new car state with fuel and tires fully restored.
        *
        * @return
        *   a new [[CarState]] with full fuel and new tires
        */
      def withReconditioning: CarState =
        carState.copyLike(fuelLevel = carState.maxFuel, tireDegradeState = minTireDegradeState)

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

    import model.shared.Constants.{minFuelLevel, maxTireLevel}

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
        (fuelLevel - fuelConsumed).max(minFuelLevel),
        speed,
        progress,
        Tire(tire.tireType, (tire.degradeState + degradeIncrease).min(maxTireLevel)),
        currentLaps,
        currentSector
      )

    /** @inheritdoc */
    override def copyLike(
        fuelLevel: Double = this.fuelLevel,
        currentSpeed: Double = this.currentSpeed,
        progress: Double = this.progress,
        tireDegradeState: Double = this.tire.degradeState,
        currentLaps: Int = this.currentLaps,
        currentSector: TrackSector = this.currentSector
    ): CarState =
      CarState(
        maxFuel,
        fuelLevel,
        currentSpeed,
        progress,
        Tire(tire.tireType, tireDegradeState),
        currentLaps,
        currentSector
      )
