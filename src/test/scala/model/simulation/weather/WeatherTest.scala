package model.simulation.weather

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.{be, equal, should, contain}
import model.simulation.weather.WeatherModule.Weather
import model.simulation.weather.WeatherConstants.*

class WeatherTest extends AnyFlatSpec:

  "A weather type" should "have three different conditions: Sunny, Rainy, and Foggy" in:
    Weather.values.length should be(3)
    Weather.values should contain(Weather.Sunny)
    Weather.values should contain(Weather.Rainy)
    Weather.values should contain(Weather.Foggy)

  it should "provide different grip modifiers for each weather condition" in:
    Weather.Sunny.gripModifier should equal(1.0)
    Weather.Rainy.gripModifier should equal(0.95)
    Weather.Foggy.gripModifier should equal(0.97)

  it should "provide different tire wear modifiers for each weather condition" in:
    Weather.Sunny.tireWearModifier should equal(1.0)
    Weather.Rainy.tireWearModifier should equal(1.1)
    Weather.Foggy.tireWearModifier should equal(1.05)

  it should "have Sunny as the default optimal condition with highest grip" in:
    val allWeatherTypes = Weather.values
    val highestGrip = allWeatherTypes.map(_.gripModifier).max

    Weather.Sunny.gripModifier should equal(highestGrip)
    Weather.Sunny.gripModifier should be > Weather.Rainy.gripModifier
    Weather.Sunny.gripModifier should be > Weather.Foggy.gripModifier

  it should "have Rainy as the most challenging condition with lowest grip" in:
    val allWeatherTypes = Weather.values
    val lowestGrip = allWeatherTypes.map(_.gripModifier).min

    Weather.Rainy.gripModifier should equal(lowestGrip)
    Weather.Rainy.gripModifier should be < Weather.Sunny.gripModifier
    Weather.Rainy.gripModifier should be < Weather.Foggy.gripModifier
