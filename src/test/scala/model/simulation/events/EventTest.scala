package model.simulation.events

import model.car.CarModule.Car
import model.car.DriverModule.Driver
import model.car.DrivingStyleModule.DrivingStyle
import model.car.TireModule
import model.car.TireModule.Tire
import model.shared.Coordinate
import model.simulation.events.EventModule.*
import model.simulation.events.EventModule.Event.asString
import model.tracks.TrackSectorModule.TrackSector
import model.tracks.TrackSectorModule.TrackSector.straight
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers.*
import org.scalatest.matchers.should.Matchers.{should, shouldBe}

class EventTest extends AnyFlatSpec:

  val validDriver: Driver = Driver("Test Driver", DrivingStyle.balanced)
  val validCar: Car = Car(
    model = "TestCar",
    carNumber = 16,
    weightKg = 750.0,
    driver = validDriver,
    maxFuel = 100.0
  )
  val validTrackSector: TrackSector = straight(500, 300, 250, 1.0)
  val validEvent: TrackSectorExited = TrackSectorExited(validCar.carNumber, 5.0)

  "An event" should "throw IllegalArgumentException if timestamp is invalid" in:
    assertThrows[IllegalArgumentException]:
      TrackSectorEntered(validCar.carNumber, validTrackSector, -1.0)

  it should "display correctly its timestamp as a string" in:
    val validString: String = "Event[+T5.0]"
    asString(validEvent) should equal(validString)

  "A Track-related event" should "throw IllegalArgumentException if track sector is ill-formed" in:
    assertThrows[IllegalArgumentException]:
      TrackSectorEntered(validCar.carNumber, straight(500, 220, 250, 5), 5.0)
