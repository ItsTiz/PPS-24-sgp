package model.simulation.events

import model.car.CarModule.Car
import model.driver.DriverModule.Driver
import model.driver.DrivingStyleModule.DrivingStyle
import model.simulation.events.EventModule.*
import model.simulation.weather.WeatherModule.*
import model.tracks.TrackSectorModule.TrackSector
import model.tracks.TrackSectorModule.TrackSector.straight
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class EventTest extends AnyFlatSpec with Matchers:

  val validDriver: Driver = Driver("Test Driver", DrivingStyle.balanced)
  val validCar: Car = Car(
    model = "TestCar",
    carNumber = 16,
    weightKg = 750.0,
    driver = validDriver,
    maxFuel = 100.0
  )
  val validTrackSector: TrackSector = straight(0, 500, 300, 250, 1.0)

  "TrackSectorEntered" should "throw IllegalArgumentException for negative timestamp" in:
    assertThrows[IllegalArgumentException]:
      TrackSectorEntered(validCar.carNumber, validTrackSector, -1.0)

  it should "store correct values and format string properly" in:
    val event = TrackSectorEntered(validCar.carNumber, validTrackSector, 5.0)
    event.timestamp shouldBe 5.0
    event.carId shouldBe validCar.carNumber
    event.asString shouldBe "Event[+T5.0]"

  "PitStopRequest" should "throw IllegalArgumentException for negative timestamp" in:
    assertThrows[IllegalArgumentException]:
      PitStopRequest(validCar.carNumber, -1.0)

  it should "store correct values and format string properly" in:
    val event = PitStopRequest(validCar.carNumber, 10.0)
    event.timestamp shouldBe 10.0
    event.carId shouldBe validCar.carNumber
    event.asString shouldBe "Event[+T10.0]"

  "CarProgressUpdate" should "throw IllegalArgumentException for negative timestamp" in:
    assertThrows[IllegalArgumentException]:
      CarProgressUpdate(validCar.carNumber, -0.5)

  it should "store correct values and format string properly" in:
    val event = CarProgressUpdate(validCar.carNumber, 8.0)
    event.timestamp shouldBe 8.0
    event.carId shouldBe validCar.carNumber
    event.asString shouldBe "Event[+T8.0]"

  "CarCompletedLap" should "throw IllegalArgumentException for negative timestamp" in:
    assertThrows[IllegalArgumentException]:
      CarCompletedLap(validCar.carNumber, -0.01)

  it should "store correct values and format string properly" in:
    val event = CarCompletedLap(validCar.carNumber, 12.5)
    event.timestamp shouldBe 12.5
    event.carId shouldBe validCar.carNumber
    event.asString shouldBe "Event[+T12.5]"

  "WeatherChanged" should "throw IllegalArgumentException for negative timestamp" in:
    assertThrows[IllegalArgumentException]:
      WeatherChanged(Weather.Rainy, -3.0)

  it should "store correct values and format string properly" in:
    val event = WeatherChanged(Weather.Sunny, 15.0)
    event.timestamp shouldBe 15.0
    event.weather shouldBe Weather.Sunny
    event.asString shouldBe "Event[+T15.0]"

  "A Track-related event" should "throw IllegalArgumentException if track sector is ill-formed" in:
    assertThrows[IllegalArgumentException]:
      TrackSectorEntered(validCar.carNumber, straight(0, 500, 220, 250, 5), 5.0)
