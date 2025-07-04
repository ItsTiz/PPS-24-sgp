package model.tracks

object TrackSectorModule:
  /** A sector of a racing track. */
  trait TrackSector:
    def maxSpeed: Double
    def avgSpeed: Double
    def gripIndex: Double
    def trackType: TrackSectorType

  /** Type of TrackSector
    */
  enum TrackSectorType:
    case Curve, Straight

  private case class CurveImpl(
      maxSpeed: Double,
      avgSpeed: Double,
      gripIndex: Double,
      curveRadius: Double,
      trackType: TrackSectorType = TrackSectorType.Curve
  ) extends TrackSector

  private case class StraightImpl(
      maxSpeed: Double,
      avgSpeed: Double,
      gripIndex: Double,
      trackType: TrackSectorType = TrackSectorType.Straight
  ) extends TrackSector

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
    def curve(maxSpeed: Double, avgSpeed: Double, gripIndex: Double, radius: Double): TrackSector =
      CurveImpl(maxSpeed, avgSpeed, gripIndex, radius)

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
    def straight(maxSpeed: Double, avgSpeed: Double, gripIndex: Double): TrackSector =
      StraightImpl(maxSpeed, avgSpeed, gripIndex)

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
