package app

import model.simulation.weather.WeatherModule.Weather
import scalafx.application.JFXApp3

object ScalaGPSimulator extends JFXApp3:
  import controller.assembler.SimulationAssembler
  import controller.ui.UISimulationController
  import scalafx.stage.Stage
  import view.{SimulationView, StartView}

  override def start(): Unit =
    stage = new JFXApp3.PrimaryStage()
    showStartPage(stage)

  private def showStartPage(stage: Stage): Unit =
    StartView.initializeStage(stage, (laps, cars, weather) => launchSimulation(laps, cars, weather))

  private def launchSimulation(laps: Int, cars: Int, weather: Weather): Unit =
    val assembler = SimulationAssembler()
    val controller = UISimulationController(assembler)
    val view = SimulationView(1000, 700, assembler.track)
    controller.setDisplay(view)
    view.initializeStage(stage)
    controller.init(cars, laps, weather)
