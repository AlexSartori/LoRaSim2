package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import lorasim2.LoRaGateway;
import lorasim2.LoRaMarkovModel;
import lorasim2.LoRaNode;
import lorasim2.SimConfig;
import lorasim2.Simulator;

/**
 *
 * @author alex
 */
public class CanvasPanel extends JPanel {
    private final Simulator simulator;
    private final int NODE_IMG_SIZE = 70;
    
    private BufferedImage img_lora_node, img_lora_gateway;

    private final HashMap<LoRaNode, Point> gui_nodes;
    private final HashMap<LoRaGateway, Point> gui_gateways;
    
    public CanvasPanel(Simulator sim) {
        super();
        this.simulator = sim;
        this.gui_nodes = new HashMap<>();
        this.gui_gateways = new HashMap<>();
        
        this.img_lora_node = this.img_lora_gateway = null;
        
        try {
            URL url = this.getClass().getClassLoader().getResource("lora-node.png");
            img_lora_node = ImageIO.read(url);
            
            url = this.getClass().getClassLoader().getResource("lora-gateway.png");
            img_lora_gateway = ImageIO.read(url);
        } catch (IOException e) {
            System.err.println("[Canvas]: Error loading some resources: " + e.getMessage());
        }
    }
    
    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        
        g2.clearRect(0, 0, getWidth(), getHeight());
        g2.setFont(new Font("sans", Font.BOLD, 16));
        
        this._drawLinks(g2);
        this._drawNodes(g2);        
    }
    
    private void _drawNodes(Graphics2D g2) {
        g2.setColor(Color.BLACK);
        
        gui_gateways.entrySet().forEach(e -> {
            Point p = e.getValue();
            LoRaGateway n = e.getKey();
            
            g2.drawImage(
                img_lora_gateway,
                (int)p.getX() - NODE_IMG_SIZE/2,
                (int)p.getY() - NODE_IMG_SIZE/2,
                NODE_IMG_SIZE,
                NODE_IMG_SIZE,
                this
            );
            
            int x = (int)(p.getX() - 40);
            int y = (int)(p.getY() + NODE_IMG_SIZE*0.8);
            String str = "Gateway (id:" + n.id + ")";
            g2.drawString(str, x, y);
        });
        
        gui_nodes.entrySet().forEach(e -> {
            Point p = e.getValue();
            LoRaNode n = e.getKey();
            
            g2.drawImage(
                img_lora_node,
                (int)p.getX() - NODE_IMG_SIZE/2,
                (int)p.getY() - NODE_IMG_SIZE/2,
                NODE_IMG_SIZE,
                NODE_IMG_SIZE,
                this
            );
            
            int x = (int)(p.getX() - 25);
            int y = (int)(p.getY() + NODE_IMG_SIZE*0.8);
            String str = "DR " + n.DR + " (id:" + n.id + ")";
            g2.drawString(str, x, y);

        });
    }
    
    private void _drawLinks(Graphics2D g2) {
        /* Set blue stroke for direct node links */
        g2.setStroke(
            new BasicStroke(2, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL)
        );
        g2.setColor(Color.BLUE);
        
        /* Draw direct links */
        gui_gateways.entrySet().forEach(entry_g -> {
            gui_nodes.entrySet().forEach(entry_n -> {
                LoRaMarkovModel model = simulator.getLinkModel(entry_g.getKey(), entry_n.getKey());
                if (model == null) return;
                
                Point p1 = entry_g.getValue(),
                      p2 = entry_n.getValue();
                g2.drawLine((int)p1.getX(), (int)p1.getY(), (int)p2.getX(), (int)p2.getY());
                
                int x = (int)(p1.getX() + p2.getX()) / 2,
                    y = (int)(p1.getY() + p2.getY()) / 2;
                String str1 = "Dist: " + model.distance_m + "m",
                       str2 = "DR: " + model.DR;
                g2.drawString(str1, x - 40, y);
                g2.drawString(str2, x - 20, y + 20);
            });
            
        });
        
        /* Set thin red dashed stroke for interference links */
        g2.setStroke(
            new BasicStroke(1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 0, new float[]{10}, 0)
        );
        g2.setColor(Color.RED);
        
        /* Draw interference links */
        ArrayList<Entry<LoRaNode, Point>> nodes = new ArrayList<>();
        gui_nodes.entrySet().forEach(x -> {
            nodes.add(x);
        });
        
        for (int i = 0; i < nodes.size(); i++) {
            for (int j = i + 1; j < nodes.size(); j++) {
                LoRaMarkovModel model = simulator.getLinkModel(nodes.get(i).getKey(), nodes.get(j).getKey());
                if (model == null) continue;
                
                Point p1 = nodes.get(i).getValue(),
                      p2 = nodes.get(j).getValue();
                g2.drawLine((int)p1.getX(), (int)p1.getY(), (int)p2.getX(), (int)p2.getY());
                
                int x = (int)(p1.getX() + p2.getX()) / 2,
                    y = (int)(p1.getY() + p2.getY()) / 2;
                String str1 = "Dist: " + model.distance_m + "m",
                       str2 = "DR: " + model.DR;
                g2.drawString(str1, x - 40, y);
                g2.drawString(str2, x - 20, y + 20);
            }
        }
    }
    
    public void clearAll() {
        gui_nodes.clear();
        gui_gateways.clear();
        this.repaint();
    }
    
    public float calcDistance(LoRaNode n1, LoRaNode n2) {
        float scale = SimConfig.getInstance().max_node_distance_m / this.getWidth();
        Point p1 = n1 instanceof LoRaGateway ? gui_gateways.get(n1) : gui_nodes.get(n1),
              p2 = n2 instanceof LoRaGateway ? gui_gateways.get(n2) : gui_nodes.get(n2);
        
        return (float)Math.sqrt(
            Math.pow(
                (float)p1.getX() - (float)p2.getX(), 2
            ) + Math.pow(
                (float)p1.getY() - p2.getY(), 2
            )
        ) * scale;
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
