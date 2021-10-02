package gui;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import lorasim2.LoRaNode;

/**
 *
 * @author alex
 */
public class CanvasPanel extends JPanel implements MouseListener {
    private final int NODE_IMG_SIZE = 70;
    
    private enum StateEnum { NONE, ADDING_NODE };
    private StateEnum state;
    private BufferedImage img_lora_node;

    private final HashMap<Point, LoRaNode> gui_nodes;
    
    public CanvasPanel() {
        super();
        this.state = StateEnum.NONE;
        this.addMouseListener(this);
        this.gui_nodes = new HashMap<>();
        
        this.img_lora_node = null;
        
        try {
            URL url = this.getClass().getClassLoader().getResource("lora-node.png");
            img_lora_node = ImageIO.read(url);
        } catch (IOException e) {
            System.err.println("Error loading some resources.");
        }
    }
    
    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        
        gui_nodes.keySet().forEach(p -> {
            g2.drawImage(
                    img_lora_node,
                    (int)p.getX() - NODE_IMG_SIZE/2,
                    (int)p.getY() - NODE_IMG_SIZE/2,
                    NODE_IMG_SIZE,
                    NODE_IMG_SIZE,
                    this
            );
        });
    }
    
    public void beginAddNode() {
        this.state = StateEnum.ADDING_NODE;
        this.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        if (state == StateEnum.ADDING_NODE) {
            state = StateEnum.NONE;
            this.setCursor(Cursor.getDefaultCursor());
            this.gui_nodes.put(me.getPoint(), null);
            this.repaint();
        }
    }

    @Override
    public void mousePressed(MouseEvent me) {}

    @Override
    public void mouseReleased(MouseEvent me) {}

    @Override
    public void mouseEntered(MouseEvent me) {}

    @Override
    public void mouseExited(MouseEvent me) {}
}
