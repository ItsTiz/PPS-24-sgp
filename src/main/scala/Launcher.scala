import app.CarSimulatorApp.stage
import controller.ui.UISimulationController
import scalafx.application.JFXApp3
import view.{SimulationView, StartView}

object Launcher extends JFXApp3:

  override def start(): Unit =
    stage = new JFXApp3.PrimaryStage()
    showStartPage()

  private def showStartPage(): Unit =
    StartView.initializeStage(stage, () => launchSimulation())

  private def launchSimulation(): Unit =
    val controller = UISimulationController
    val view = SimulationView(1000, 700, controller.track)
    controller.setDisplay(view)
    view.initializeStage(stage)
    controller.init()
