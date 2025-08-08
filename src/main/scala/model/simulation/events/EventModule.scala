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

  /** A specific type of event only for weather types. */
  sealed trait WeatherEvent extends Event:
    def weather: Weather

  private def validateEvent(timestamp: BigDecimal): Unit =
    require(timestamp >= 0, "Timestamp needs to be positive.")

  /** Event representing a car entering a different track sector.
    *
    * @param carId
    *   The ID of the car that triggered the event
    * @param trackSector
    *   The track sector that the car entered
    * @param timestamp
    *   The simulation or system time when the event occurred
    */
  case class TrackSectorEntered(carId: Int, trackSector: TrackSector, timestamp: BigDecimal) extends CarEvent:
    validateEvent(timestamp)

  /** Event representing a car requesting a pit stop.
    *
    * @param carId
    *   The ID of the car that made the pit stop request
    * @param timestamp
    *   The simulation or system time when the event occurred
    */
  case class PitStopRequest(carId: Int, timestamp: BigDecimal) extends CarEvent:
    validateEvent(timestamp)

  /** Event representing an update in the car's progress along the track.
    *
    * @param carId
    *   The ID of the car whose progress was updated
    * @param timestamp
    *   The simulation or system time when the event occurred
    */
  case class CarProgressUpdate(carId: Int, timestamp: BigDecimal) extends CarEvent:
    validateEvent(timestamp)

  /** Event indicating that a car has completed a lap.
    *
    * @param carId
    *   The ID of the car that completed the lap
    * @param timestamp
    *   The simulation or system time when the event occurred
    */
  case class CarCompletedLap(carId: Int, timestamp: BigDecimal) extends CarEvent:
    validateEvent(timestamp)

  /** Event indicating a change in weather conditions.
    *
    * @param weather
    *   The new weather state
    * @param timestamp
    *   The simulation or system time when the weather changed
    */
  case class WeatherChanged(weather: Weather, timestamp: BigDecimal) extends WeatherEvent:
    validateEvent(timestamp)
