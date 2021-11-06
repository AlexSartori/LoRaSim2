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
import java.util.Set;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import lorasim2.LoRaGateway;
import lorasim2.LoRaMarkovModel;
import lorasim2.LoRaNode;

/**
 *
 * @author alex
 */
public class CanvasPanel extends JPanel {
    private final int NODE_IMG_SIZE = 70;
    private final Random rng = new Random();
    
    private BufferedImage img_lora_node, img_lora_gateway;

    private final HashMap<LoRaNode, Point> gui_nodes;
    private final HashMap<LoRaGateway, Point> gui_gateways;
    
    public CanvasPanel() {
        super();
        this.gui_nodes = new HashMap<>();
        this.gui_gateways = new HashMap<>();
        
        this.img_lora_node = this.img_lora_gateway = null;
        
        try {
            URL url = this.getClass().getClassLoader().getResource("lora-node.png");
            img_lora_node = ImageIO.read(url);
            
            url = this.getClass().getClassLoader().getResource("lora-gateway.png");
            img_lora_gateway = ImageIO.read(url);
        } catch (IOException e) {
            System.err.println("Error loading some resources: " + e.getMessage());
        }
    }
    
    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.clearRect(0, 0, getWidth(), getHeight());
        
        Set<Entry<LoRaNode, Point>> nodes = gui_nodes.entrySet();
        Set<Entry<LoRaGateway, Point>> gateways = gui_gateways.entrySet();

        /* Set blue stroke for direct node links */
        g2.setStroke(
            new BasicStroke(4, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL)
        );
        g2.setColor(Color.BLUE);
        
        /* Draw direct links */
        gateways.forEach(entry_g -> {
            nodes.forEach(entry_n -> {
                Point p1 = entry_g.getValue(),
                      p2 = entry_n.getValue();
                g2.drawLine((int)p1.getX(), (int)p1.getY(), (int)p2.getX(), (int)p2.getY());
            });
        });
        
        /* Set thin gray dashed stroke for interference links */
        g2.setStroke(
            new BasicStroke(1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 0, new float[]{10}, 0)
        );
        g2.setColor(Color.GRAY);
        
        /* Draw interference links */
        Object[] nodes_pts = gui_nodes.values().toArray();
        for (int i = 0; i < nodes_pts.length; i++) {
            for (int j = i; j < nodes_pts.length; j++) {
                Point p1 = (Point)nodes_pts[i],
                      p2 = (Point)nodes_pts[j];
                g2.drawLine((int)p1.getX(), (int)p1.getY(), (int)p2.getX(), (int)p2.getY());
            }
        }
        
        /* Draw nodes */
        gateways.forEach(e -> {
            g2.drawImage(
                img_lora_gateway,
                (int)e.getValue().getX() - NODE_IMG_SIZE/2,
                (int)e.getValue().getY() - NODE_IMG_SIZE/2,
                NODE_IMG_SIZE,
                NODE_IMG_SIZE,
                this
            );    
        });
        nodes.forEach(e -> {
            g2.drawImage(
                img_lora_node,
                (int)e.getValue().getX() - NODE_IMG_SIZE/2,
                (int)e.getValue().getY() - NODE_IMG_SIZE/2,
                NODE_IMG_SIZE,
                NODE_IMG_SIZE,
                this
            );
        });
        
    }
    
    public void clearAll() {
        gui_nodes.clear();
        gui_gateways.clear();
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
    
    public void addRandomGateway() {
        Point p = new Point(
            NODE_IMG_SIZE + rng.nextInt(this.getWidth() - NODE_IMG_SIZE*2),
            NODE_IMG_SIZE + rng.nextInt(this.getHeight() - NODE_IMG_SIZE*2)
        );
        gui_gateways.put(new LoRaGateway(), p);
        
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
