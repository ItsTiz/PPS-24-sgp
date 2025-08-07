package view.scoreboard

/** Represents a single row in the final scoreboard table.
  *
  * @param position
  *   the final ranking position of the car
  * @param carModel
  *   the model of the car
  * @param driverName
  *   the name of the driver
  * @param interval
  *   the time difference from the leader, in milliseconds
  * @param bestLap
  *   the best lap time of the car, in milliseconds
  * @param laps
  *   a sequence of lap times for the car, in milliseconds
  */
class FinalScoreboardRow(
    val position: Int,
    val carModel: String,
    val driverName: String,
    val interval: Double,
    val bestLap: Double,
    val laps: Seq[Double]
):
  import scalafx.beans.property.*
  import view.utils.formatTime

  /** JavaFX property for the position column */
  val positionProperty: StringProperty = StringProperty(position.toString)

  /** JavaFX property for the car model column */
  val carModelProperty: StringProperty = StringProperty(carModel)

  /** JavaFX property for the driver name column */
  val driverNameProperty: StringProperty = StringProperty(driverName)

  /** JavaFX property for the interval from leader column */
  val intervalProperty: StringProperty = StringProperty(formatTime(interval))

  /** JavaFX property for the best lap time, formatted as mm:ss.sss */
  val bestLapProperty: StringProperty = StringProperty(formatTime(bestLap))

  /** JavaFX property for the sequence of lap times, formatted and joined by " - " */
  val lapsProperty: StringProperty = StringProperty(laps.map(formatTime).mkString(" - "))

/** Companion object containing logic for rendering the final scoreboard view */
object FinalScoreboardView:
  import model.simulation.states.RaceStateModule.RaceState
  import scalafx.scene.layout.VBox

  /** Creates a visual VBox containing a table view representing the final scoreboard.
    *
    * The table includes:
    *   - Position (based on race order)
    *   - Car model
    *   - Driver name
    *   - Interval from the leader
    *   - Best lap time
    *   - All lap times
    *
    * @param raceState
    *   the final state of the race
    * @return
    *   a VBox containing the final scoreboard table
    */
  def finalScoreboardView(raceState: RaceState): VBox =
    import scalafx.scene.control.*
    import scalafx.collections.ObservableBuffer
    import scalafx.geometry.Insets
    import view.utils.computeLeaderAndTotalTimes

    val (leaderOpt, leaderTime, totalTimeByCar) = computeLeaderAndTotalTimes(raceState)

    val rows = raceState.scoreboard.raceOrder.zipWithIndex.map { case (car, index) =>
      val position = index + 1
      val laps = raceState.scoreboard.lapsByCar.getOrElse(car, Seq.empty)
      val bestLap = if laps.nonEmpty then laps.min else 0.0
      val interval = if car == leaderOpt.get then 0.0 else totalTimeByCar.getOrElse(car, 0.0) - leaderTime
      new FinalScoreboardRow(position, car.model, car.driver.name, interval, bestLap, laps)
    }

    val table = new TableView[FinalScoreboardRow]:
      columns ++= List(
        new TableColumn[FinalScoreboardRow, String]("Position") {
          cellValueFactory = _.value.positionProperty
          prefWidth = 80
        },
        new TableColumn[FinalScoreboardRow, String]("Car Model") {
          cellValueFactory = _.value.carModelProperty
          prefWidth = 150
        },
        new TableColumn[FinalScoreboardRow, String]("Driver Name") {
          cellValueFactory = _.value.driverNameProperty
          prefWidth = 150
        },
        new TableColumn[FinalScoreboardRow, String]("Interval") {
          cellValueFactory = _.value.intervalProperty
          prefWidth = 80
        },
        new TableColumn[FinalScoreboardRow, String]("Best Lap") {
          cellValueFactory = _.value.bestLapProperty
          prefWidth = 80
        },
        new TableColumn[FinalScoreboardRow, String]("Laps") {
          cellValueFactory = _.value.lapsProperty
          prefWidth = 300
        }
      )
      items = ObservableBuffer.from(rows)

    new VBox:
      padding = Insets(10)
      children = List(table)
