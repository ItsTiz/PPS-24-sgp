package controller
import model.simulation.states.RaceStateModule.*
import model.simulation.states.StateModule.State
import scalaz.Monad

object SimulationModule:
  import model.simulation.states.StateModule.given

  type Simulation[A] = State[RaceState, A]

  given simulationMonad: Monad[Simulation] = stateMonad[RaceState]

  trait SimulationState:

    def getState: Simulation[RaceState]
    def setState(newState: RaceState): Simulation[Unit]

  private object SimulationStateImpl extends SimulationState:

    // TODO watch this for future change
    extension [A](sim: Simulation[A])
      def run(initial: RaceState): (RaceState, A) = sim match
        case s => s.run(initial)

    override def getState: Simulation[RaceState] =
      State(s => (s, s))

    override def setState(newState: RaceState): Simulation[Unit] =
      State(_ => (newState, ()))

  object SimulationState:
    def apply(): SimulationState = SimulationStateImpl
