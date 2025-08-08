package app

import scalafx.application.JFXApp3
import controller.assembler.SimulationAssembler
import controller.ui.UISimulationController

/** Entry point for the ScalaGP application.
  *
  * This object is responsible for:
  *   - Launching the JavaFX application
  *   - Showing the start screen
  *   - Initializing the simulation controller and view once the user selects a track and starts the race
  */
object ScalaGPSimulator extends JFXApp3:
  import scalafx.stage.Stage
  import view.SimulationView
  import view.simulation.StartView

  /** Holds the currently active UI simulation controller.
    *
    * The controller will be set after the start page is displayed, allowing the simulation to be started after
    * configuration.
    */
  private var controller: Option[UISimulationController] = None

  /** Application entry point called by JavaFX runtime.
    *
    * Initializes the primary stage and shows the start page where the user can configure the simulation before
    * launching it.
    */
  override def start(): Unit =
    stage = new JFXApp3.PrimaryStage()
    showStartPage(stage)

  /** Displays the start page and sets up the listener for when the user starts the simulation.
    *
    * @param stage
    *   The primary JavaFX stage of the application.
    */
  private def showStartPage(stage: Stage): Unit =
    val assembler = SimulationAssembler()
    val uiController = UISimulationController(assembler)

    /** Initializes the start view.
      *
      * @param stage
      *   The application stage.
      * @param listener
      *   The UI simulation controller that will handle configuration events.
      * @param onStart
      *   A callback triggered when the user selects a track type and starts the simulation. Creates and initializes the
      *   simulation view.
      */
    StartView.initializeStage(
      stage,
      uiController,
      trackType =>
        val view = SimulationView(1000, 700, assembler.track, trackType)
        uiController.setDisplay(view)
        view.initializeStage(stage)
    )
    controller = Some(uiController)
