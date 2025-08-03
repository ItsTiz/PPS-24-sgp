import app.CarSimulatorApp.stage
import scalafx.application.JFXApp3
import scalafx.stage.Stage
import view.{SimulationView, StartView}
import controller.UISimulationController

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
