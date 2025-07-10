package model.simulation

//import model.shared.Monads.Monad
import scalaz._

object States:

  /** Data structure for state evolution. It has and evolving function S producing result A
    */
  case class State[S, A](run: S => (S, A))

  /** Companion object for State */
  object State:
    extension [S, A](m: State[S, A])

      /** apply function facilitates running the state on an initial 's' */
      def apply(s: S): (S, A) = m match
        case State(run) => run(s)

  /** A given instance that works for all S */
  given stateMonad[S]: Monad[[A] =>> State[S, A]] with

    /** A value wrapped in a state that does not change */
    override def point[A](a: => A): State[S, A] = State(s => (s, a))

    /** Bind function that defines how to get State[S, B] from State[S, A] */
    override def bind[A, B](m: State[S, A])(f: A => State[S, B]): State[S, B] =
      State(s =>
        m(s) match
          case (s2, a) => f(a)(s2)
      )

    /** map function to finally map values in for-comprehensions */
    override def map[A, B](m: State[S, A])(f: A => B): State[S, B] =
      bind(m)(a => point(f(a)))
