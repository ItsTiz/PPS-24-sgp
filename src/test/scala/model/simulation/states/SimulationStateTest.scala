package model.simulation.states

import RaceStateModule.RaceState
import model.car.CarGenerator
import model.car.CarModule.Car
import model.car.TireModule.Tire
import model.simulation.states.CarStateModule.CarState
import model.simulation.states.SimulationModule.*
import model.weather.WeatherModule.Weather
import model.tracks.TrackSectorModule.TrackSector
import model.tracks.TrackSectorModule.TrackSector.straight
import model.car.TireModule.TireType.Medium
import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*

class SimulationStateTest extends AnyFlatSpec with Matchers:

  val cars: List[Car] = CarGenerator.generateCars()
  val trackStraight: TrackSector = straight(0, 500, 320, 200, 1.0)
  val carStates: List[CarState] = cars.map(c =>
    CarState(
      maxFuel = c.maxFuel,
      fuelLevel = c.maxFuel, // cars start from max fuel
      currentSpeed = 0.0,
      progress = 0.0,
      tire = Tire(Medium, degradeState = 0.0),
      currentLaps = 0,
      currentSector = trackStraight
    )
  )

  val dummyState: RaceState =
    RaceState.withInitialEvents(Map from (cars zip carStates), List.empty, Weather.Sunny, laps = 3)
  val simState: SimulationState = SimulationState()

  "getState" should "retrieve the current RaceState unchanged" in:
    val simulation = simState.getState
    val (finalState, result) = simulation.run(dummyState).value
    result shouldBe dummyState
    finalState shouldBe dummyState

  "setState" should "update the RaceState to the given one" in:
    val newState = RaceState.withInitialEvents(Map from (cars zip carStates), List.empty, Weather.Rainy, laps = 5)
    val simulation = simState.setState(newState)
    val (finalState, result) = simulation.run(dummyState).value
    finalState shouldBe newState
    result shouldBe ()

  "pure" should "return a Simulation that yields a value and does not change the state" in:
    val simulation = simState.pure(42)
    val (finalState, result) = simulation.run(dummyState).value
    result shouldBe 42
    finalState shouldBe dummyState

  "Simulation.run extension" should "run a Simulation and return the result and final state" in:
    val simulation =
      for
        stateBefore <- simState.getState
        _ <- simState.setState(stateBefore)
        result <- simState.pure("done")
      yield result

    val (finalState, result) = simulation.run(dummyState).value
    result shouldBe "done"
    finalState shouldBe dummyState
