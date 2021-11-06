package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;

/**
 * @author alex
 */
public class CanvasWindow extends javax.swing.JFrame implements ActionListener {
    private JToolBar toolbar;
    private CanvasPanel canvas;
    private ResultsPanel res_panel;
    private SimConfigDialog sim_config;
    
    public CanvasWindow() {
        super();
        this.initUI();
        sim_config = new SimConfigDialog(this);
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
        canvas = new CanvasPanel();
        this.add(canvas, BorderLayout.CENTER);
        
        /* Results Panel ---------------------------------------------------- */
        res_panel = new ResultsPanel();
        this.add(res_panel, BorderLayout.EAST);
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
                
                for (int i = 0; i < sim_config.getNumOfNodes(); i++)
                    canvas.addRandomNode();
                for (int i = 0; i < sim_config.getNumOfGateways(); i++)
                    canvas.addRandomNode();
                
                break;
            default:
                System.err.println("Unexpected action command in CanvasWindow listener: " + cmd);
                break;
        }
    }
}
