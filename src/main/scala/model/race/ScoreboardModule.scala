package model.race

import model.car.CarModule.Car

/** Module that defines a scoreboard for a race, keeping track of lap times and race order. */
object ScoreboardModule:

  /** A scoreboard maintains race position and lap times for each car. */
  trait Scoreboard:

    /** Current order of the race based on completed laps and total time. */
    def raceOrder: List[Car]

    /** Mapping from each car to its list of completed lap times. */
    def lapsByCar: Map[Car, List[Double]]

    /** Records a new lap time for a given car.
      *
      * @param car
      *   the car that completed a lap
      * @param lapTime
      *   the lap time in seconds
      * @return
      *   a new updated [[Scoreboard]] instance
      */
    def recordLap(car: Car, lapTime: Double): Scoreboard

    override def toString: String =
      "" + lapsByCar.foreach(e => println(e))

  /** Internal implementation of the [[Scoreboard]] trait. */
  private case class ScoreboardImpl(
      raceOrder: List[Car],
      lapsByCar: Map[Car, List[Double]]
  ) extends Scoreboard:

    /** Records a lap and returns a new scoreboard with updated race order and lap data. */
    override def recordLap(car: Car, lapTime: Double): Scoreboard =
      val updatedLaps = lapsByCar.updatedWith(car) {
        case Some(times) => Some(lapTime :: times)
        case None => Some(List(lapTime))
      }

      // Sort by most laps (descending), then by lowest total lap time
      val newOrder = updatedLaps.toList
        .sortBy { case (_, laps) => (-laps.size, laps.sum) }
        .map(_._1)

      ScoreboardImpl(newOrder, updatedLaps)

  /** Factory for creating a new empty scoreboard from an initial list of cars. */
  object Scoreboard:
    /** Initializes a scoreboard with given list of cars (no laps completed). */
    def apply(initialOrder: List[Car]): Scoreboard =
      ScoreboardImpl(
        raceOrder = initialOrder,
        lapsByCar = initialOrder.map(_ -> List.empty[Double]).toMap
      )
