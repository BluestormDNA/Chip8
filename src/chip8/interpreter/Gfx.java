/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chip8.interpreter;

/**
 *
 * @author BlueStorm
 */
public class Gfx {

    private final int ROWS = 32;
    private final int COLUMNS = 64;

    private final int[] gfx;

    public Gfx() {
        gfx = new int[ROWS * COLUMNS];
    }

    public void debugDraw() {
        for (int y = 0; y < ROWS; y++) {
            System.out.println("");
            for (int x = 0; x < COLUMNS; x++) {
                if (gfx[(y * COLUMNS) + x] == 0) {
                    System.out.print("0");
                } else {
                    System.out.print("1");
                }
            }
        }
        System.out.println("");
    }

    public int[] getGfx() {
        return gfx;
    }

}
