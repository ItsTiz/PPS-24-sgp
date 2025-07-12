package model.simulation.states

import scalaz.*

object StateModule:

  /** Data structure for state evolution. It has and evolving function S producing result A
    * @param run
    *   the function representing the state transformation
    * @tparam S
    *   the type of the state
    * @tparam A
    *   the type of the computation result
    */
  case class State[S, A](run: S => (S, A))

  /** Companion object for State */
  object State:
    extension [S, A](m: State[S, A])

      /** apply function facilitates running the state on an initial 's'
        * @param s
        *   the initial state
        * @return
        *   a tuple containing the new state and the result of the computation
        */
      def apply(s: S): (S, A) = m match
        case State(run) => run(s)

  /** A given instance that works for all S
    * @tparam S
    *   the type of the state threaded through the computation
    */
  given stateMonad[S]: Monad[[A] =>> State[S, A]] with

    /** A value wrapped in a state that does not change
      * @param a
      *   the value to wrap
      * @tparam A
      *   the type of the value
      * @return
      *   a `State` that returns the same state and the given value
      */
    override def point[A](a: => A): State[S, A] = State(s => (s, a))

    /** Bind function that defines how to get State[S, B] from State[S, A] *
      * @param m
      *   the first computation
      * @param f
      *   a function producing the next computation based on the result of `m`
      * @tparam A
      *   the result type of the first computation
      * @tparam B
      *   the result type of the second computation
      * @return
      *   a new `State` representing the composed computation
      */
    override def bind[A, B](m: State[S, A])(f: A => State[S, B]): State[S, B] =
      State(s =>
        m(s) match
          case (s2, a) => f(a)(s2)
      )

    /** map function to finally map values in for-comprehensions
      * @param m
      *   the original computation
      * @param f
      *   the function to apply to the result
      * @tparam A
      *   the original result type
      * @tparam B
      *   the new result type
      * @return
      *   a new `State` with the mapped result
      */
    override def map[A, B](m: State[S, A])(f: A => B): State[S, B] =
      bind(m)(a => point(f(a)))
