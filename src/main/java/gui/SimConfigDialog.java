package gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author alex
 */
public class SimConfigDialog extends JDialog implements ActionListener, ChangeListener {
    private MainWindow parent;
    private JSpinner n_gateways, n_nodes, sim_duration, gui_scale, payload_size;
    private JSlider percent_dr_low, percent_dr_mid, percent_dr_hi;
    private JLabel l_percent_dr_low, l_percent_dr_mid, l_percent_dr_hi;
    
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
        this.add(new JLabel("Percentage of nodes with low DR:"), gbc);
        gbc.gridx++;
        percent_dr_low = new JSlider(0, 100, 50);
        percent_dr_low.addChangeListener(this);
        this.add(percent_dr_low, gbc);
        gbc.gridx++;
        l_percent_dr_low = new JLabel(String.format("%d%%", percent_dr_low.getValue()));
        this.add(l_percent_dr_low, gbc);
        
        gbc.gridy++;
        gbc.gridx = 0;
        this.add(new JLabel("Percentage of nodes with medium DR:"), gbc);
        gbc.gridx++;
        percent_dr_mid = new JSlider(0, 100, 20);
        percent_dr_mid.addChangeListener(this);
        this.add(percent_dr_mid, gbc);
        gbc.gridx++;
        l_percent_dr_mid = new JLabel(String.format("%d%%", percent_dr_mid.getValue()));
        this.add(l_percent_dr_mid, gbc);
        
        gbc.gridy++;
        gbc.gridx = 0;
        this.add(new JLabel("Percentage of nodes with high DR:"), gbc);
        gbc.gridx++;
        percent_dr_hi = new JSlider(0, 100, 30);
        percent_dr_hi.addChangeListener(this);
        this.add(percent_dr_hi, gbc);
        gbc.gridx++;
        l_percent_dr_hi = new JLabel(String.format("%d%%", percent_dr_hi.getValue()));
        this.add(l_percent_dr_hi, gbc);
        
        gbc.gridy++;
        gbc.gridx = 0;
        this.add(new JLabel("Payload size (bytes):"), gbc);
        gbc.gridx++;
        payload_size = new JSpinner(new SpinnerNumberModel(8, 1, 64, 1));
        payload_size.addChangeListener(this);
        this.add(payload_size, gbc);
        
        gbc.gridy++;
        gbc.gridx = 0;
        this.add(new JLabel("Interface scale (1 pixel = ? meters)"), gbc);
        gbc.gridx++;
        gui_scale = new JSpinner(new SpinnerNumberModel(2, 0, 50, 1));
        gui_scale.addChangeListener(this);
        this.add(gui_scale, gbc);
        
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JButton btn_ok = new JButton("Ok");
        btn_ok.addActionListener(this);
        this.add(btn_ok, gbc);
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

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand() == "Ok") {
            this.setVisible(false);
            parent.actionPerformed(
                new ActionEvent(this, 0, "conf-sim-ok")
            );
        }
    }

    @Override
    public void stateChanged(ChangeEvent ce) {
        JSlider source = (JSlider)ce.getSource();
        String txt = String.format("%d%%", source.getValue());
        
        if (source == percent_dr_low)
            l_percent_dr_low.setText(txt);
        else if (source == percent_dr_mid)
            l_percent_dr_mid.setText(txt);
        else if (source == percent_dr_hi)
            l_percent_dr_hi.setText(txt);
        else
            System.err.println("Unknown source in JSlider ChangeListener");
    }
}
