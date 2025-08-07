package model.simulation.events.logger

import model.simulation.events.EventModule.*
import model.simulation.events.logger.EventContext.{Processed, Scheduled}

class EventLogger(filter: EventFilter) extends Logger[Event, EventContext]:

  /** @inheritdoc
    */
  override def log(event: Event, context: EventContext): Unit =
    if filter(event) then
      val prefix = context match
        case Scheduled => "[SCHEDULED]"
        case Processed => "[PROCESSED]"
      println(s"$prefix ${describe(event)}")

  private def describe(event: Event): String =
    def heading(t: BigDecimal): String = s"[+T$t]"
    event match
      case TrackSectorEntered(carId, sector, timestamp) => s"${heading(timestamp)} Car#$carId in sector#${sector.id}."
      case PitStopRequest(carId, timestamp) => s"${heading(timestamp)} Pit-stop service for Car#$carId."
      case CarCompletedLap(carId, timestamp) => s"${heading(timestamp)} Car#$carId complete lap."
      case WeatherChanged(weather, timestamp) => s"${heading(timestamp)} Weather change to #$weather."
      case CarProgressUpdate(carId, timestamp) => s"${heading(timestamp)} Car#$carId progress."

  /** @inheritdoc */
  override def logAll(values: Iterable[Event], context: EventContext): Unit =
    values.foreach(event => log(event, context))
