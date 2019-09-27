# Kotlin Game Engine + ksnake

### Overview:

* **Origin: Regular school project**
* Docs: German/pdf (sorry for that). Comments are English.
* Language: Kotlin
* Licence: GPLv3
* Copyright: Lucy von Känel <snow.dream.ch@gmail.com> - 2018

### Content:

A Game Engine written in kotlin where the engine development was driven
by the included game "ksnake-evolution". A slightly altered version of "snake".
This is not a complete engine by all means but already has the features 
for basic terminal games.

The game itself is in it's own package and the engine itself does not rely
on that implementation. It's completely independent.

### Feature highlights:

* Focus on multithreading
* 2D ASCII Compositor implemented (Nothing else yet) 
  * Keyboard events without pressing the return key
  * Reaches up to 400'000 FPS (hardware and implementation dependent)
  * Template entities for Text, Walls and Highscores/Menus
* Compositor multiplexing (Remote rendering could be added)
* Fake vSync
* Reprojection
* Independent Logic/Graphics compositon
* Controllable FPS and Logic ticks
* 2D Collision detection, sync and async, no 3D yet
* Windows/Linux/Mac(untested)

### Example Screenshot:

![ksnake-evolution](/doc/screenshot.png?raw=true "ksnake-evolution")

### Ksnake UML:

![ksnake-evolution](/doc/uml.png?raw=true "ksnake-evolution")

