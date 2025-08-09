package view.weather

import scalafx.scene.image.Image
import model.weather.WeatherModule.Weather

/** Utility object responsible for providing weather icons for the simulation. If the icon resource is not found, it
  * falls back to a placeholder image.
  */
object WeatherView:

  /** Returns the weather icon image corresponding to the given weather condition.
    *
    * @param weather
    *   the current weather condition
    * @return
    *   the Image for the weather icon; falls back to a placeholder if resource not found
    */
  def getWeatherIcon(weather: Weather): Image =
    val iconPath = weather match
      case Weather.Sunny => "/icons/sunny.png"
      case Weather.Rainy => "/icons/rainy.png"
      case Weather.Foggy => "/icons/foggy.png"

    val stream = getClass.getResourceAsStream(iconPath)
    if stream == null then
      println(s"ERROR: Could not find icon at $iconPath")
      new Image("https://via.placeholder.com/50") // fallback placeholder image URL
    else
      new Image(stream)
