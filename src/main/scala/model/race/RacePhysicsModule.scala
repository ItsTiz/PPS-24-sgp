package model.race

import model.car.CarModule.Car
import model.car.DrivingStyleModule.DrivingStyle
import model.simulation.states.CarStateModule.CarState
import model.simulation.weather.WeatherModule.Weather
import model.tracks.TrackSectorModule.TrackSector

object RacePhysicsModule:

  trait RacePhysics:

    /** A function returning a new CarState based on the previous one.
      */
    def advanceCar(car: Car, carState: CarState)(weather: Weather): CarState

  object RacePhysics:
    def apply(): RacePhysics = RacePhysicsImpl

  private object RacePhysicsImpl extends RacePhysics:

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

    private def calculateNewSpeed(car: Car, carState: CarState)(sector: TrackSector, weather: Weather): Double =
      val baseSpeed = if carState.currentSpeed <= 0 then
        sector.avgSpeed * carState.tire.speedModifier
      else
        carState.currentSpeed

      val grip = sector.gripIndex * carState.tire.grip * weather.gripModifier
      val styleBoost = 1.0 + car.driver.style.speedIncreasePercent
      val tireHealth = 0.005//carState.tire.degradeState
      val weightPenalty = 800.0 / car.weightKg //TODO magic numbers
      val effectiveSpeed = math.round(baseSpeed * grip * styleBoost *  (1 - tireHealth) * weightPenalty)

      effectiveSpeed.min(sector.maxSpeed.toLong)


  private def calculateNewProgress(car: Car, carState: CarState)(sector: TrackSector, weather: Weather): Double =
    val deltaTime = 0.1 // in seconds; simulation step length //TODO magic numbers
    val speedMps = carState.currentSpeed / 3.6
    println(s"speedMps: $speedMps")
    val distanceTravelled = speedMps * deltaTime
    println(s"distanceTravelled: $distanceTravelled")
    val baseProgress = distanceTravelled / sector.sectorLength
    println(s"baseProgress: $baseProgress")
    val gripFactor = carState.tire.grip * (1 - carState.tire.degradeState) * weather.gripModifier * sector.gripIndex
    println(s"gripFactor: $gripFactor")
    val stylePenalty = 1.0 - (car.driver.style.speedIncreasePercent * 0.2)
    println(s"stylePenalty: $stylePenalty")
    val weightPenalty = 800.0 / car.weightKg //TODO magic numbers
    println(s"weightPenalty: $weightPenalty")
    val modifier = gripFactor * stylePenalty * weightPenalty
    println(s"modifier: $modifier")

    (baseProgress * modifier).min(1)
