package view

import model.simulation.states.RaceStateModule.RaceState

case class CLIDisplay() extends SimulationDisplay:

  override def update(state: RaceState): Unit =
    println(s"RaceState[T+${state.raceTime}]")
    println("=======================")
    println("[CARS]")
    println(state.cars)
    println("[EVENT QUEUE]")
    println(state.events)
    println("[WEATHER]")
    println(state.weather)
    println("=======================")
