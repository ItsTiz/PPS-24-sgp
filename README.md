# PPS-24-sgp

## 🏁 ScalaGP

#### **ScalaGP Simulator** is a Formula 1-inspired racing simulation project developed in Scala. It models a full race environment including drivers, cars, tires, weather and strategies. The simulation visualizes races on a custom track, where cars move based on predefined mechanics such as speed, fuel consumption, tire degradation, and sector grip. 
---

## 🎮 Description

The **ScalaGP** simulates a Formula-style car race with the following key features:

- 🏎️ **Cars**: Each car has its own model, number, max fuel, tire setup and a race state.
- ⛽ **Fuel and Tires**: Cars consume fuel and tire durability based on their driving strategy and sector grip.
- 🌦️ **Weather**: Weather conditions (Sunny, Rainy, Foggy) affect tire grip and car behavior.
- 🗺️ **Tracks**: The track is composed of different sectors with varying lengths, grip levels, and speed limits.
- 📈 **Race State**: Simulates car progression over time, lap counting, and final results.
- 🖼️ **Graphical View**: The UI includes a simple real-time 2D visualization using JavaFX Canvas. The track and cars are rendered, and cars move progressively during the race.

Each car aims to complete the defined number of laps while managing its resources and adapting to the track and weather conditions. The simulation ends when all laps are completed.

---

## 🚀 How to Run

To run the simulator:

1. **Download the JAR** from the latest release.
2. Open your terminal.
3. Run the following command:

```bash
java -jar sgp.jar
