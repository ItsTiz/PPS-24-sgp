package model.simulation.events

import model.simulation.weather.WeatherModule.Weather
import model.tracks.TrackSectorModule.TrackSector

object EventModule:

  /** An event that occurs in the simulation. Events are enqueued in a priority list. */
  sealed trait Event:

    /** The simulation or system time when the event occurred. */
    def timestamp: BigDecimal

  /** Utility functions for events. */
  object Event:

    extension (e: Event)
      /** @return the string representation of the event */
      def asString: String = s"Event[+T${e.timestamp}]"

  /** A specific type of event only for cars. */
  sealed trait CarEvent extends Event:
    def carId: Int

    // override def toString: String = s"CarEvent[+T$timestamp]{carId: $carId}"

  /** A specific type of event only for weather types. */
  sealed trait WeatherEvent extends Event:
    def weather: Weather

    // override def toString: String = s"WeatherEvent[+T$timestamp]{weather: $weather}"

  private def validateEvent(timestamp: BigDecimal): Unit =
    require(timestamp >= 0, "Timestamp needs to be positive.")

  /** Event representing a car entering a different track sector.
    *
    * @param car
    *   The car that triggered the event
    * @param trackSector
    *   The track sector that the car entered
    * @param timestamp
    *   The simulation or system time when the event occurred
    */
  case class TrackSectorEntered(carId: Int, trackSector: TrackSector, timestamp: BigDecimal) extends CarEvent:
    validateEvent(timestamp)

  /** Event representing a car exiting its current track sector.
    *
    * @param car
    *   The car that triggered the event
    * @param timestamp
    *   The simulation or system time when the event occurred
    */
  case class TrackSectorExited(carId: Int, timestamp: BigDecimal) extends CarEvent:
    validateEvent(timestamp)

  /** Event representing a car requesting a pit stop.
    *
    * @param car
    *   The car that made the pit stop request
    * @param timestamp
    *   The simulation or system time when the event occurred
    */
  case class PitStopRequest(carId: Int, timestamp: BigDecimal) extends CarEvent:
    validateEvent(timestamp)

  case class CarProgressUpdate(carId: Int, timestamp: BigDecimal) extends CarEvent:
    validateEvent(timestamp)

  case class CarCompletedLap(carId: Int, timestamp: BigDecimal) extends CarEvent:
    validateEvent(timestamp)

  case class WeatherChanged(weather: Weather, timestamp: BigDecimal) extends WeatherEvent:
    validateEvent(timestamp)
