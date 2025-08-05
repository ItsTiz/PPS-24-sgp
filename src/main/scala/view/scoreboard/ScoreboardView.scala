package view.scoreboard

import scalafx.scene.control.{TableColumn, TableView}
import scalafx.scene.layout.VBox
import scalafx.beans.property.StringProperty
import model.car.CarModule.Car
import model.race.ScoreboardModule.Scoreboard
import scalafx.collections.ObservableBuffer

class ScoreboardRow(car: String, lapTime: Double):
  val carName = StringProperty(car)
  val formattedBestLap = StringProperty(ScoreboardView.formatTime(lapTime))

object ScoreboardView:
  /** Formats a lap time in seconds as mm:ss.SSS */
  def formatTime(seconds: Double): String =
    val minutes = seconds.toInt / 60
    val remainingSeconds = seconds % 60
    f"$minutes%02d:${remainingSeconds}%06.3f"

class ScoreboardView extends VBox:

  private val table = new TableView[ScoreboardRow] {
    columns ++= List(
      new TableColumn[ScoreboardRow, String]("Car") {
        cellValueFactory = _.value.carName // ✅ no `.delegate` needed
        prefWidth = 150
      },
      new TableColumn[ScoreboardRow, String]("Best Lap") {
        cellValueFactory = _.value.formattedBestLap // ✅ no `.delegate`
        prefWidth = 120
      }
    )
  }

  children = Seq(table)

  /** Update the scoreboard table from a Scoreboard instance. */
  def update(scoreboard: Scoreboard): Unit =
    val rows = scoreboard.raceOrder.map { car =>
      val lapTimes = scoreboard.lapsByCar.getOrElse(car, Nil)
      val bestLap = if lapTimes.nonEmpty then lapTimes.min else 0.0
      new ScoreboardRow(car.driver.name, bestLap)
    }

    table.items = ObservableBuffer.from(rows)
