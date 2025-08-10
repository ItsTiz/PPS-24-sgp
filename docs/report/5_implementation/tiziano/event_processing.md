---
title: Event Processing
nav_order: 2
parent: Implementation - Tiziano Vuksan
---


## **RaceState**

Represents the complete state of the race at a given moment in the simulation.

- Contains all race data, including cars, track, events, and environmental conditions.
- Updated only through controlled mechanisms to maintain consistency.

---


## **EventProcessor**

Handles in-race events that can affect the simulation.

- Processes events such as weather changes, incidents, or pit stops.
- Delegates changes to the `SimulationEngineImpl` so they are applied in the next state update.
- Keeps event handling separate from the core simulation logic for modularity.

---