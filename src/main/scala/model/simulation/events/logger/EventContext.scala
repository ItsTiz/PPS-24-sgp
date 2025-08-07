package model.simulation.events.logger

/** Describes the context in which an [[Event]] is being logged.
  *
  * Used to distinguish between events that are scheduled for future execution versus events that have already been
  * processed during the simulation.
  */
enum EventContext:
  case Scheduled, Processed
