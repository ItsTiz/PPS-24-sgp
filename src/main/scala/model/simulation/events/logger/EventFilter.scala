package model.simulation.events.logger
import model.simulation.events.EventModule.{CarProgressUpdate, Event, TrackSectorEntered}

/** A filter for deciding which simulation [[Event]]s should be logged.
  *
  * An [[EventFilter]] is a function from [[Event]] to `Boolean`:
  *   - returns `true` - the event should be logged
  *   - returns `false` - the event should be skipped
  */
trait EventFilter extends (Event => Boolean)

object EventFilter:

  /** Logs all events. */
  val allowAll: EventFilter = _ => true

  /** Skips logging for [[CarProgressUpdate]] events. */
  val skipCarProgress: EventFilter =
    case CarProgressUpdate(_, _) => false
    case _ => true

  /** Skips logging for [[TrackSectorEntered]] events. */
  val skipTrackEntered: EventFilter =
    case TrackSectorEntered(_, _, _) => false
    case _ => true

  /** Creates a filter that logs only specific event types.
    *
    * @param eventTypes
    *   a set of allowed event classes
    * @return
    *   a filter that logs only events of the specified types
    */
  def allowOnly(eventTypes: Set[Class[_ <: Event]]): EventFilter =
    e => eventTypes.contains(e.getClass)

  /** Extension methods for combining multiple [[EventFilter]]s. */
  extension (f1: EventFilter)

    /** Combines two filters with logical AND: only logs events accepted by both. */
    def and(f2: EventFilter): EventFilter = e => f1(e) && f2(e)

    /** Combines two filters with logical OR: logs events accepted by either. */
    def or(f2: EventFilter): EventFilter = e => f1(e) || f2(e)

    /** Negates the filter: logs events that would otherwise be skipped. */
    def not: EventFilter = e => !f1(e)
