/*
 */
package gui;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;

/**
 *
 * @author alex
 */
public class CanvasWindow extends javax.swing.JFrame {
    public CanvasWindow() {
        super();
        this.initUI();
    }
    
    private void initUI() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 600);
        this.setTitle("LoRaSim2");
        this.setLayout(new BorderLayout());
        
        /* Toolbar ---------------------------------------------------------- */
        JPanel tbPanel = new JPanel();
        tbPanel.add(new JLabel("Henlo"));
        
        JToolBar toolbar = new JToolBar();
        toolbar.add(tbPanel);
        
        this.add(toolbar, BorderLayout.NORTH);
        
        /* Canvas ----------------------------------------------------------- */
        CanvasPanel canvas = new CanvasPanel();
        this.add(canvas, BorderLayout.CENTER);
    }
}
