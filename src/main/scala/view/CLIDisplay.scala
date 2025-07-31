package view

import model.simulation.states.RaceStateModule.RaceState

case class CLIDisplay() extends SimulationDisplay:

  /** @inheritdoc */
  override def update(state: RaceState): Unit =
    println(s"RaceState[T+${state.raceTime}]")
    println("============================================================================================")
    println("[CARS]")
    state.cars.foreach(c => println(c))
    println("[CAR STATES]")
    state.carStates.foreach(c => println(c))
    println("[EVENT QUEUE]")
    state.events.foreach(c => println(c))
    println("[WEATHER]")
    println(state.weather)
    println("============================================================================================")
