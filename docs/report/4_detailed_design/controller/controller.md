---
title: Controller
nav_order: 3
parent: Detailed Design
---
# Controller

### Events Processing

Event processing follows a chronological execution model where events are dequeued and processed in timestamp order.
Each event contains the logic necessary to modify the race state, including updates to car positions, fuel levels, tire
conditions, and race standings. The processing system ensures atomicity, where each event either completes successfully
or leaves the state unchanged.
The processor validates event preconditions before execution, checking for conflicts or impossible scenarios. For
example, a pit stop event verifies that the car is in the correct track position and has sufficient time to complete the
operation. Failed events are logged but do not interrupt the simulation flow.

### Events Scheduling

Event scheduling manages the dynamic creation and queuing of future events based on current race conditions. The
scheduler analyzes car states, weather patterns, and race progress to determine when events should occur. Strategic
events like pit stops are scheduled based on fuel consumption rates and tire degradation models.
Random events are scheduled using probability distributions that consider factors such as car reliability, weather
conditions, and race intensity. The scheduler maintains event priorities to handle conflicts when multiple events occur
simultaneously, ensuring realistic race progression and maintaining simulation consistency.