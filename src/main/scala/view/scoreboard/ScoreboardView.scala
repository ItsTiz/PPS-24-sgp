package view.scoreboard

import scalafx.scene.control.{Alert, TableColumn, TableRow, TableView}
import scalafx.scene.layout.VBox
import scalafx.beans.property.StringProperty
import model.car.CarModule.Car
import model.race.ScoreboardModule.Scoreboard
import model.simulation.states.RaceStateModule.RaceState
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.input.MouseEvent
import scalafx.Includes.*
import scalafx.scene.transform.MatrixType.MT_2D_2x3.rows

/** Represents a row in the scoreboard table, containing the car name and its best lap time (formatted).
  *
  * @param car
  *   The name of the car or driver.
  * @param lapTime
  *   The best lap time in seconds.
  */
class ScoreboardRow(car: String, lapTime: Double):
  val carName = StringProperty(car)
  val formattedBestLap = StringProperty(ScoreboardView.formatTime(lapTime))

/** Companion object for [[ScoreboardView]], providing utility methods. */
object ScoreboardView:

  /** Formats a lap time in milliseconds as mm:ss.SSS.
    *
    * @param milliseconds
    *   The time in milliseconds.
    * @return
    *   A string formatted as minutes:seconds.milliseconds (e.g. "01:23.456")
    */
  def formatTime(milliseconds: Double): String =
    val totalSeconds = milliseconds / 1000.0
    val minutes = totalSeconds.toInt / 60
    val remainingSeconds = totalSeconds % 60
    f"$minutes%02d:${remainingSeconds}%06.3f"

/** A JavaFX-based view for displaying a race scoreboard in tabular format.
  *
  * It shows the car/driver name and their best lap time in a table.
  */
class ScoreboardView extends VBox:
  private var currentRaceState: RaceState = _

  /** The table component displaying the scoreboard. */
  private val table = new TableView[ScoreboardRow] {
    columns ++= List(
      new TableColumn[ScoreboardRow, String]("Car") {
        cellValueFactory = _.value.carName
        prefWidth = 150
      },
      new TableColumn[ScoreboardRow, String]("Interval") {
        cellValueFactory = _.value.formattedBestLap
        prefWidth = 120
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
    currentRaceState = raceState

    val leader = raceState.scoreboard.raceOrder.headOption
    val totalTimeByCar = raceState.scoreboard.lapsByCar.view.mapValues(_.sum).toMap
    val leaderTime = leader.flatMap(totalTimeByCar.get).getOrElse(0.0)

    val rows = raceState.scoreboard.raceOrder.map: car =>
      val interval = if car == leader.get then 0.0 else totalTimeByCar.getOrElse(car, 0.0) - leaderTime
      new ScoreboardRow(car.driver.name, interval) -> car

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
