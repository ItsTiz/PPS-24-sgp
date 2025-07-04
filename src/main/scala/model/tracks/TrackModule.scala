package model.tracks

import model.tracks.TrackSectorModule.TrackSectorType.*
import model.tracks.TrackSectorModule.*

object TrackModule:

  /** A race track, composed of name and sectors
    */
  trait Track:
    def name: String
    def sectors: List[TrackSector]

  /** Factory and utility functions for tracks. */
  object Track:
    /** Creates a new [[Track]] with the given name and list of sectors.
      *
      * This method enforces basic domain constraints:
      *   - Name must be non-empty
      *   - The list of sectors must not be empty
      *   - The track must contain at least two curves and two straights
      *
      * @param name
      *   the name of the track
      * @param sectors
      *   the list of track sectors that make up the track
      * @return
      *   a new [[Track]] instance
      * @throws IllegalArgumentException
      *   if any validation constraint is violated
      */
    def apply(name: String, sectors: List[TrackSector]): Track = StandardTrack(name, sectors)

    /** Extractor method to deconstruct a [[Track]] into its name and sectors.
      *
      * @param t
      *   the track instance to extract from
      * @return
      *   a tuple containing the name and list of sectors
      */
    def unapply(t: Track): Option[(String, List[TrackSector])] = Some(t.name, t.sectors)

    /** Safely retrieves a track sector by index.
      *
      * @param t
      *   the track to access
      * @param index
      *   the index of the sector to retrieve
      * @return
      *   `Some(sector)` if the index is valid, otherwise `None`
      */
    def getSectorAt(t: Track, index: Int): Option[TrackSector] =
      t.sectors.lift(index)

    private case class StandardTrack(override val name: String, override val sectors: List[TrackSector]) extends Track:
      require(name.nonEmpty, "Track name must not be empty.")
      require(sectors.nonEmpty, "Track must have at least one sector.")
      require(sectors.count(_.trackType == Curve) >= 2, "Track must have a minimum of two curves.")
      require(sectors.count(_.trackType == Straight) >= 2, "Track must have a minimum of two straight lines.")
