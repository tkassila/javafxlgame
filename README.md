# JavaFX L Game

A digital desktop implementation of the classic **L Game**, invented by Edward de Bono, built with Java and JavaFX.

## 📌 About the Game
The L Game is a simple but strategic two-player board game played on a 4x4 grid. Each player controls a 3x2 L-shaped piece, and there are two neutral coins on the board. The objective is to block your opponent so that they cannot move their L-piece to a new valid position.

## ✨ Features
- **Strict Rule Enforcement**: Validates L-piece placements and automatically detects blocked game-over states.
- **State Persistence**: The game automatically saves your progress locally (`state.dat`). You can close the app anytime and resume right where you left off.
- **Multiple Languages**: Fully localized in English, Finnish, Swedish, German, and Spanish. Change the language on the fly via the menu.
- **Save Slots**: Save multiple unfinished games, browse them in a list, and resume them later.
- **Player Customization**: Edit player names for a personalized experience.
- **Cross-Platform**: Runs on Linux, macOS, and Windows.

## 🚀 How to Run

Ensure you have **Java 11+** and **Maven** installed.

You can launch the game using the provided startup scripts:

**Linux / macOS:**
```bash
./run.sh
```

**Windows:**
```cmd
run.cmd
```

Alternatively, you can run it directly using Maven:
```bash
mvn clean compile javafx:run
```

## 🎮 Controls
* **Move Mouse**: Move the L-piece outline over the board.
* **Scroll Wheel / R**: Rotate the L-piece and toggle vertical/horizontal alignment.
* **Double Click / F**: Flip (mirror) the L-piece orientation.
* **Left Click**: Draft-place the L-piece onto the grid.
* **V / Enter**: Confirm your L-piece move (while in draft mode).
* **Escape / C**: Lift the L-piece back up (cancel draft placement).
* **Left Click (on coin)**: Select a neutral coin and click an empty square to move it.
* **S**: Skip the optional neutral coin move and end your turn.
* **N**: Start a new game.

## 🛠️ Technology Stack
* Java 11
* JavaFX (UI, WebView for Help documentation)
* Maven (Build & Dependency Management)
* BootstrapFX (CSS Styling)
