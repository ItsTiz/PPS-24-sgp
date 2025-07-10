package model.simulation

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.{equal, should, shouldBe}
import scalaz.syntax.bind.ToBindOps
import scalaz.Scalaz.ToFunctorOps

class StateTest extends AnyFlatSpec:
  import States.*

  val simpleCounter: State[Int, Unit] = State(i => (i + 1, ()))

  "A simple counter implemented via State" should "increment the state by 1" in:
    val (state, _) = simpleCounter(0)
    state shouldBe 1

  "Chained counters" should "increment state cumulatively" in:
    val result: State[Int, Unit] =
      for
        x <- simpleCounter
        x <- simpleCounter
        x <- simpleCounter
      yield x

    val (tuple, _) = result(0)
    tuple should equal(3)
