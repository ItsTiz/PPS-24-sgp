package model.shared

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.must.Matchers.*
import org.scalatest.matchers.should.Matchers.shouldBe

class CoordinateTest extends AnyFunSuite:

  test("Coordinate should return correct x and y") {
    val coord = Coordinate(10.5, 20.0)
    coord.x shouldBe 10.5
    coord.y shouldBe 20.0
  }

  test("distanceTo should return correct Euclidean distance") {
    val a = Coordinate(0, 0)
    val b = Coordinate(3, 4)
    a.distanceTo(b) shouldBe 5.0
    b.distanceTo(a) shouldBe 5.0
  }

  test("moveBy should return new Coordinate with correct offsets") {
    val coord = Coordinate(2.0, 3.0)
    val moved = coord.moveBy(1.5, -1.0)
    moved.x shouldBe 3.5
    moved.y shouldBe 2.0

    // original must remain unchanged (immutability)
    coord.x shouldBe 2.0
    coord.y shouldBe 3.0
  }
