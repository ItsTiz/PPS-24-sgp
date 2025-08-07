package view.scoreboard

/** Represents a row in the scoreboard table, containing the car name and its best lap time (formatted).
  *
  * @param car
  *   The name of the car or driver.
  * @param lapTime
  *   The best lap time in seconds.
  */
class ScoreboardRow(position: Int, car: String, lapTime: Double):
  import scalafx.beans.property.StringProperty
  import view.utils.formatTime

  val positionCar = StringProperty(position.toString)
  val carName = StringProperty(car)
  val formattedBestLap = StringProperty(formatTime(lapTime))

/** A JavaFX-based view for displaying a race scoreboard in tabular format.
  *
  * It shows the car/driver name and their best lap time in a table.
  */
import scalafx.scene.layout.VBox
class ScoreboardView extends VBox:
  import model.simulation.states.RaceStateModule.RaceState
  import scalafx.scene.control.{Alert, TableColumn, TableRow, TableView}
  import model.car.CarModule.Car

  private var currentRaceState: RaceState = _

  /** The table component displaying the scoreboard. */
  private val table = new TableView[ScoreboardRow] {
    columns ++= List(
      new TableColumn[ScoreboardRow, String]("") {
        cellValueFactory = _.value.positionCar
        prefWidth = 30
      },
      new TableColumn[ScoreboardRow, String]("Car") {
        cellValueFactory = _.value.carName
        prefWidth = 100
      },
      new TableColumn[ScoreboardRow, String]("Interval") {
        cellValueFactory = _.value.formattedBestLap
        prefWidth = 80
      }
    )
  }

  // Add the table to the VBox container
  children = Seq(table)

  /** Updates the scoreboard view using data from a [[RaceState]] instance, that includes the updated scoreboard.
    *
    * For each car in the race order, it calculates the interval from the first car (if available) and populates the
    * table rows.
    *
    * @param raceState
    *   The current race state instance from the race logic.
    */

  def updateScoreboard(raceState: RaceState): Unit =
    import scalafx.collections.ObservableBuffer
    import scalafx.scene.input.MouseEvent
    import scalafx.Includes.*
    import view.utils.computeLeaderAndTotalTimes

    currentRaceState = raceState

    val (leaderOpt, leaderTime, totalTimeByCar) = computeLeaderAndTotalTimes(raceState)

    val rows = raceState.scoreboard.raceOrder.zipWithIndex.map: (car, index) =>
      val position = index + 1
      val interval = if car == leaderOpt.get then 0.0 else totalTimeByCar.getOrElse(car, 0.0) - leaderTime
      new ScoreboardRow(position, car.driver.name, interval) -> car

    table.items = ObservableBuffer.from(rows.map(_._1))

    table.rowFactory = _ =>
      new TableRow[ScoreboardRow]:
        onMouseClicked = (event: MouseEvent) =>
          val rowData = item.value
          if event.clickCount == 1 && rowData != null then
            rows.find(_._1.carName.value == rowData.carName.value).foreach:
              case (_, car) => showCarAlert(car)

  /** Displays a popup with the current status of the specified [[Car]].
    *
    * The information includes fuel level, maximum fuel capacity, tire type, and tire degradation, based on the most
    * recent race state.
    *
    * @param car
    *   The car whose information should be displayed.
    */
  def showCarAlert(car: Car): Unit =
    import scalafx.scene.control.Alert.AlertType

    val carStateOpt = (currentRaceState.cars zip currentRaceState.carStates).toMap.get(car)
    carStateOpt.foreach: carState =>
      val fuelLevel = f"${carState.fuelLevel}%.2f"
      val maxFuel = f"${car.maxFuel}%.2f"
      val tireType = carState.tire.tireType.toString
      val tireDegrade = f"${carState.tire.degradeState}%.2f"

      new Alert(AlertType.Information):
        title = s"Car Info - ${car.driver.name}"
        headerText = "Car Status"
        contentText =
          s"""Fuel Level: $fuelLevel / $maxFuel L
             |Tire Type: $tireType
             |Tire Degradation: $tireDegrade %
           """.stripMargin
      .showAndWait()
