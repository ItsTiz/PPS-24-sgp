package model.simulation.events.processor

import model.race.physics.RacePhysicsModule.RacePhysics
import model.simulation.events.EventModule.Event
import model.simulation.events.logger.{EventContext, Logger}
import model.simulation.events.scheduler.EventScheduler
import model.simulation.states.RaceStateModule.RaceState
import model.tracks.TrackModule.Track

trait EventProcessor:
  /** Processes a single event and updates the race state accordingly.
    *
    * @param state
    *   the current state of the race
    * @param event
    *   the event to be processed
    * @return
    *   an updated race state after processing the event
    */
  def processEvent(state: RaceState)(event: Event): RaceState

/** Factory for creating [[EventProcessor]] instances. */
object EventProcessor:
  /** Creates a new EventProcessor instance.
    *
    * @param physics
    *   the physics engine used for race calculations
    * @param track
    *   the track used for the race
    * @return
    *   a new EventProcessor instance
    */
  def apply()(using physics: RacePhysics, Scheduler: EventScheduler, Logger: Logger[Event, EventContext])
      : EventProcessor =
    new EventProcessorImpl
