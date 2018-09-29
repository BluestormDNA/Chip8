/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chip8.interpreter;

import java.util.Arrays;

/**
 *
 * @author BlueStorm
 */
public class Cpu {

    private int pc;
    private int opcode;
    private int I;
    private int stackPointer;
    private final int[] V;
    private final int[] stack;
    private final int[] memory;

    private final int[] gfx;
    private boolean drawFlag;

    public Cpu(Memory memory, Gfx gfx) {
        pc = 0x200;
        V = new int[0x10];
        stack = new int[0x10];
        this.memory = memory.getMemory();
        this.gfx = gfx.getGfx();
        drawFlag = false;
    }

    public void fetch() {
        opcode = memory[pc] << 8 | memory[pc + 1]; // short (byte | byte) 16 bits
        infoFetchedOpcode();
    }

    public void decodeAndExecute() {
        switch (opcode & 0xF000) {
            case 0x0000:
                decodeAndExecute0x0000();
                break;
            case 0x1000:
                //1NNN 	Flow 	goto NNN; 	Jumps to address NNN.
                pc = opcode & 0x0FFF;
                break;
            case 0x2000:
                //2nnn - CALL
                //Calls address NNN
                stack[stackPointer++] = pc;
                pc = opcode & 0x0FFF;
                break;
            case 0x3000:
                //3XNN 	Cond 	if(Vx==NN) 	Skips the next instruction if VX equals NN.
                if (V[(opcode & 0x0F00) >> 8] == (opcode & 0x00FF)) {
                    pc += 2;
                }
                pc += 2;
                break;
            case 0x4000:
                //4XNN 	Cond 	if(Vx!=NN) 	Skips the next instruction if VX doesn't equal NN. (Usually the next instruction is a jump to skip a code block) 
                warnUnsupportedOpcode();
                break;
            case 0x5000:
                //5XY0 	Cond 	if(Vx==Vy) 	Skips the next instruction if VX equals VY.
                warnUnsupportedOpcode();
                break;
            case 0x6000:
                //6xkk - LD Vx, byte
                //Set Vx = kk.
                //The interpreter puts the value kk into register Vx.
                V[(opcode & 0x0F00) >> 8] = opcode & 0x00FF;
                pc += 2;
                break;
            case 0x7000:
                //7XNN  Const Vx += NN  Adds NN to VX. (Carry flag is not changed)
                V[(opcode & 0x0F00) >> 8] += opcode & 0x00FF; // esto probablemente haga overflow...
                pc += 2;
                break;
            case 0x8000:
                decodeAndExecute0x8000();
                break;
            case 0x9000:
                warnUnsupportedOpcode();
                break;
            case 0xA000:
                //Annn - LD I, addr
                //Set I = nnn.
                //The value of register I is set to nnn.
                I = opcode & 0x0FFF;
                pc += 2;
                break;
            case 0xB000:
                warnUnsupportedOpcode();
                break;
            case 0xC000:
                warnUnsupportedOpcode();
                break;
            case 0xD000:
                /* Dxyn - DRW Vx, Vy, nibble
                Display n-byte sprite starting at memory location I at (Vx, Vy), set VF = collision.
                The interpreter reads n bytes from memory, starting at the address stored in I.
                These bytes are then displayed as sprites on screen at coordinates (Vx, Vy).
                Sprites are XORed onto the existing screen. If this causes any pixels to be erased,
                VF is set to 1, otherwise it is set to 0. If the sprite is positioned so part of it
                is outside the coordinates of the display, it wraps around to the opposite side of the screen.*/
                int x = V[(opcode & 0x0F00) >> 8];
                int y = V[(opcode & 0x00F0) >> 4];
                int n = opcode & 0x000F;
                int[] sprite = new int[n];
                System.arraycopy(memory, I, sprite, 0, n);

                V[0xF] = 0;

                for (int line = 0; line < n; line++) {
                    //System.out.println("for line " + line + " n = " + n);
                    for (int bit = 0; bit < 8; bit++) {
                        //System.out.println("for bit " + bit);
                        if (((sprite[line] & 0xFF) & (0x80 >> bit)) != 0) {
                            //System.out.println("if: " + (b & (0x80 >> bit)) + " " + ((0x80 >> bit) == 1));
                            if ((gfx[x + bit + ((y + line) * 64)] ^ 1) == 0) {
                                V[0xF] = 1;
                            }
                            gfx[x + bit + ((y + line) * 64)] ^= 1;
                        }
                    }
                }

                //System.out.println("drawX " + x);
                //System.out.println("drawY " + y);
                //System.out.println("n " + n);
                drawFlag = true;
                pc += 2;
                break;

            case 0xE000:
                warnUnsupportedOpcode();
                break;

            case 0xF000:
                decodeAndExecute0xF000();

                break;

            default:
                warnUnsupportedOpcode();
        }
    }

    private void decodeAndExecute0x0000() {
        System.out.println(Integer.toHexString(opcode));
        switch (opcode & 0xF0FF) {
            case 0x000E:
                //00E0 	Display 	disp_clear() 	Clears the screen. 
                Arrays.fill(gfx, 0);
                pc += 2;
                break;
            case 0x00EE:
                //00EE 	Flow 	return; 	Returns from a subroutine. 
                pc = stack[--stackPointer];
                pc += 2;
                break;
            default: //0nnn omited This instruction is only used on the old computers on which Chip-8 was originally implemented. It is ignored by modern interpreters.
                warnUnsupportedOpcode();
        }
    }

    private void decodeAndExecute0x8000() {
        switch (opcode & 0xF0FF) {
            case 0xF033:
                warnUnsupportedOpcode();
                break;
            default:
                warnUnsupportedOpcode();
        }
    }

    private void decodeAndExecute0xF000() {
        switch (opcode & 0xF0FF) {
            case 0xF033:
                warnUnsupportedOpcode();
                break;
            default:
                warnUnsupportedOpcode();
        }
    }

    public boolean isDrawFlag() {
        return drawFlag;
    }

    public void setDrawFlag(boolean drawFlag) {
        this.drawFlag = drawFlag;
    }

    public void warnUnsupportedOpcode() {
        System.err.println("Unsupported Opcode: " + Integer.toHexString(opcode));
    }

    public void infoFetchedOpcode() {
        System.out.println(Integer.toHexString(opcode) + "");
    }
}
