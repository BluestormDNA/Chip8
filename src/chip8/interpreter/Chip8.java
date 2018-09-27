/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chip8.interpreter;

import chip8.gui.Screen;
import javax.swing.SwingUtilities;

/**
 *
 * @author BlueStorm
 */
public class Chip8 {

    private Memory memory;
    private Cpu cpu;
    private Gfx gfx;
    private Screen screen;

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
        cpu = new Cpu(memory, gfx);

        //SwingUtilities.invokeLater(() -> {
        screen = new Screen(gfx);
        //});

        while (true) {
            cpu.fetch();
            cpu.decodeAndExecute();
            if (cpu.isDrawFlag()) {
                //gfx.debugDraw();
                cpu.setDrawFlag(false);
            }
            //memory.printMem();
        }
    }

}
