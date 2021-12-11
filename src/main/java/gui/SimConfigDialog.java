package gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

/**
 * @author alex
 */
public class SimConfigDialog extends JDialog implements ActionListener {
    private MainWindow parent;
    private JSpinner n_gateways, n_nodes, sim_duration, gui_scale, payload_size, tx_rnd_delay;
     
    public SimConfigDialog(MainWindow parent) {
        super();
        this.initUI();
        this.parent = parent;
    }
    
    private void initUI() {
        this.setTitle("Configure Simulation");
        this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        this.setLayout(new GridBagLayout());
        this.setSize(600, 400);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.ipadx = gbc.ipady = 5;
        gbc.gridx = gbc.gridy = 0;

        this.add(new JLabel("Simulation duration (s):"), gbc);
        gbc.gridx++;
        sim_duration = new JSpinner(new SpinnerNumberModel(600, 0, Integer.MAX_VALUE, 1));
        this.add(sim_duration, gbc);
        
        gbc.gridy++;
        gbc.gridx = 0;
        this.add(new JLabel("Number of gateways:"), gbc);
        gbc.gridx++;
        n_gateways = new JSpinner(new SpinnerNumberModel(1, 1, 5, 1));
        this.add(n_gateways, gbc);
        
        gbc.gridy++;
        gbc.gridx = 0;
        this.add(new JLabel("Number of nodes:"), gbc);
        gbc.gridx++;
        n_nodes = new JSpinner(new SpinnerNumberModel(5, 1, 10, 1));
        this.add(n_nodes, gbc);
        
        gbc.gridy++;
        gbc.gridx = 0;
        this.add(new JLabel("Payload size (bytes):"), gbc);
        gbc.gridx++;
        payload_size = new JSpinner(new SpinnerNumberModel(8, 1, 64, 1));
        this.add(payload_size, gbc);
        
        gbc.gridy++;
        gbc.gridx = 0;
        this.add(new JLabel("Interface scale (1 pixel = ? meters)"), gbc);
        gbc.gridx++;
        gui_scale = new JSpinner(new SpinnerNumberModel(2, 0, 50, 1));
        this.add(gui_scale, gbc);
        
        gbc.gridy++;
        gbc.gridx = 0;
        this.add(new JLabel("Max. random TX delay (ms)"), gbc);
        gbc.gridx++;
        tx_rnd_delay = new JSpinner(new SpinnerNumberModel(200, 0, 500, 1));
        this.add(tx_rnd_delay, gbc);
        
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JButton btn_ok = new JButton("Ok");
        btn_ok.addActionListener(this);
        this.add(btn_ok, gbc);
    }
    
    public int getSimDuration() {
        return (int)sim_duration.getValue();
    }
    
    public int getNumOfGateways() {
        return (int)n_gateways.getValue();
    }
    
    public int getNumOfNodes() {
        return (int)n_nodes.getValue();
    }
    
    public int getPayloadSize() {
        return (int)payload_size.getValue();
    }
    
    public int getGuiScale() {
        return (int)gui_scale.getValue();
    }
    
    public int getMaxTXDelay() {
        return (int)tx_rnd_delay.getValue();
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand() == "Ok") {
            this.setVisible(false);
            parent.actionPerformed(
                new ActionEvent(this, 0, "conf-sim-ok")
            );
        }
    }
}
