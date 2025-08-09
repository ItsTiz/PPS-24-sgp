package controller.ui

import model.weather.WeatherModule.Weather
import model.tracks.TrackModule.TrackType

trait SimulationConfigListener:
  /** Configures the simulation with the provided parameters and starts it. This method is called when the user clicks
    * the start button in the StartView.
    *
    * @param laps
    *   the number of laps for the race
    * @param cars
    *   the number of cars/drivers in the simulation
    * @param weather
    *   the initial weather conditions
    * @param track
    *   the track of the race
    */
  def onSimulationConfigured(laps: Int, cars: Int, weather: Weather, track: TrackType): Unit
