package model.simulation.events.logger

/** A generic logging interface for values of type `T` with context `Ctx`.
  *
  * @tparam T
  *   the type of values to be logged
  * @tparam Ctx
  *   the context in which the values are logged
  */
trait Logger[-T, -Ctx]:

  /** Logs a value of type `T` with a given context.
    *
    * @param value
    *   the value to be logged
    * @param context
    *   the logging context
    */
  def log(value: T, context: Ctx): Unit

  /** Logs multiple values of type `T` with a given context.
    *
    * @param values
    *   an iterable collection of values to be logged
    * @param context
    *   the logging context
    */
  def logAll(values: Iterable[T], context: Ctx): Unit

  /** Logs an informational message.
    *
    * @param s
    *   the message to log
    */
  def info(s: String): Unit =
    println(s"[INFO]$s")

  /** Logs a warning message.
    *
    * @param s
    *   the message to log
    */
  def warn(s: String): Unit =
    println(s"[WARN]$s")

  /** Logs an error message.
    *
    * @param s
    *   the message to log
    */
  def error(s: String): Unit =
    println(s"[ERROR]$s")
