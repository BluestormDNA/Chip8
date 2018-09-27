/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chip8.gui;

import chip8.interpreter.Gfx;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author BlueStorm
 */
public class Screen extends JPanel {

    private final int[] gfx;

    public Screen(Gfx gfx) {
        this.gfx = gfx.getGfx();

        this.setPreferredSize(new Dimension(640, 320));
        JFrame window = new JFrame("Chip 8");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.add(this);
        window.pack();
        window.setVisible(true);
    }

    @Override
    public void paint(Graphics g) {
        for (int i = 0; i < gfx.length; i++) {
            int x = i % 64;
            int y = i / 64;

            Color color = (gfx[i] == 0) ? Color.BLACK : Color.WHITE;
            g.setColor(color);

            g.fillRect(x * 10, y * 10, 10, 10);
        }

    }

}
