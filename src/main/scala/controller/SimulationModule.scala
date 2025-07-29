package controller

import cats.data.State
import model.simulation.states.RaceStateModule.RaceState

object SimulationModule:

  type Simulation[A] = State[RaceState, A]

  trait SimulationState:

    def getState: Simulation[RaceState]
    def setState(newState: RaceState): Simulation[Unit]

  private object SimulationStateImpl extends SimulationState:

    override def getState: Simulation[RaceState] = State.get

    override def setState(newState: RaceState): Simulation[Unit] =
      State.set(newState)

  object SimulationState:
    def apply(): SimulationState = SimulationStateImpl

    // Extension method to run the State
    extension [A](sim: Simulation[A])
      def run(initial: RaceState): (RaceState, A) = sim.run(initial).value
