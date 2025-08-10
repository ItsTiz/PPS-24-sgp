---
title: Model
nav_order: 1
parent: Detailed Design
---

## Model Structure

The `model` package contains the **core logic** of the simulation, including the entities, constants, and rules that
define cars, drivers, tracks, races, and supporting utilities. It is organized into subpackages according to domain
concepts:

## Model Package Structure

The **`model`** package is the core of the simulation and is organized into domain-specific subpackages, each
encapsulating a distinct part of the racing world:

- `Car`:
  Represents the vehicles in the simulation, including their physical properties and performance constraints such as
  fuel capacity, tire wear, and grip. It defines constants, car management logic, and tire-specific behavior. For more
  details see [Car](../model/fraccalvieri/ines.md#car)

- `Driver`:
  Models the human drivers, including personal attributes and driving styles. This includes both the static definitions
  of possible styles and the logic for applying them to race performance. For more
  details see [Driver](../model/fraccalvieri/ines.md#driver)

- `Tracks`:
  Describes racing circuits and their composition in terms of sectors. Each track contains structural data like sector
  length, curvature, speed limits, and grip conditions, as well as tools for generating track layouts. For more
  details see [Car](../model/fraccalvieri/ines.md#car)

- `Race`:
  Encapsulates race-specific entities and logic, including the race physics and the scoreboard with the state of race. For more
  details see [Car](../model/fraccalvieri/ines.md#car)

- `Simulation`:
  The core model entities of the simulation, those which are responsible to represent dynamically the domain in function
  of time. For more
  details see [Car](../model/fraccalvieri/ines.md#car)

- `Weather`:
  Contains all the needed entities to represent weather conditions. For more
  details see [Car](../model/fraccalvieri/ines.md#car)

The organization reflects a **Domain-Driven Design (DDD)** approach, where each subpackage encapsulates its own part of
the simulation world, and dependencies between them follow logical domain relationships.

---
