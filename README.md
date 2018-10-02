# Chip8
Chip 8 Interpreter / Emulator

This is a Java WIP Chip8 Interpreter / Emulator mainly as an educational exercice

What has been implemented:
* My basic approach to the architecture of the emulator and the interpreter.
* A rom loader.
* A swing window to see something (doh!)
* Very basic sound (even by chip8 standards)
* Choppy Keyboard input
* CPU stack, stackpointer, registers and a all the cpu opcodes for fetch decode and execution.
  * That damn Dxyn still needs work
* Passes test rom
* At the moment almost all games boot but theres an array oob ex when drawing pending to be fixed

A legacy Pong Screen from the first builds boot when only a bunch of opcodes where implemented (00EE, 1nnn, 2nnn, 3xnn, 6xkk, 7xnn, Annn and partial Dxyn:


![Pong2](https://user-images.githubusercontent.com/28767885/46166952-c58b7700-c294-11e8-99c5-374c34168b1e.png)

