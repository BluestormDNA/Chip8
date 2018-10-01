/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chip8.interpreter;

import chip8.gui.Keyboard;
import chip8.gui.Screen;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author BlueStorm
 */
public class Chip8 extends Thread {

    private Memory memory;
    private Cpu cpu;
    private Gfx gfx;
    private Screen screen;
    private Keyboard keyboard;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Chip8 chip8 = new Chip8();
        chip8.init();
    }

    public void init() {
        memory = new Memory();
        memory.loadFont();
        memory.loadRom("./rom/pong2.c8");
        
        gfx = new Gfx();
        keyboard = new Keyboard();
        cpu = new Cpu(memory, gfx, keyboard);

        screen = new Screen(gfx);
        screen.addKeyListener(keyboard);
        

        while (true) {
            cpu.fetch();
            cpu.decodeAndExecute();
            cpu.updateTimers();
            if (cpu.isDrawFlag()) {
                screen.repaint();
                cpu.setDrawFlag(false);
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                Logger.getLogger(Chip8.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

}
