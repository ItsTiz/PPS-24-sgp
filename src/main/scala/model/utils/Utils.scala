package model.utils

/** Converts speed from kilometers per hour (km/h) to meters per second (m/s).
  *
  * @param speed
  *   the speed in kilometers per hour
  * @return
  *   the speed converted to meters per second
  */
def toMetersPerSecond(speed: Double): Double =
  speed / 3.6

/** Computes the inverse ratio of a value with respect to a given maximum.
  *
  * @param value
  *   the input value to be converted
  * @param maxValue
  *   the maximum possible value (must be positive)
  * @return
  *   a normalized inverse ratio between 0.0 and 1.0
  */
def inverseRatio(value: Double, maxValue: Double): Double =
  (maxValue - value) / maxValue
