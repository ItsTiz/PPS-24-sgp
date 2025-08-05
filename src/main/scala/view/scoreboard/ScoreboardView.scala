package view.scoreboard

import scalafx.scene.control.{TableColumn, TableView}
import scalafx.scene.layout.VBox
import scalafx.beans.property.StringProperty
import model.car.CarModule.Car
import model.race.ScoreboardModule.Scoreboard
import scalafx.collections.ObservableBuffer

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
   * The time in milliseconds.
   * @return
   * A string formatted as minutes:seconds.milliseconds (e.g. "01:23.456")
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

  /** Updates the scoreboard view using data from a [[Scoreboard]] instance.
   *
   * For each car in the race order, it calculates the interval from the first car (if available)
   * and populates the table rows.
   *
   * @param scoreboard
   *   The current scoreboard instance from the race logic.
   */
  def update(scoreboard: Scoreboard): Unit =
    val leader = scoreboard.raceOrder.headOption
    val totalTimeByCar: Map[Car, Double] =
      scoreboard.lapsByCar.view.mapValues(_.sum).toMap

    val leaderTime = leader.flatMap(totalTimeByCar.get).getOrElse(0.0)

    val rows = scoreboard.raceOrder.map { car =>
      val carTime = totalTimeByCar.getOrElse(car, 0.0)
      val interval = if (car == leader.get) 0.0 else carTime - leaderTime
      new ScoreboardRow(car.driver.name, interval)
    }

    table.items = ObservableBuffer.from(rows)

