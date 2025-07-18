package model.simulation.states

import model.simulation.events.EventModule.Event
import model.simulation.events.EventModule
import scala.collection.immutable.Queue

object RaceStateModule:
  import EventModule.Event
  import model.car.CarModule.Car
  import model.simulation.weather.WeatherModule.Weather

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
      currentRaceTime: Double,
      currentWeather: Weather
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
    def apply(cars: List[Car], weather: Weather): RaceState =
      RaceStateImpl(cars, Queue.empty, 0, weather)

    def withInitialEvents(cars: List[Car], events: List[Event], weather: Weather): RaceState =
      RaceStateImpl(cars, Queue.from(events), 0, weather)

    extension (rs: RaceState)

      /** Returns the list of cars participating in the race.
        *
        * @return
        *   A list of Car objects representing all cars in the race
        */
      def cars: List[Car] = rs match
        case RaceStateImpl(c, _, _, _) => c

      /** Returns the queue of events to be processed in the race.
        *
        * @return
        *   A queue of Event objects waiting to be processed
        */
      def events: Queue[Event] = rs match
        case RaceStateImpl(_, e, _, _) => e

      /** Returns the current time of the race simulation.
        *
        * @return
        *   The current race time as a Double value
        */
      def raceTime: Double = rs match
        case RaceStateImpl(_, _, t, _) => t

      /** Returns the current weather conditions of the race.
        *
        * @return
        *   The current Weather object representing race conditions
        */
      def weather: Weather = rs match
        case RaceStateImpl(_, _, _, w) => w

      /** Adds an event to the race state's event queue.
        *
        * @param e
        *   The event to add to the queue
        * @return
        *   A new RaceState with the event added to the queue
        */
      def enqueueEvent(e: Event): RaceState = rs match
        case RaceStateImpl(c, events, ct, w) => RaceStateImpl(c, events.appended(e), ct, w)

      /** Removes and returns the next event from the race state's event queue.
        *
        * @return
        *   A tuple containing the dequeued event (if any) and the updated RaceState
        */
      def dequeueEvent: (Option[Event], RaceState) = rs match
        case RaceStateImpl(c, e, ct, w) =>
          if e.isEmpty
          then (None, RaceStateImpl(c, Queue.empty, ct, w))
          else
            val (event, queue) = e.dequeue
            (Some(event), RaceStateImpl(c, queue, ct, w))

      /** Finds a car in the race state that matches the given car.
        *
        * @param car
        *   The car to find
        * @return
        *   The found car if it exists, None otherwise
        */
      def findCar(carNumber: Int): Option[Car] = rs match
        case RaceStateImpl(c, e, ct, w) =>
          if c.exists(car => car.carNumber == carNumber) then c.find(car => car.carNumber == carNumber) else None

      /** Updates a car in the race state.
        *
        * @param car
        *   The updated car
        * @return
        *   A new RaceState with the updated car
        */
      def updateCar(car: Car): RaceState = rs match
        case RaceStateImpl(c, e, ct, w) =>
          RaceStateImpl(car :: List.from(c.filter(c => c != car)), e, ct, w)

      /** Updates the weather in the race state.
        *
        * @param newWeather
        *   The new weather
        * @return
        *   A new RaceState with updated weather
        */
      def updateWeather(newWeather: Weather): RaceState = rs match
        case RaceStateImpl(c, e, ct, _) => RaceStateImpl(c, e, ct, newWeather)

      /** Advances the race time.
        *
        * @param deltaTime
        *   Time period to add to current logical time
        * @return
        *   A new RaceState with updated time
        */
      def advanceTime(deltaTime: Double): RaceState = rs match
        case RaceStateImpl(c, q, t, w) =>
          RaceStateImpl(c, q, t + deltaTime, w)
