package controller

import model.car.CarModule.Car
import model.simulation.events.EventModule
import model.simulation.events.EventModule.*
import model.simulation.states.RaceStateModule.RaceState

trait EventProcessor:
  def processEvent(state: RaceState)(event: Event): RaceState

  object EventProcessor:
    def apply(): EventProcessor = EventProcessorImpl

private object EventProcessorImpl extends EventProcessor:

  override def processEvent(state: RaceState)(event: Event): RaceState = ???
  def scheduleNextEvents(car: Car): List[Event] = ???
