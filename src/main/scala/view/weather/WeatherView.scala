package view.weather

import scalafx.scene.image.Image
import model.weather.WeatherModule.Weather

/** Utility object responsible for providing weather icons for the simulation. If the icon resource is not found, it
  * falls back to a placeholder image.
  */

object WeatherView:
  private val iconMap = Map(
    Weather.Sunny -> "/icons/sunny.png",
    Weather.Rainy -> "/icons/rainy.png",
    Weather.Foggy -> "/icons/foggy.png"
  )

  private val fallbackIcon = new Image(getClass.getResourceAsStream("/icons/sunny.png"))

  /** Returns the weather icon image corresponding to the given weather condition.
    *
    * @param weather
    *   the current weather condition
    * @return
    *   the Image for the weather icon; falls back to a placeholder if resource not found
    */

  def getWeatherIcon(weather: Weather): Image =
    iconMap.get(weather) match
      case Some(path) =>
        val stream = new Image(getClass.getResourceAsStream(path))
        if stream == null then fallbackIcon else new Image(stream)
      case None => fallbackIcon
