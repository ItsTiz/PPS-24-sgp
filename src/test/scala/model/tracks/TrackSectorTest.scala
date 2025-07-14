package model.tracks

import model.tracks.TrackSectorModule.TrackSector
import model.tracks.TrackSectorModule.TrackSector.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers.*
import org.scalatest.matchers.should.Matchers.{should, shouldBe}

class TrackSectorTest extends AnyFlatSpec:
  val curveSector: TrackSector = curve(100, 50, 5, 4)
  val straightSector: TrackSector = straight(300, 250, 5)

  "A Curve" should "have a radius" in:
    radius(curveSector) should equal(Some(4))

  it should "throw IllegalArgumentException if avg speed is more than max speed" in:
    assertThrows[IllegalArgumentException]:
      curve(100, 150, 5, 4)

  "A straight" should "not have a radius" in:
    radius(straightSector) should equal(None)

  it should "throw IllegalArgumentException if avg speed is more than max speed" in:
    assertThrows[IllegalArgumentException]:
      straight(200, 300, 5)
