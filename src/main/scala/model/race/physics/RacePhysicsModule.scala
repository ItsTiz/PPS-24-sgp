package model.race.physics

object RacePhysicsModule:
  import model.car.CarModule.Car
  import model.simulation.states.CarStateModule.CarState
  import model.simulation.weather.WeatherModule.Weather

  /** Defines the physics calculations for advancing cars during a race simulation. */
  trait RacePhysics:

    /** Calculates a new car state based on the previous state, applying physics rules.
      *
      * @param car
      *   the car being simulated
      * @param carState
      *   the current state of the car
      * @param weather
      *   the current weather conditions affecting the race
      * @return
      *   a new [[CarState]] with updated values for speed, progress, fuel, and tire degradation
      */
    def advanceCar(car: Car, carState: CarState)(weather: Weather): CarState

  /** Factory for creating [[RacePhysics]] instances. */
  object RacePhysics:
    /** Creates a new race physics calculator.
      *
      * @return
      *   a new [[RacePhysics]] instance
      */
    def apply(): RacePhysics = RacePhysicsImpl

  /** Internal implementation of the [[RacePhysics]] trait. */
  private object RacePhysicsImpl extends RacePhysics:
    import model.tracks.TrackSectorModule.TrackSector
    import model.utils.inverseRatio
    import model.car.CarConstants.maxTireLevel
    import model.simulation.states.CarStateConstants.averageCarWeight

    /** Calculates a new car state based on the previous state, applying physics rules.
      *
      * This implementation updates the car's speed, fuel consumption, tire degradation, and progress along the track
      * based on the car's characteristics, current state, and weather conditions.
      *
      * @param car
      *   the car being simulated
      * @param carState
      *   the current state of the car
      * @param weather
      *   the current weather conditions affecting the race
      * @return
      *   a new [[CarState]] with updated values
      */
    override def advanceCar(car: Car, carState: CarState)(weather: Weather): CarState =
      carState.withUpdatedState(
        speed = calculateNewSpeed(car, carState)(carState.currentSector, weather),
        fuelConsumed = getConsumedFuel(car),
        degradeIncrease = getTireDegradeIncrease(car)(weather),
        newProgress = calculateNewProgress(car, carState)(carState.currentSector, weather),
        tire = carState.tire,
        currentLaps = carState.currentLaps,
        currentSector = carState.currentSector
      )

    private def getTireDegradeIncrease(car: Car)(weather: Weather): Double =
      car.driver.style.tireDegradationRate * weather.tireWearModifier

    private def getConsumedFuel(car: Car): Double =
      car.driver.style.fuelConsumptionRate

    private def getGripFactor(carState: CarState, sector: TrackSector, weather: Weather): Double =
      carState.tire.grip *
        inverseRatio(carState.tire.degradeState, maxTireLevel) *
        weather.gripModifier *
        sector.gripIndex

    private def calculateNewSpeed(car: Car, carState: CarState)(sector: TrackSector, weather: Weather): Double =
      val baseSpeed = sector.avgSpeed * carState.tire.speedModifier
      val styleBoost = 1.0 + car.driver.style.speedIncreasePercent
      val weightPenalty = averageCarWeight / car.weightKg
      val effectiveSpeed =
        baseSpeed * getGripFactor(carState, sector, weather) * styleBoost * weightPenalty

      math.round(effectiveSpeed.min(sector.maxSpeed.toLong))

    private def calculateNewProgress(car: Car, carState: CarState)(sector: TrackSector, weather: Weather): Double =
      import model.race.RaceConstants.{logicalTimeStep, maxSectorProgress}
      import model.utils.toMetersPerSecond
      val distanceTravelled = toMetersPerSecond(carState.currentSpeed) * logicalTimeStep
      val baseProgress = distanceTravelled / sector.sectorLength
      val stylePenalty = 1.0 - (car.driver.style.speedIncreasePercent * 0.2)
      val weightPenalty = averageCarWeight / car.weightKg
      val modifier = getGripFactor(carState, sector, weather) * stylePenalty * weightPenalty

      (carState.progress + baseProgress * modifier).min(maxSectorProgress)
