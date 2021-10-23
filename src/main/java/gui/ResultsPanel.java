package gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;

/**
 *
 * @author alex
 */
public class ResultsPanel extends JScrollPane {
    JPanel contentPanel;
    GridBagConstraints gbc;
    
    public ResultsPanel() {
        super();
        setBorder(BorderFactory.createTitledBorder("Simulation Results"));
        setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
        setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);
        getVerticalScrollBar().setUnitIncrement(16);
        
        contentPanel = new JPanel();
        contentPanel.setMaximumSize(new Dimension(500, 1000));
        // contentPanel.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        contentPanel.setLayout(new GridBagLayout());
        this.add(contentPanel);
        
        gbc = new GridBagConstraints();
        // gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.PAGE_START;
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.gridx = 0;
        gbc.gridy = 0;
        
        JLabel l = new JLabel("Run a simulation to plot here the results");
        contentPanel.add(l, gbc);
        
        this.setViewportView(contentPanel);
        
        testChart();
    }
    
    private void testChart() {
        for (int i = 0; i < 10; i++) {
            XYChart chart = new XYChartBuilder().title("Test Chart").width(500).height(300).build();
            chart.addSeries(
                "Series 1",
                new int[] {0, 1, 2, 3, 7, 15, 17, 21},
                new int[] {-3, 7, 6, 3, -5, 18, 21, 15}
            );
            chart.getStyler().setLegendVisible(false);

            JPanel chartPanel = new XChartPanel<>(chart);
            //chartPanel.setSize(300, 150);
            gbc.gridy++;
            contentPanel.add(chartPanel, gbc);
        }
    }
}
