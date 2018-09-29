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

    private int delayTimer;
    private int soundTimer;

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
        switch (opcodeHeader()) {
            case 0x0:
                decodeAndExecute0x0();
                break;
            case 0x1:
                //1NNN 	Flow 	goto NNN; 	Jumps to address NNN.
                pc = nnn();
                break;
            case 0x2:
                //2NNN 	Flow 	*(0xNNN)() 	Calls subroutine at NNN. 
                stack[stackPointer++] = pc;
                pc = nnn();
                break;
            case 0x3:
                //3XNN 	Cond 	if(Vx==NN) 	Skips the next instruction if VX equals NN.
                if (V[x()] == nn()) {
                    pc += 2;
                }
                pc += 2;
                break;
            case 0x4:
                //4XNN 	Cond 	if(Vx!=NN) 	Skips the next instruction if VX doesn't equal NN. (Usually the next instruction is a jump to skip a code block) 
                if (V[x()] != nn()) {
                    pc += 2;
                }
                pc += 2;
                break;
            case 0x5:
                //5XY0 	Cond 	if(Vx==Vy) 	Skips the next instruction if VX equals VY.
                if (V[x()] == V[y()]) {
                    pc += 2;
                }
                pc += 2;
                break;
            case 0x6:
                //6XNN 	Const 	Vx = NN 	Sets VX to NN. 
                V[x()] = nn();
                pc += 2;
                break;
            case 0x7:
                //7XNN  Const Vx += NN  Adds NN to VX. (Carry flag is not changed)
                V[x()] += nn(); // esto probablemente haga overflow...
                pc += 2;
                break;
            case 0x8:
                decodeAndExecute0x8();
                break;
            case 0x9:
                //9XY0 	Cond 	if(Vx!=Vy) 	Skips the next instruction if VX doesn't equal VY.
                if (V[x()] != V[y()]) {
                    pc += 2;
                }
                pc += 2;
                break;
            case 0xA:
                //ANNN 	MEM 	I = NNN 	Sets I to the address NNN. 
                I = nnn();
                pc += 2;
                break;
            case 0xB:
                //BNNN 	Flow 	PC=V0+NNN 	Jumps to the address NNN plus V0. 
                pc = V[0] + nnn();
                break;
            case 0xC:
                //CXNN 	Rand 	Vx=rand()&NN 	Sets VX to the result of a bitwise and operation on a random number (Typically: 0 to 255) and NN. 
                V[x()] = (int) (Math.random() * 255) & nn(); //Todo check this...
                pc += 2;
                break;
            case 0xD:
                //DXYN 	Disp 	draw(Vx,Vy,N) 	Draws a sprite at coordinate (VX, VY) that has a width of 8 pixels and a height of N pixels. Each row of 8 pixels is read as bit-coded starting from memory location I; I value doesn’t change after the execution of this instruction. As described above, VF is set to 1 if any screen pixels are flipped from set to unset when the sprite is drawn, and to 0 if that doesn’t happen 
                int x = V[x()];
                int y = V[y()];
                int n = n();
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

            case 0xE:
                decodeAndExecute0xE();
                break;

            case 0xF:
                decodeAndExecute0xF();
                break;

            default:
                warnUnsupportedOpcode();
        }
    }

    private void decodeAndExecute0x0() {
        switch (opcode & 0xF0FF) {
            case 0x00E0:
                //00E0 	Display 	disp_clear() 	Clears the screen. 
                Arrays.fill(gfx, 0);
                pc += 2;
                break;
            case 0x00EE:
                //00EE 	Flow 	return; 	Returns from a subroutine. 
                pc = stack[--stackPointer];
                pc += 2;
                break;
            default: //0nnn This instruction is only used on the old computers on which Chip-8 was originally implemented. It is ignored by modern interpreters.
                warnUnsupportedOpcode();
        }
    }

    private void decodeAndExecute0x8() {
        switch (opcode & 0xF00F) {
            case 0x8000:
                //8XY0 	Assign 	Vx=Vy 	Sets VX to the value of VY. 
                V[x()] = V[y()];
                break;
            case 0x8001:
                //8XY1 	BitOp 	Vx=Vx|Vy 	Sets VX to VX or VY. (Bitwise OR operation) 
                V[x()] |= V[y()];
                break;
            case 0x8002:
                //8XY2 	BitOp 	Vx=Vx&Vy 	Sets VX to VX and VY. (Bitwise AND operation) 
                V[x()] &= V[y()];
                break;
            case 0x8003:
                //8XY3 	BitOp 	Vx=Vx^Vy 	Sets VX to VX xor VY. 
                V[x()] ^= V[y()];
                break;
            case 0x8004:
                //8XY4 	Math 	Vx += Vy 	Adds VY to VX. VF is set to 1 when there's a carry, and to 0 when there isn't. 
                //TODO IMPLEMENT CARRY !!!
                V[x()] += V[y()];
                break;
            case 0x8005:
                //8XY5 	Math 	Vx -= Vy 	VY is subtracted from VX. VF is set to 0 when there's a borrow, and 1 when there isn't.
                // TODO IMPLEMENT CARRY !!!
                V[x()] -= V[y()];
                break;
            case 0x8006:
                //8XY6 	BitOp 	Vx>>=1 	Stores the least significant bit of VX in VF and then shifts VX to the right by 1.
                V[0xF] = V[x()] & 0x1;
                V[x()] >>= 1;
                break;
            case 0x8007:
                //8XY7 	Math 	Vx=Vy-Vx 	Sets VX to VY minus VX. VF is set to 0 when there's a borrow, and 1 when there isn't.
                // TODO IMPLEMENT BORROW
                V[x()] = V[y()] - V[x()];
                break;
            case 0x800E:
                //8XYE 	BitOp 	Vx<<=1 	Stores the most significant bit of VX in VF and then shifts VX to the left by 1.
                V[0xF] = V[x()] & 0x8;
                V[x()] <<= 1;
                break;
            default:
                warnUnsupportedOpcode();
        }
    }

    private void decodeAndExecute0xE() {
        switch (opcode & 0xF0FF) {
            case 0xE09E:
                //EX9E 	KeyOp 	if(key()==Vx) 	Skips the next instruction if the key stored in VX is pressed.
                warnUnsupportedOpcode();
                break;
            case 0xE0A1:
                //EXA1 	KeyOp 	if(key()!=Vx) 	Skips the next instruction if the key stored in VX isn't pressed.
                warnUnsupportedOpcode();
                break;
        }
    }

    private void decodeAndExecute0xF() {
        switch (opcode & 0xF0FF) {
            case 0xF007:
                //FX07 	Timer 	Vx = get_delay() 	Sets VX to the value of the delay timer. 
                V[x()] = delayTimer;
                pc += 2;
                break;
            case 0xF00A:
                //FX0A 	KeyOp 	Vx = get_key() 	A key press is awaited, and then stored in VX. (Blocking Operation. All instruction halted until next key event) 
                warnUnsupportedOpcode();
                break;
            case 0xF015:
                //FX15 	Timer 	delay_timer(Vx) 	Sets the delay timer to VX. 
                delayTimer = V[x()];
                pc += 2;
                break;
            case 0xF018:
                //FX18 	Sound 	sound_timer(Vx) 	Sets the sound timer to VX. 
                soundTimer = V[x()];
                pc += 2;
                break;
            case 0xF01E:
                //FX1E 	MEM 	I +=Vx 	Adds VX to I.
                I += V[x()];
                pc += 2;
                break;
            case 0xF029:
                //FX29 	MEM 	I=sprite_addr[Vx] 	Sets I to the location of the sprite for the character in VX. Characters 0-F (in hexadecimal) are represented by a 4x5 font. 
                I = V[x()] * 5; //// TODO thats probably wrong
                pc += 2;
                break;
            case 0xF033:
                //FX33 	BCD 	set_BCD(Vx);    *(I+0)=BCD(3);  *(I+1)=BCD(2);  *(I+2)=BCD(1);
                //Stores the binary-coded decimal representation of VX, with the most significant of three digits at the address in I, the middle digit at I plus 1, and the least significant digit at I plus 2. (In other words, take the decimal representation of VX, place the hundreds digit in memory at location in I, the tens digit at location I+1, and the ones digit at location I+2.) 
                memory[I] = V[x()] % 10;
                memory[I + 1] = V[x()] / 10 % 10;
                memory[I + 2] = V[x()] / 100 % 10;
                pc += 2;
                break;
            case 0xF055:
                //FX55 	MEM 	reg_dump(Vx,&I) 	Stores V0 to VX (including VX) in memory starting at address I. The offset from I is increased by 1 for each value written, but I itself is left unmodified. 
                for (int i = 0; x() > i; i++) {
                    memory[I + i] = V[0 + i];
                }
                pc += 2;
                break;
            case 0xF065:
                //FX65 	MEM 	reg_load(Vx,&I) 	Fills V0 to VX (including VX) with values from memory starting at address I. The offset from I is increased by 1 for each value written, but I itself is left unmodified. 
                for (int i = 0; x() > i; i++) {
                    V[0 + i] = memory[I + i];
                }
                pc += 2;
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
        System.out.println("Adress: " + Integer.toHexString(pc) + " Opcode: " + Integer.toHexString(opcode) + "");
    }

    private int opcodeHeader() {
        return (opcode & 0xF000) >> 12;
    }

    private int x() {
        return (opcode & 0x0F00) >> 8;
    }

    private int y() {
        return (opcode & 0x00F0) >> 4;
    }

    private int n() {
        return opcode & 0x000F;
    }

    private int nn() {
        return opcode & 0x00FF;
    }

    private int nnn() {
        return opcode & 0x0FFF;
    }

}
