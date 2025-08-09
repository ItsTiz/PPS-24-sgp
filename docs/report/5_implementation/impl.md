---
title: Implementation
nav_order: 5
parent: Report
---

# Implementation


### drafts

For SimulationEngine, explain monadic style:

**Execution flow inside `executeStep`:**

1. **Fetch current state** from the `SimulationState` manager.
2. **Dequeue events** scheduled for the current simulation time.
3. **Advance simulation time** by `logicalTimeStep`.
4. **Process events**:
    - Apply each event to the current state using the `EventProcessor`.
    - Update the simulation state after each event.
5. **Decide continuation**:
    - Continue if there are still events to process **and** the race is not finished.