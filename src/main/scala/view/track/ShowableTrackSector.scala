package view.track

import model.tracks.TrackSectorModule.TrackSector
import model.shared.CoordinateModule.Coordinate
import model.tracks.TrackModule.Track

/** A wrapper that makes a [[TrackSector]] drawable on screen. It stores its spatial coordinates and a flag for the
  * starting sector.
  *
  * @tparam A
  *   The underlying data type to show (e.g., a TrackSector).
  */
trait Showable[A]:
  def sector: A
  def start: Coordinate
  def end: Coordinate

/** Represents a drawable track sector, including:
  *   - The track sector data
  *   - The starting and ending coordinates
  *   - A flag indicating if it's the first sector
  *   - A flag for inverting the curve orientation (used for drawing arcs)
  *
  * @param sector
  *   The underlying [[TrackSector]].
  * @param start
  *   The starting coordinate of the sector.
  * @param end
  *   The ending coordinate of the sector.
  * @param isStart
  *   True if this is the first sector in the track.
  * @param invert
  *   True if the curve arc should be drawn in inverted direction.
  */
case class ShowableTrackSector(
    sector: TrackSector,
    start: Coordinate,
    end: Coordinate,
    isStart: Boolean = false,
    invert: Boolean = false
) extends Showable[TrackSector]

/** Object responsible for generating drawable track sectors (ShowableTrackSector) from abstract logical definitions.
  */
object ShowableTrackGenerator:

  /** Generates a basic rectangular closed track using straight and curved sectors. Each curve has a fixed corner
    * radius, and all geometry is calculated to maintain visual continuity.
    *
    * @param startX
    *   The X-coordinate of the top-left corner of the rectangle.
    * @param startY
    *   The Y-coordinate of the top-left corner of the rectangle.
    * @param width
    *   The total width of the rectangle.
    * @param height
    *   The total height of the rectangle.
    * @return
    *   A list of connected [[ShowableTrackSector]] forming a closed loop.
    */
  def generateRectangular(track: Track, startX: Double = 300, startY: Double = 200,
      width: Double = 550, height: Double = 300): List[ShowableTrackSector] =
    val cornerRadius = 100

    // TODO make coordinates compatible with track
    val coordinates = List(
      (Coordinate(startX + cornerRadius, startY), Coordinate(startX + width - cornerRadius, startY)), // Top straight
      (Coordinate(startX + width - cornerRadius, startY),
        Coordinate(startX + width, startY + cornerRadius)), // Top-right corner
      (Coordinate(startX + width, startY + cornerRadius),
        Coordinate(startX + width, startY + height - cornerRadius)), // Right straight
      (Coordinate(startX + width, startY + height - cornerRadius),
        Coordinate(startX + width - cornerRadius, startY + height)), // Bottom-right corner
      (Coordinate(startX + width - cornerRadius, startY + height),
        Coordinate(startX + cornerRadius, startY + height)), // Bottom straight
      (Coordinate(startX + cornerRadius, startY + height),
        Coordinate(startX, startY + height - cornerRadius)), // Bottom-left corner
      (Coordinate(startX, startY + height - cornerRadius), Coordinate(startX, startY + cornerRadius)), // Left straight
      (Coordinate(startX, startY + cornerRadius), Coordinate(startX + cornerRadius, startY)) // Top-left corner
    )

    (track.sectors zip coordinates).zipWithIndex.map { case ((sector, (start, end)), idx) =>
      ShowableTrackSector(
        sector = sector,
        start = start,
        end = end,
        isStart = idx == 0,
        invert = idx == 3 || idx == 7 // Invert bottom-right and top-left curves for correct arc direction
      )
    }
