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

def normalize(value: Double, maxValue: Double): Double =
  (maxValue - value) / maxValue
