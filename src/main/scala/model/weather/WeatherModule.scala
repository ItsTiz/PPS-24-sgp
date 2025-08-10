package model.weather

import scala.util.Random

object WeatherModule:

  /** Represents different weather conditions that can occur during a race.
    *
    * Each weather type affects racing conditions differently, particularly in terms of grip and tire wear.
    */
  enum Weather:
    case Sunny, Rainy, Foggy

  /** Companion object for Weather enum that provides extension methods.
    */
  object Weather:
    import WeatherConstants.*

    extension (weather: Weather)
      /** Returns the grip modifier for the current weather condition.
        *
        * @return
        *   a double value representing the grip modifier
        */
      def gripModifier: Double = weather match
        case Sunny => sunnyGrip
        case Rainy => rainyGrip
        case Foggy => foggyGrip

      /** Returns the tire wear modifier for the current weather condition.
        *
        * @return
        *   a double value representing the tire wear modifier
        */
      def tireWearModifier: Double = weather match
        case Sunny => sunnyTireModifier
        case Rainy => rainyTireModifier
        case Foggy => foggyTireModifier

  object WeatherGenerator:

    def getRandomWeather: Weather =
      Weather.values(new Random().nextInt(Weather.values.length))
