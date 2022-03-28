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
    
    public CanvasPanel(Simulator sim) {
        super();
        this.simulator = sim;
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
    
    private Point scalePointToUI(Point p) {
        float max_ui_size = Integer.min(this.getWidth(), this.getHeight()) - (NODE_IMG_SIZE * 2);
        float scale = SimConfig.getInstance().max_node_distance_m / max_ui_size;
        return new Point(
            (int)(p.getX() / scale),
            (int)(p.getY() / scale)
        );
    }
    
    private void _drawNodes(Graphics2D g2) {
        g2.setColor(Color.BLACK);
        
        simulator.getGateways().forEach(g -> {
            Point p = simulator.getNodeLocation(g);
            p = scalePointToUI(p);
            
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
            String str = "Gateway (id:" + g.id + ")";
            g2.drawString(str, x, y);
        });
        
        simulator.getNodes().forEach(n -> {
            Point p = simulator.getNodeLocation(n);
            p = scalePointToUI(p);
            
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
        g2.setStroke(
            new BasicStroke(2, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL)
        );
        g2.setColor(Color.BLUE);
        
        /* Draw direct links */
        simulator.getGateways().forEach(g -> {
            simulator.getNodes().forEach(n -> {
                LoRaMarkovModel model = simulator.getLinkModel(g, n);
                if (model == null) return;
                
                Point p1 = scalePointToUI(simulator.getNodeLocation(g)),
                      p2 = scalePointToUI(simulator.getNodeLocation(n));
                g2.drawLine((int)p1.getX(), (int)p1.getY(), (int)p2.getX(), (int)p2.getY());
                
                int x = (int)(p1.getX() + p2.getX()) / 2,
                    y = (int)(p1.getY() + p2.getY()) / 2;
                String str1 = "Dist: " + model.distance_m + "m",
                       str2 = "DR: " + model.DR;
                g2.drawString(str1, x - 40, y);
                g2.drawString(str2, x - 20, y + 20);
            });
            
        });
    }
}
