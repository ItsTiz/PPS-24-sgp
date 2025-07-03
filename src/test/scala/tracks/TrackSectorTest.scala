package tracks

import model.tracks.TrackSectorModule.TrackSector
import model.tracks.TrackSectorModule.TrackSector.*
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.must.Matchers.*
import org.scalatest.matchers.should.Matchers.{should, shouldBe}

class TrackSectorTest extends AnyFunSuite:
  test("Track sectors tests:"):
    val curveSector: TrackSector = curve(50, 100, 5, 4)
    radius(curveSector) should equal (Some(4))

    val straightSector: TrackSector = straight(50, 80, 5)
    straightSector.avgSpeed should equal (80)
    radius(straightSector) should equal (None)
