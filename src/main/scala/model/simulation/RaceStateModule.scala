package model.simulation

import scalaz.Scalaz.ToBooleanOps2FromBoolean

import scala.::
import scala.collection.immutable.Queue

object RaceStateModule:
  import model.car.CarModule.Car
  import EventModule.Event

  opaque type RaceState = RaceStateImpl

  private case class RaceStateImpl(
      cars: List[Car],
      eventQueue: Queue[Event],
      currentRaceTime: Double
      // TODO add current weather here
  ):
    require(cars.nonEmpty)

  object RaceState:

    def apply(cars: List[Car]): RaceState =
      RaceStateImpl(cars, Queue.empty, 0)

    extension (rs: RaceState)
      def enqueueEvent(e: Event): RaceState = rs match
        case RaceStateImpl(cars, events, currentTime) => RaceStateImpl(cars, events.appended(e), currentTime)

      def dequeueEvent: (Option[Event], RaceState) = rs match
        case RaceStateImpl(c, e, ct) =>
          if e.isEmpty
          then (None, RaceStateImpl(c, Queue.empty, ct))
          else
            val (event, queue) = e.dequeue
            (Some(event), RaceStateImpl(c, queue, ct))

      def car(car: Car): Option[Car] = rs match
        case RaceStateImpl(c, e, ct) => if c.contains(car) then c.find(c => c == car) else None

      def updateCar(car: Car): RaceState = rs match
        case RaceStateImpl(c, e, ct) =>
          RaceStateImpl(car :: List.from(c.filter(c => c != car)), e, ct)
