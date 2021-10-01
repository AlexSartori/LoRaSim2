package gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

/**
 *
 * @author alex
 */
public class CanvasPanel extends JPanel {
    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.drawString("Loading...", this.getWidth()/2, this.getHeight()/2);
    }
}
