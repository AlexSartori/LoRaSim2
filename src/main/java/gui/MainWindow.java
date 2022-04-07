package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import lorasim2.SimulationStats;
import lorasim2.Simulator;

/**
 * @author alex
 */
public class MainWindow extends javax.swing.JFrame {
    private Simulator sim;
    private SimulationStats sim_res;
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
        tb_newsim.addActionListener((ae) -> {
            _createSimulation();
        });
        
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
        sim_res = sim.runSimulation();
        
        /* Unlock threads waiting for results */
        synchronized(this) {
            this.notifyAll();
        }
        
        System.out.println("[Main]: Preparing results...");
        res_window = new ResultsWindow();
        res_window.showResults(sim_res);
    }
    
    public SimulationStats getSimResult() {
        return sim_res;
    }
}
