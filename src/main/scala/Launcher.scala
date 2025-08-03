import Launcher.stage
import app.CarSimulatorApp.stage
import scalafx.application.JFXApp3
import view.SimulationView
import controller.{SimulationController, UISimulationController}

object Launcher extends JFXApp3:

  override def start(): Unit =
    val controller = UISimulationController
    val view = SimulationView(1000, 700, controller.track)
    controller.setDisplay(view)
    stage = new JFXApp3.PrimaryStage()
    view.initializeStage(stage)
    controller.init()
