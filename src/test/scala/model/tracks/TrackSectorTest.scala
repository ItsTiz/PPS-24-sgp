package model.tracks

import model.tracks.TrackSectorModule.TrackSector
import model.tracks.TrackSectorModule.TrackSector.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers.*
import org.scalatest.matchers.should.Matchers.{should, shouldBe}

class TrackSectorTest extends AnyFlatSpec:
  val curveSector: TrackSector = curve(250, 100, 50, 1, 5)
  val straightSector: TrackSector = straight(500, 300, 250, 1)

  "A Curve" should "have a radius" in:
    radius(curveSector) should equal(Some(5))

  it should "throw IllegalArgumentException if avg speed is more than max speed" in:
    assertThrows[IllegalArgumentException]:
      curve(250, 100, 150, 5, 1)

  "A straight" should "not have a radius" in:
    radius(straightSector) should equal(None)

  it should "throw IllegalArgumentException if avg speed is more than max speed" in:
    assertThrows[IllegalArgumentException]:
      straight(500, 200, 300, 1)
