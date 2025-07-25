package model.tracks

object TrackSectorModule:
  /** A sector of a racing track. */
  trait TrackSector:
    def maxSpeed: Double
    def avgSpeed: Double
    def gripIndex: Double
    def sectorType: TrackSectorType
    def sectorLength: Double

    override def toString: String =
      s"""TrackSector(
         |        maxSpeed: $maxSpeed
         |        avgSpeed: $avgSpeed
         |        gripIndex: $gripIndex
         |        sectorType: $sectorType
         |        sectorLength: $sectorLength
         |    )""".stripMargin

  /** Type of TrackSector */
  enum TrackSectorType:
    case Curve, Straight

  // TODO is sectorType necessary? - could it be implemented differently?
  private case class CurveImpl(
      sectorLength: Double,
      maxSpeed: Double,
      avgSpeed: Double,
      gripIndex: Double,
      curveRadius: Double,
      sectorType: TrackSectorType = TrackSectorType.Curve
  ) extends TrackSector:
    validateTrackSector(sectorLength, maxSpeed, avgSpeed, gripIndex)
    require(curveRadius > 0, "Curve radius must be positive.")

  private case class StraightImpl(
      sectorLength: Double,
      maxSpeed: Double,
      avgSpeed: Double,
      gripIndex: Double,
      sectorType: TrackSectorType = TrackSectorType.Straight
  ) extends TrackSector:
    validateTrackSector(sectorLength, maxSpeed, avgSpeed, gripIndex)

  private def validateTrackSector(length: Double, max: Double, avg: Double, grip: Double): Unit =
    require(length > 0, "Sector length must be positive.")
    require(max > 0 && avg > 0, "Speeds must be positive.")
    require(avg < max, "Average speed must be less than max speed.")
    require(grip > 0 && grip <= 1.0, "Grip index must be in the range ]0, 1].")

  /** Factory and utility functions for track sectors. */
  object TrackSector:

    /** Create a curved track sector.
      *
      * @param maxSpeed
      *   the maximum safe speed in this sector - km/H
      * @param avgSpeed
      *   the typical speed vehicles tend to hold - km/H
      * @param gripIndex
      *   the grip level of the road surface - a relative number
      * @param radius
      *   the radius of the curve - in meters
      * @return
      *   a curved track sector instance
      */
    def curve(sectorLength: Double, maxSpeed: Double, avgSpeed: Double, gripIndex: Double, radius: Double): TrackSector =
      CurveImpl(sectorLength, maxSpeed, avgSpeed, gripIndex, radius)

    /** Create a straight track sector.
      *
      * @param maxSpeed
      *   the maximum safe speed in this sector - km/H
      * @param avgSpeed
      *   the typical speed vehicles tend to hold - km/H
      * @param gripIndex
      *   the grip level of the road surface - a relative number
      * @return
      *   a straight track sector instance
      */
    def straight(sectorLength: Double, maxSpeed: Double, avgSpeed: Double, gripIndex: Double): TrackSector =
      StraightImpl(sectorLength, maxSpeed, avgSpeed, gripIndex)

    /** Returns the radius for curve sectors.
      *
      * @param ts
      *   the curve to get the radius from
      * @return
      *   radius optional of the curve if the track sector is one, otherwise None
      */

    def radius(ts: TrackSector): Option[Double] = ts match
      case c: CurveImpl => Some(c.curveRadius)
      case _ => None
