package model.car

/** Driving style of a racing driver. */
enum DrivingStyle:
  case Aggressive, Conservative, Balanced

/** A driver associated to a car. */
trait Driver:
  def name: String
  def style: DrivingStyle

object Driver:
  def apply(name: String, style: DrivingStyle): Driver = DriverImpl(name, style)
  private case class DriverImpl(name: String, style: DrivingStyle) extends Driver
