package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import lorasim2.LoRaGateway;
import lorasim2.LoRaMarkovModel;
import lorasim2.LoRaModelFactory;
import lorasim2.LoRaNode;
import lorasim2.SimConfig;
import lorasim2.SimulationStats;
import lorasim2.Simulator;

/**
 * @author alex
 */
public class MainWindow extends javax.swing.JFrame implements ActionListener {
    private Simulator sim;
    private JToolBar toolbar;
    private CanvasPanel canvas;
    private ResultsWindow res_window;
    
    public MainWindow(Simulator s) {
        super();
        this.sim = s;
        this.initUI();
    }
    
    private void initUI() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1200, 600);
        this.setTitle("LoRaSim2");
        this.setLayout(new BorderLayout());
        
        /* Toolbar ---------------------------------------------------------- */
        JButton tb_newsim = new JButton("Run Simulation");
        tb_newsim.addActionListener(this);
        
        JPanel tb_panel = new JPanel(new FlowLayout());
        tb_panel.add(tb_newsim);
        
        toolbar = new JToolBar();
        toolbar.add(tb_panel);
        this.add(toolbar, BorderLayout.NORTH);
        
        /* Canvas ----------------------------------------------------------- */
        canvas = new CanvasPanel(this.sim);
        this.add(canvas, BorderLayout.CENTER);
    }
    
    private void _createSimulation() {
        SimulationStats res = sim.runSimulation();
        
        System.out.println("[Main]: Preparing results...");
        res_window = new ResultsWindow();
        res_window.showResults(res);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        String cmd = ae.getActionCommand();
        
        switch (cmd) {
            case "Run Simulation":
                _createSimulation();
                canvas.repaint();
                break;
            default:
                System.err.println("[Main]: Unexpected action command in CanvasWindow listener: " + cmd);
                break;
        }
    }
}
