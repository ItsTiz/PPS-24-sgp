package tracks

import model.tracks.TrackModule.Track
import model.tracks.TrackModule.Track.*
import model.tracks.TrackSectorModule.TrackSector
import model.tracks.TrackSectorModule.TrackSector.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class TrackTest extends AnyFlatSpec with Matchers:
  val validCurve: TrackSector = curve(100, 50, 4, 7)
  val validStraight: TrackSector = straight(320, 200, 4)
  val validCurve2: TrackSector = curve(100, 50, 4, 3)
  val validStraight2: TrackSector = straight(320, 150, 4)
  val validName: String = "Monza"

  val minimalTrackList: List[TrackSector] =
    List(validCurve, validStraight, validCurve2, validStraight2)

  val validTrack: Track = Track(validName, minimalTrackList)

  "A Track" must "not have empty contents" in:
    validTrack.name should not be empty
    validTrack.sectors should not be empty

  it should "have at least 2 curves and straight lines" in:
    noException should be thrownBy (validTrack)

  it should "throw IllegalArgumentException if it has only one straight line" in:
    assertThrows[IllegalArgumentException]:
      Track(validName, List(validCurve, validStraight, validCurve2))

  it should "throw IllegalArgumentException if it has only one curve line" in:
    assertThrows[IllegalArgumentException]:
      Track(validName, List(validStraight, validCurve2, validStraight2))

  it should "return the selected sector correctly" in:
    getSectorAt(validTrack, 2) shouldBe Some(validCurve2)
