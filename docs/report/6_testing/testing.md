---
title: Testing
nav_order: 6
parent: Report
---

# Testing

## Test-Driven Development (TDD)
For software development, we adopted the Test-Driven Development (TDD) approach specifically for the **model** layer. Each feature was implemented only after creating a test that demonstrated its expected behavior. 

## ScalaTest
To test the model, we used the ScalaTest library, with team members adopting different styles: Ines used FunSuite, while Tiziano preferred AnyFlatSpec combined with Matchers to improve test readability. An example test case is shown below:
```scala
// FunSuit
test("Car basic properties should match values") {
  assert(car.model == "TestCar")
  assert(car.carNumber == 10)
  assert(car.weightKg == 750.0)
  assert(car.driver == driver)
  assert(car.maxFuel == 100.0)

}

test("Driver should hold name and driving style") {
  val aggressiveDriver = Driver("Speedy", DrivingStyle.aggressive)
  assert(aggressiveDriver.name == "Speedy")
  assert(aggressiveDriver.style == DrivingStyle.aggressive)
}

// AnyFlatSpec
"A RaceState" should "not have empty cars list" in:
  assertThrows[IllegalArgumentException]:
    RaceState(Map.empty, Sunny, 3)

it should "return correct values for basic state properties" in:
  validRaceState.raceTime should equal(0)
  validRaceState.weather should equal(Sunny)
  validRaceState.laps should equal(3)
  validRaceState.cars should contain theSameElementsAs cars
  validRaceState.carStates.map(_.currentSpeed) should contain only 0.0
  validRaceState.scoreboard should not be null

```

## Scoverage
To measure test coverage, we employed Scoverage, a tool integrated with SBT. We focused exclusively on coverage results from the model. Our test coverage for the model stands at 60%.
The overall code line coverage for the entire project is 25,41%. However, it should be noted that the view package, which contains graphical elements, as well as the controller packages, do not have any tests.