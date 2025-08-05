package model.tracks

import model.tracks.TrackSectorModule.TrackSectorType.*
import model.tracks.TrackSectorModule.*
import model.tracks.TrackSectorModule.TrackSector.{curve, straight}

object TrackModule:

  /** A race track, composed of name and sectors */
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

    /* TODO indexOf returns first occurrence - if sectors are equal in parameters they are equal as objects. This affects lap counting */
    def nextSector(t: Track)(current: TrackSector): Option[(TrackSector, Boolean)] =
      val i = t.sectors.indexOf(current)
      val willCircleBack = i == t.sectors.length - 1
      if i == -1 then None
      else Some(t.sectors((i + 1) % t.sectors.size), willCircleBack)

    private case class StandardTrack(override val name: String, override val sectors: List[TrackSector]) extends Track:
      require(name.nonEmpty, "Track name must not be empty.")
      require(sectors.nonEmpty, "Track must have at least one sector.")
      require(sectors.count(_.sectorType == Curve) >= 2, "Track must have a minimum of two curves.")
      require(sectors.count(_.sectorType == Straight) >= 2, "Track must have a minimum of two straight lines.")

  object TrackGenerator:
    /** Generates a minimal track layout with a small number of sectors, useful for quick tests and simulations with
      * lower computational load.
      *
      * @param name
      *   Optional name of the track
      * @return
      *   A Track instance with a short layout of straights and curves
      */
    def generateMinimalTrack(name: String = "minimal-track"): Track =
      val sectors: List[TrackSector] =
        List(
          straight(id = 0, sectorLength = 500, maxSpeed = 320, avgSpeed = 300, gripIndex = 1),
          curve(id = 1, sectorLength = 350, maxSpeed = 120, avgSpeed = 115, gripIndex = 1, radius = 8),
          straight(id = 2, sectorLength = 500, maxSpeed = 300, avgSpeed = 280, gripIndex = 1),
          curve(id = 3, sectorLength = 350, maxSpeed = 100, avgSpeed = 95, gripIndex = 1, radius = 7)
        )
      Track(name, sectors)

    /** Generates a longer and more varied track layout, suitable for testing more complex race scenarios.
      *
      * @param name
      *   Optional name of the track
      * @return
      *   A Track instance with more sectors and varied grip/speed parameters
      */
    def generateSimpleTrack(name: String = "simple-track"): Track =
      val sectors: List[TrackSector] =
        List(
          straight(id = 0, sectorLength = 500, maxSpeed = 320, avgSpeed = 300, gripIndex = 1.0),
          curve(id = 1, sectorLength = 220, maxSpeed = 140, avgSpeed = 130, gripIndex = 0.8, radius = 10),
          straight(id = 2, sectorLength = 370, maxSpeed = 330, avgSpeed = 300, gripIndex = 1.0),
          curve(id = 3, sectorLength = 200, maxSpeed = 160, avgSpeed = 140, gripIndex = 0.8, radius = 10),
          straight(id = 4, sectorLength = 500, maxSpeed = 310, avgSpeed = 300, gripIndex = 1.0),
          curve(id = 5, sectorLength = 200, maxSpeed = 150, avgSpeed = 140, gripIndex = 0.8, radius = 10),
          straight(id = 6, sectorLength = 350, maxSpeed = 220, avgSpeed = 210, gripIndex = 0.95),
          curve(id = 7, sectorLength = 200, maxSpeed = 130, avgSpeed = 120, gripIndex = 0.7, radius = 10)
        )
      Track(name, sectors)

    /** Generates a more challenging track layout, with tighter curves, lower grip, and a mix of fast and technical
      * sections.
      *
      * @param name
      *   Optional name of the track
      * @return
      *   A Track instance representing a harder layout
      */
    def generateChallengingTrack(name: String = "challenging-track"): Track =
      val sectors: List[TrackSector] =
        List(
          straight(id = 0, sectorLength = 600, maxSpeed = 320, avgSpeed = 300, gripIndex = 1.0),
          curve(id = 1, sectorLength = 180, maxSpeed = 100, avgSpeed = 90, gripIndex = 0.65, radius = 6),
          straight(id = 2, sectorLength = 400, maxSpeed = 290, avgSpeed = 270, gripIndex = 0.95),
          curve(id = 3, sectorLength = 160, maxSpeed = 90, avgSpeed = 80, gripIndex = 0.6, radius = 5),
          straight(id = 4, sectorLength = 300, maxSpeed = 250, avgSpeed = 230, gripIndex = 0.9),
          curve(id = 5, sectorLength = 240, maxSpeed = 120, avgSpeed = 110, gripIndex = 0.75, radius = 9),
          straight(id = 6, sectorLength = 450, maxSpeed = 310, avgSpeed = 290, gripIndex = 1.0),
          curve(id = 7, sectorLength = 200, maxSpeed = 110, avgSpeed = 100, gripIndex = 0.7, radius = 8),
          straight(id = 8, sectorLength = 380, maxSpeed = 270, avgSpeed = 250, gripIndex = 0.9),
          curve(id = 9, sectorLength = 150, maxSpeed = 80, avgSpeed = 70, gripIndex = 0.6, radius = 5)
        )
      Track(name, sectors)
