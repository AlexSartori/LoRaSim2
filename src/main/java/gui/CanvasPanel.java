package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import lorasim2.LoRaMarkovModel;
import lorasim2.LoRaNode;

/**
 *
 * @author alex
 */
public class CanvasPanel extends JPanel {
    private final int NODE_IMG_SIZE = 70;
    private final Random rng = new Random();
    
    private BufferedImage img_lora_node;

    private final HashMap<LoRaNode, Point> gui_nodes;
    
    public CanvasPanel() {
        super();
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
        g2.clearRect(0, 0, getWidth(), getHeight());
        
        /* Set gray dashed stroke for node links */
        Object[] nodes = gui_nodes.keySet().toArray();
        g2.setStroke(
            new BasicStroke(3, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 0, new float[]{10}, 0)
        );
        g2.setColor(Color.GRAY);
        
        /* Draw links */
        for (int i = 0; i < nodes.length; i++) {
            for (int j = i; j < nodes.length; j++) {
                Point p1 = gui_nodes.get(nodes[i]),
                      p2 = gui_nodes.get(nodes[j]);
                g2.drawLine((int)p1.getX(), (int)p1.getY(), (int)p2.getX(), (int)p2.getY());
            }
        }
        
        /* Draw nodes */
        gui_nodes.values().forEach(p -> {
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
    
    public void clearAll() {
        gui_nodes.clear();
        this.repaint();
    }
    
    public void addRandomNode() {
        Point p = new Point(
            NODE_IMG_SIZE + rng.nextInt(this.getWidth() - NODE_IMG_SIZE*2),
            NODE_IMG_SIZE + rng.nextInt(this.getHeight() - NODE_IMG_SIZE*2)
        );
        gui_nodes.put(new LoRaNode(new LoRaMarkovModel()), p);
        
        this.repaint();
    }
    
    /*private LoRaNode getClosestNodeToPoint(Point p) {
        LoRaNode closest_node = null;
        double closest_dist = -1, tmp_dist;
        
        for (Entry<LoRaNode, Point> e : gui_nodes.entrySet()) {
            tmp_dist = e.getValue().distance(p);
            
            if (closest_dist == -1 || tmp_dist < closest_dist) {
                closest_dist = tmp_dist;
                closest_node = e.getKey();
            }
        }
        
        return closest_dist <= NODE_IMG_SIZE/2 ? closest_node : null;
    }*/
}
