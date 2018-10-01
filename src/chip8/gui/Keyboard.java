/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chip8.gui;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 *
 * @author BlueStorm
 */
public class Keyboard extends KeyAdapter {

    private final int[] map = new int[]{
        KeyEvent.VK_X, KeyEvent.VK_1, KeyEvent.VK_2, KeyEvent.VK_3,
        KeyEvent.VK_Q, KeyEvent.VK_W, KeyEvent.VK_E, KeyEvent.VK_A,
        KeyEvent.VK_S, KeyEvent.VK_D, KeyEvent.VK_Z, KeyEvent.VK_C,
        KeyEvent.VK_4, KeyEvent.VK_R, KeyEvent.VK_F, KeyEvent.VK_V};

    private int key = -1;

    @Override
    public void keyPressed(KeyEvent ke) {
        key = position(ke);
        System.err.println("KEY PRESSED " + key);
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        key = -1;
    }

    public int position(KeyEvent ke) {
        System.err.println("position ke : " + ke.getKeyCode());
        for (int i = 0; i < map.length; i++) {
            if (ke.getKeyCode() == map[i]) {
                System.out.println("BOTON!!!! " + i);
                return i;
            }
        }
        return -1;
    }

    public int getKey() {
        return key;
    }

}
