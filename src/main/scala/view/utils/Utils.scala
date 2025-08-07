package view.utils

/** Formats a time value in milliseconds into a string of format mm:ss.sss.
  *
  * @param milliseconds
  *   the time duration in milliseconds
  * @return
  *   a string formatted as minutes:seconds.milliseconds
  */
def formatTime(milliseconds: Double): String =
  val totalSeconds = milliseconds / 1000.0
  val minutes = totalSeconds.toInt / 60
  val remainingSeconds = totalSeconds % 60
  f"$minutes%02d:${remainingSeconds}%06.3f"

import model.car.CarModule.Car
import model.simulation.states.RaceStateModule.RaceState
def computeLeaderAndTotalTimes(raceState: RaceState): (Option[Car], Double, Map[Car, Double]) = {
  val totalTimeByCar: Map[Car, Double] = raceState.scoreboard.lapsByCar.view.mapValues(_.sum).toMap
  val leaderOpt: Option[Car] = raceState.scoreboard.raceOrder.headOption
  val leaderTime: Double = leaderOpt.flatMap(totalTimeByCar.get).getOrElse(0.0)

  (leaderOpt, leaderTime, totalTimeByCar)
}
