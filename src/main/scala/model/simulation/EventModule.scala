package model.simulation

import model.car.CarModule.Car
import model.tracks.TrackSectorModule.TrackSector

object EventModule:

  sealed trait Event:
    def timestamp: Double

  object Event:
    extension (e: Event)
      def asString: String = s"Event[+T${e.timestamp}]"

  sealed trait CarEvent extends Event:
    def car: Car

  private def validateEvent(timestamp: Double): Unit =
    require(timestamp > 0, "Timestamp needs to be positive.")

  case class TrackSectorEntered(car: Car, trackSector: TrackSector, timestamp: Double) extends CarEvent:
    validateEvent(timestamp)
  case class TrackSectorExited(car: Car, timestamp: Double) extends CarEvent:
    validateEvent(timestamp)
  case class PitStopRequest(car: Car, timestamp: Double) extends CarEvent:
    validateEvent(timestamp)
