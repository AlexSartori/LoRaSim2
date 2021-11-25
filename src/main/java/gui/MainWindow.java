package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import lorasim2.LoRaGateway;
import lorasim2.LoRaNode;
import lorasim2.Simulator;

/**
 * @author alex
 */
public class MainWindow extends javax.swing.JFrame implements ActionListener {
    private final SimConfigDialog sim_config;
    private final Simulator simulator;
    private JToolBar toolbar;
    private CanvasPanel canvas;
    private ResultsPanel res_panel;
    
    public MainWindow() {
        super();
        simulator = new Simulator();
        sim_config = new SimConfigDialog(this);
        this.initUI();
    }
    
    private void initUI() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 600);
        this.setTitle("LoRaSim2");
        this.setLayout(new BorderLayout());
        
        /* Toolbar ---------------------------------------------------------- */
        JButton tb_newsim = new JButton("Configure Simulation");
        tb_newsim.addActionListener(this);
        
        JPanel tb_panel = new JPanel(new FlowLayout());
        tb_panel.add(tb_newsim);
        
        toolbar = new JToolBar();
        toolbar.add(tb_panel);
        this.add(toolbar, BorderLayout.NORTH);
        
        /* Canvas ----------------------------------------------------------- */
        canvas = new CanvasPanel(this.simulator, this.sim_config);
        this.add(canvas, BorderLayout.CENTER);
        
        /* Results Panel ---------------------------------------------------- */
        res_panel = new ResultsPanel();
        this.add(res_panel, BorderLayout.EAST);
    }
    
    private void _createSimulation(int n_nodes, int n_gateways) {
        ArrayList<LoRaNode> nodes = new ArrayList<>();
        ArrayList<LoRaGateway> gateways = new ArrayList<>();
        
        for (int i = 0; i < n_nodes; i++) {
            LoRaNode n = new LoRaNode(0);
            nodes.add(n);
            simulator.addNode(n);
            canvas.randomlyPlaceNewNode(n);
        }
        for (int i = 0; i < n_gateways; i++) {
            LoRaGateway g = new LoRaGateway();
            gateways.add(g);
            simulator.addNode(g);
            canvas.randomlyPlaceNewGateway(g);
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        String cmd = ae.getActionCommand();
        
        switch (cmd) {
            case "Configure Simulation":
                sim_config.setVisible(true);
                break;
            case "conf-sim-ok":
                canvas.clearAll();
                _createSimulation(sim_config.getNumOfNodes(), sim_config.getNumOfGateways());
                canvas.repaint();
                break;
            default:
                System.err.println("Unexpected action command in CanvasWindow listener: " + cmd);
                break;
        }
    }
}
