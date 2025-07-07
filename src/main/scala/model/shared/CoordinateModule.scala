package model.shared

/** A 2D coordinate representing a point on the track. */
trait Coordinate:

  def x: Double
  def y: Double

  /** Calculates the Euclidean distance to another coordinate.
    *
    * @param other
    *   the other coordinate to compute the distance to
    * @return
    *   the Euclidean distance between this and the other coordinate
    */
  def distanceTo(other: Coordinate): Double =
    math.hypot(x - other.x, y - other.y)

  /** Returns a new coordinate moved by the given offset.
    *
    * @param dx
    *   the offset to apply on the X-axis
    * @param dy
    *   the offset to apply on the Y-axis
    * @return
    *   a new [[Coordinate]] instance with the updated position
    */
  def moveBy(dx: Double, dy: Double): Coordinate

/** Factory and utilities for [[Coordinate]] instances. */
object Coordinate:

  /** Creates a new [[Coordinate]] with the given x and y values.
    *
    * @param x
    *   the X coordinate
    * @param y
    *   the Y coordinate
    * @return
    *   a new [[Coordinate]] instance
    */
  def apply(x: Double, y: Double): Coordinate =
    CoordinateImpl(x, y)

  /** Extractor method for [[Coordinate]].
    *
    * @param c
    *   the coordinate to extract
    * @return
    *   a tuple of (x, y)
    */
  def unapply(c: Coordinate): Option[(Double, Double)] =
    Some((c.x, c.y))

  /** Private implementation of the [[Coordinate]] trait. */
  private case class CoordinateImpl(x: Double, y: Double) extends Coordinate:

    /** @inheritdoc */
    override def moveBy(dx: Double, dy: Double): Coordinate =
      copy(x = x + dx, y = y + dy)
