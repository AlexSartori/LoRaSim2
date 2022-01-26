package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
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
import lorasim2.SimulationResults;
import lorasim2.Simulator;

/**
 * @author alex
 */
public class MainWindow extends javax.swing.JFrame implements ActionListener {
    private Simulator sim;
    private final SimConfigDialog sim_config;
    private JToolBar toolbar;
    private CanvasPanel canvas;
    private ResultsWindow res_window;
    
    public MainWindow() {
        super();
        sim = new Simulator();
        sim_config = new SimConfigDialog(this);
        this.initUI();
    }
    
    private void initUI() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1200, 600);
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
        canvas = new CanvasPanel(this.sim, this.sim_config);
        this.add(canvas, BorderLayout.CENTER);
        
        /* Results Panel ---------------------------------------------------- */
        // res_panel = new ResultsWindow();
        // res_panel.setMinimumSize(new Dimension(500, 800));
        // this.add(res_panel, BorderLayout.EAST);
    }
    
    private void _createSimulation(int n_nodes, int n_gateways) {
        ArrayList<LoRaNode> nodes = new ArrayList<>();
        ArrayList<LoRaGateway> gateways = new ArrayList<>();
        Random rng = new Random();
        SimConfig sim_conf = new SimConfig(
            sim_config.getSimDuration(),
            sim_config.getMaxTXDelay(),
            sim_config.getPayloadSize()
        );
        sim.resetSimulation();
        
        for (int i = 0; i < n_nodes; i++) {
            int dr = rng.nextInt(4) * 2;
            LoRaNode n = new LoRaNode(dr);
            nodes.add(n);
            sim.addNode(n);
            canvas.randomlyPlaceNewNode(n);
        }
        for (int i = 0; i < n_gateways; i++) {
            LoRaGateway g = new LoRaGateway();
            gateways.add(g);
            sim.addGateway(g);
            canvas.randomlyPlaceNewGateway(g);
        }
        
        gateways.forEach(gw -> {
            nodes.forEach(n -> {
                try {
                    LoRaMarkovModel model = LoRaModelFactory.getLinkModel(gw, n, canvas.calcDistance(gw, n));
                    if (model != null)
                        sim.setLinkModel(gw, n, model);
                    else
                        System.out.println("[Main]: Warning: no model found for nodes: " + gw.id + " --> " + n.id);
                } catch (Exception e) {
                    System.err.println("[Main]: Failed to set model for link: " + gw.id + " <--> " + n.id);
                    e.printStackTrace();
                }
            });
        });
        
        nodes.forEach(n1 -> {
            nodes.forEach(n2 ->{
                if (n1 == n2)
                    return;
                
                try {
                    LoRaMarkovModel model = LoRaModelFactory.getLinkModel(n1, n2, canvas.calcDistance(n1, n2));
                    if (model != null)
                        sim.setLinkModel(n1, n2, model);
                    else
                        System.out.println("[Main]: Warning: no model found for nodes: " + n1.id + " --> " + n2.id);
                } catch (Exception e) {
                    System.err.println("[Main]: Failed to set model for link: " + n1.id + " <--> " + n2.id);
                }
            });
        });
        
        SimulationResults res = sim.runSimulation(sim_conf);
        res_window = new ResultsWindow();
        res_window.plot(res);
        res_window.setVisible(true);
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
                System.err.println("[Main]: Unexpected action command in CanvasWindow listener: " + cmd);
                break;
        }
    }
}
