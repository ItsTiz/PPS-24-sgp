package model.simulation

import scala.collection.immutable.Queue

object RaceStateModule:
  import model.car.CarModule.Car
  import EventModule.Event

  /** Represents the state of a race at a specific point in time. This is an opaque type that encapsulates the
    * implementation details.
    */
  opaque type RaceState = RaceStateImpl

  /** Implementation of the RaceState type.
    *
    * @param cars
    *   List of cars participating in the race
    * @param eventQueue
    *   Queue of events to be processed
    * @param currentRaceTime
    *   Current time of the race simulation
    */
  private case class RaceStateImpl(
      cars: List[Car],
      eventQueue: Queue[Event],
      currentRaceTime: Double
      // TODO add current weather here
  ):
    require(cars.nonEmpty)

  /** Companion object for RaceState providing factory methods and extensions */
  object RaceState:

    /** Creates a new RaceState with the given cars and empty event queue.
      *
      * @param cars
      *   List of cars participating in the race
      * @return
      *   A new RaceState instance
      */
    def apply(cars: List[Car]): RaceState =
      RaceStateImpl(cars, Queue.empty, 0)

    extension (rs: RaceState)
      /** Adds an event to the race state's event queue.
        *
        * @param e
        *   The event to add to the queue
        * @return
        *   A new RaceState with the event added to the queue
        */
      def enqueueEvent(e: Event): RaceState = rs match
        case RaceStateImpl(cars, events, currentTime) => RaceStateImpl(cars, events.appended(e), currentTime)

      /** Removes and returns the next event from the race state's event queue.
        *
        * @return
        *   A tuple containing the dequeued event (if any) and the updated RaceState
        */
      def dequeueEvent: (Option[Event], RaceState) = rs match
        case RaceStateImpl(c, e, ct) =>
          if e.isEmpty
          then (None, RaceStateImpl(c, Queue.empty, ct))
          else
            val (event, queue) = e.dequeue
            (Some(event), RaceStateImpl(c, queue, ct))

      /** Finds a car in the race state that matches the given car.
        *
        * @param car
        *   The car to find
        * @return
        *   The found car if it exists, None otherwise
        */
      def car(car: Car): Option[Car] = rs match
        case RaceStateImpl(c, e, ct) => if c.contains(car) then c.find(c => c == car) else None

      /** Updates a car in the race state.
        *
        * @param car
        *   The updated car
        * @return
        *   A new RaceState with the updated car
        */
      def updateCar(car: Car): RaceState = rs match
        case RaceStateImpl(c, e, ct) =>
          RaceStateImpl(car :: List.from(c.filter(c => c != car)), e, ct)
