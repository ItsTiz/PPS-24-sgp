package model.race

import org.scalatest.funsuite.AnyFunSuite
import model.race.ScoreboardModule.*
import model.car.CarModule.*
import model.car.DriverModule.*
import model.car.DrivingStyleModule.*
import model.car.TireModule
import model.car.TireModule.Tire

class ScoreboardTest extends AnyFunSuite:

  val car1 =
    Car("Ferrari", 16, 750.0, Driver("Leclerc", DrivingStyle.balanced), 100.0)
  val car2 =
    Car("Mercedes", 44, 750.0, Driver("Hamilton", DrivingStyle.aggressive), 100.0)
  val car3 =
    Car("Haas", 43, 750.0, Driver("Bearman", DrivingStyle.defensive), 100.0)

  test("Scoreboard initializes correctly with empty laps") {
    val scoreboard = Scoreboard(List(car1, car2, car3))
    assert(scoreboard.lapsByCar(car1).isEmpty)
    assert(scoreboard.lapsByCar(car2).isEmpty)
    assert(scoreboard.lapsByCar(car3).isEmpty)
    assert(scoreboard.raceOrder == List(car1, car2, car3))
  }

  test("Scoreboard records a lap and updates the order") {
    val scoreboard = Scoreboard(List(car1, car2, car3))
      .recordLap(car1, 72.0)
      .recordLap(car2, 70.0)
      .recordLap(car2, 69.0)

    // car2 has 2 laps, car 1 has 1 lap, car 3 has 0 lap
    assert(scoreboard.lapsByCar(car2).size == 2)
    assert(scoreboard.lapsByCar(car1).size == 1)
    assert(scoreboard.lapsByCar(car3).isEmpty)

    assert(scoreboard.raceOrder == List(car2, car1, car3))
  }

  test("Scoreboard records a lap and updates the order, same number of laps for all the cars") {
    val scoreboard1 = Scoreboard(List(car1, car2, car3))
      .recordLap(car1, 72.0)
      .recordLap(car2, 70.0)
      .recordLap(car3, 73.0) // Totals: car1=72, car2=70, car3=73 → Order: 2,1,3

    assert(scoreboard1.raceOrder == List(car2, car1, car3))

    val scoreboard2 = scoreboard1
      .recordLap(car1, 68.0) // car1 total: 140
      .recordLap(car2, 76.0) // car2 total: 146
      .recordLap(car3, 70.0) // car3 total: 143 → Order: 1,3,2

    assert(scoreboard2.raceOrder == List(car1, car3, car2))

    val scoreboard3 = scoreboard2
      .recordLap(car1, 69.0) // car1 total: 209
      .recordLap(car2, 67.0) // car2 total: 213
      .recordLap(car3, 78.0) // car3 total: 221 → Order: 1,2,3

    assert(scoreboard3.lapsByCar(car1).size == 3)
    assert(scoreboard3.lapsByCar(car2).size == 3)
    assert(scoreboard3.lapsByCar(car3).size == 3)

    assert(scoreboard3.raceOrder == List(car1, car2, car3))
  }
