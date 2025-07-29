package model.simulation.states

import cats.data.State
import model.simulation.states.RaceStateModule.RaceState

/** Module providing core abstractions for managing simulation state.
  *
  * This module defines the fundamental types and operations for working with the race simulation state in a functional
  * manner using the State monad.
  */
object SimulationModule:

  /** Type alias for a State monad that operates on [[RaceState]].
    *
    * This type represents a state transition function that produces a value of type A while potentially modifying the
    * [[RaceState]].
    */
  type Simulation[A] = State[RaceState, A]

  /** Interface for accessing and modifying the simulation state. */
  trait SimulationState:

    /** Retrieves the current race state.
      *
      * @return
      *   a [[Simulation]] that yields the current [[RaceState]]
      */
    def getState: Simulation[RaceState]

    /** Updates the race state with a new state.
      *
      * @param newState
      *   the new [[RaceState]] to set
      * @return
      *   a [[Simulation]] that updates the state and yields Unit
      */
    def setState(newState: RaceState): Simulation[Unit]

  private object SimulationStateImpl extends SimulationState:

    /** @inheritdoc */
    override def getState: Simulation[RaceState] = State.get

    /** @inheritdoc */
    override def setState(newState: RaceState): Simulation[Unit] =
      State.set(newState)

  /** Factory and utility functions for [[SimulationState]]. */
  object SimulationState:
    /** Creates a new [[SimulationState]] instance.
      *
      * @return
      *   a new [[SimulationState]] instance
      */
    def apply(): SimulationState = SimulationStateImpl

    /** Extension methods for the [[Simulation]] type. */
    extension [A](sim: Simulation[A])
      /** Runs a simulation with the given initial state.
        *
        * @param initial
        *   the initial [[RaceState]] to start the simulation with
        * @return
        *   a tuple containing the final state and the result value
        */
      def run(initial: RaceState): (RaceState, A) = sim.run(initial).value
