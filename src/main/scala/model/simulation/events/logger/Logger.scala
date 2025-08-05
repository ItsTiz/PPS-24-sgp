package model.simulation.events.logger

trait Logger[T]:

  /** Logs a value of type `T`.
    *
    * @param toLog
    *   the value to be logged
    */
  def log(toLog: T): Unit
