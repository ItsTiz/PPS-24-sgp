package controller.assembler

import controller.engine.SimulationEngine
import controller.scheduler.SimulationScheduler
import model.simulation.events.logger.{EventContext, EventFilter, EventLogger, Logger}
import model.simulation.events.EventModule.Event
import model.simulation.events.processor.EventProcessor
import model.race.physics.RacePhysicsModule.RacePhysics
import model.simulation.events.scheduler.EventScheduler
import model.tracks.TrackModule.Track
import model.simulation.init.SimulationInitializer
import model.simulation.states.SimulationModule.SimulationState

/** Configuration and dependency injection for simulation components. */
class SimulationAssembler:

  private lazy val simulationInitializer: SimulationInitializer = SimulationInitializer()
  private lazy val simulationEngine: SimulationEngine = SimulationEngine()
  private lazy val simulationScheduler: SimulationScheduler = SimulationScheduler()
  private lazy val filteringOptions: EventFilter = EventFilter.skipCarProgress

  given track: Track = simulationInitializer.track
  private given physics: RacePhysics = RacePhysics()
  private given eventLogger: Logger[Event, EventContext] = EventLogger(filteringOptions)
  private given eventScheduler: EventScheduler = EventScheduler()
  private given eventProcessor: EventProcessor = EventProcessor()
  private given simulationState: SimulationState = SimulationState()

  export simulationInitializer.initSimulationEntities
  export simulationEngine.executeStep
  export simulationState.pure
  export simulationScheduler.startSimulation
