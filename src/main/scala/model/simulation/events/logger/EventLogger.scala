package model.simulation.events.logger

import model.simulation.events.EventModule.*

object EventLogger extends Logger[Event]:

  /** Logs a simulation [[Event]] by printing a human-readable message.
    *
    * @param event
    *   the simulation event to log
    */
  override def log(event: Event): Unit = event match
    case TrackSectorEntered(carId, sector, timestamp) =>
      println(s"[+T$timestamp] Car#$carId entered sector#${sector.id}.")
    case PitStopRequest(carId, timestamp) =>
      println(s"[+T$timestamp] Pit-stop service for Car#$carId.")
    case CarCompletedLap(carId, timestamp) =>
      println(s"[+T$timestamp] Car#$carId has completed a lap.")
    case WeatherChanged(weather, timestamp) =>
      println(s"[+T$timestamp] Weather has changed to #$weather.")
    case CarProgressUpdate(carId, timestamp) => ()
