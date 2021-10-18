package gui;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author alex
 */
public class ResultsPanel extends JPanel {
    public ResultsPanel() {
        super();
        
        this.setBorder(BorderFactory.createTitledBorder("Simulation Results"));
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        JLabel l = new JLabel("Run a simulation to plot here the results");
        this.add(l);
    }
}
