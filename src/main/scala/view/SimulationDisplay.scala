package view

import model.simulation.states.RaceStateModule.RaceState

/** Trait representing a generic display component for the race simulation. Implementations of this trait define how the
  * race state should be visually or textually presented to the user.
  */
trait SimulationDisplay:

  /** Updates the display based on the current race simulation state.
    *
    * @param state
    *   The current state of the race simulation to be rendered or shown
    */
  def update(state: RaceState): Unit
