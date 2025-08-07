import app.CarSimulatorApp.stage
import controller.assembler.SimulationAssembler
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
    val assembler = SimulationAssembler()
    val controller = UISimulationController(assembler)
    val view = SimulationView(1000, 700, assembler.track)
    controller.setDisplay(view)
    view.initializeStage(stage)
    controller.init()
