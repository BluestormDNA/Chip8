/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chip8.interpreter;

import chip8.data.Data;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author BlueStorm
 */
public class Memory {

    private final static int ROM_MEM_OFFSET = 0x200;
    private final static int FONT_MEM_OFFSET = 0x50;

    private final int[] memory;

    public Memory() {
        memory = new int[0x1000];
    }

    public int[] getMemory() {
        return memory;
    }

    public void loadRom(String file) {
        try {
            byte[] rom = Files.readAllBytes(new File(file).toPath());
            for (int i = 0; i < rom.length; i++) {
                memory[ROM_MEM_OFFSET + i] = Byte.toUnsignedInt(rom[i]);
            }
        } catch (IOException ex) {
            Logger.getLogger(Chip8.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loadFont() {
        for (int i = 0; i < Data.FONTSET.length; i++) {
            memory[FONT_MEM_OFFSET + i] = Data.FONTSET[i];
        }
    }

    public void printMem() {
        for (int i = 0; i < memory.length; i++) {
            System.out.println(i + " " + memory[i]);
            System.out.println(Integer.toHexString(memory[i] & 0xFF) + " : " + (memory[i] & 0xFF));
        }
    }
}
