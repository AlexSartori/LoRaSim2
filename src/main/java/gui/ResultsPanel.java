package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import lorasim2.LoRaNode;
import lorasim2.SimulationResults;
import lorasim2.SimulationResults.Packet;
import org.knowm.xchart.HeatMapChart;
import org.knowm.xchart.HeatMapChartBuilder;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.internal.chartpart.RenderableSeries;
import org.knowm.xchart.internal.series.MarkerSeries;
import org.knowm.xchart.style.markers.Marker;
import org.knowm.xchart.style.markers.SeriesMarkers;

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
        contentPanel.setMinimumSize(new Dimension(500, 100));
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
    }
    
    @Override
    public Dimension getMinimumSize() {
      return new Dimension(500, 100);
    }
    
    @Override
    public Dimension getPreferredSize() {
      return new Dimension(500, 800);
    }

    public void plot(SimulationResults r) {
        HashMap<LoRaNode, ArrayList<Packet>> data = r.getTransmissions();
        XYChart transmissions = new XYChartBuilder()
            .title("Transmissions").width(470).height(300)
            .xAxisTitle("Time (ms)").yAxisTitle("Node ID")
            .build();
        
        for (Entry<LoRaNode, ArrayList<Packet>> row : data.entrySet()) {
            int id = row.getKey().id;
            int pkt_id = 0;
            
            for (Packet p : row.getValue()) {
                transmissions.addSeries(
                    "Node #" + id + " - Packet #" + pkt_id++,
                    new float[]{ p.start_ms, p.end_ms },
                    new float[]{ id, id }
                ).setMarker(SeriesMarkers.CIRCLE).setMarkerColor(p.successful ? Color.green : Color.red)
                .setLineWidth(3).setLineColor(p.successful ? Color.green : Color.red);
            }
        }
        
        transmissions.getStyler().setLegendVisible(false);
        JPanel chartPanel = new XChartPanel<>(transmissions);

        gbc.gridy++;
        contentPanel.add(chartPanel, gbc);
        this.setViewportView(contentPanel);
    }
    
    private void testChart() {
        for (int i = 0; i < 2; i++) {
            XYChart chart = new XYChartBuilder().title("Test Chart").width(500).height(300).build();
            chart.addSeries(
                "Series 1",
                new int[] {0, 1},
                new int[] {1, 1}
            ).setMarkerColor(Color.green).setMarker(SeriesMarkers.CIRCLE).setLineWidth(3).setLineColor(Color.green);
            chart.addSeries("Series 2",
                new double[] {4.2f, 6},
                new double[] {1, 1}
            ).setMarkerColor(Color.green).setMarker(SeriesMarkers.CIRCLE).setLineWidth(3).setLineColor(Color.green);
            chart.addSeries("Series 3",
                new double[] {2, 3},
                new double[] {1, 1}
            ).setMarkerColor(Color.red).setMarker(SeriesMarkers.CIRCLE).setLineWidth(3).setLineColor(Color.red);
            chart.getStyler().setLegendVisible(false);

            JPanel chartPanel = new XChartPanel<>(chart);
            
            //chartPanel.setSize(300, 150);
            gbc.gridy++;
            contentPanel.add(chartPanel, gbc);
        }
    }
}
