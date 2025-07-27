package view
import model.simulation.states.RaceStateModule.RaceState

trait SimulationDisplay:

  def update(state: RaceState): Unit
