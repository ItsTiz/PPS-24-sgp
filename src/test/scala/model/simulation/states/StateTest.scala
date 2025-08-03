package model.simulation.states

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.{equal, should, shouldBe}
import scalaz.Scalaz.ToFunctorOps
import scalaz.syntax.bind.ToBindOps

class StateTest extends AnyFlatSpec:
  import model.simulation.states.StateModule.*

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
