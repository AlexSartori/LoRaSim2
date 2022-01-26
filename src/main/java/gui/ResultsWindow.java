package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import lorasim2.LoRaNode;
import lorasim2.SimulationResults;
import lorasim2.SimulationResults.Packet;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.style.markers.SeriesMarkers;

/**
 *
 * @author alex
 */
public class ResultsWindow extends JFrame {
    final int CHART_WIDTH = 500;
    JScrollPane scrollPane;
    JPanel contentPanel;
    GridBagConstraints gbc;
    
    public ResultsWindow() {
        /* Init frame ------------------------------------------------------- */
        super("Simulation Results");
        setMinimumSize(new Dimension(CHART_WIDTH + 30, 400));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        /* Panel with main layout ------------------------------------------- */
        contentPanel = new JPanel();
        contentPanel.setLayout(new GridBagLayout());
        
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.PAGE_START;
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.gridx = 0;
        gbc.gridy = 0;
        
        /* Scroll pane ------------------------------------------------------ */
        scrollPane = new JScrollPane(contentPanel);
        scrollPane.setHorizontalScrollBarPolicy(scrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(scrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        this.add(scrollPane);
    }
    
    public void showResults(SimulationResults r) {
        HashMap<LoRaNode, ArrayList<Packet>> rx_data = new HashMap<>();

        for (Entry<LoRaNode, ArrayList<Packet>> row : r.getTransmissions().entrySet()) {
            for (Packet p : row.getValue()) {
                if (!rx_data.containsKey(p.dst))
                    rx_data.put(p.dst, new ArrayList<>());
                rx_data.get(p.dst).add(p);
            }
        }
        
        for (Entry<LoRaNode, ArrayList<Packet>> row : rx_data.entrySet()) {
            XYChart c = createNodeReceptionsPlot(row.getKey(), row.getValue());
            JPanel p = new XChartPanel(c);
            
            gbc.gridy++;
            contentPanel.add(p, gbc);
        }
        
        this.setVisible(true);
    }
    
    private XYChart createNodeReceptionsPlot(LoRaNode n, ArrayList<Packet> data) {
        XYChart chart = new XYChartBuilder()
            .title("Node #" + n.id + " RX data").width(CHART_WIDTH).height(300)
            .xAxisTitle("Time (ms)").yAxisTitle("Source node ID")
            .build();
        
        int pkt_id = 0;
        for (Packet pkt : data) {
            chart.addSeries(
                "Node #" + n.id + " - Packet #" + pkt_id++,
                new float[]{ pkt.start_ms, pkt.end_ms },
                new float[]{ pkt.src.id, pkt.src.id }
            ).setMarker(SeriesMarkers.CIRCLE).setMarkerColor(pkt.successful ? Color.green : Color.red)
            .setLineWidth(3).setLineColor(pkt.successful ? Color.green : Color.red);
        }
        
        chart.getStyler().setLegendVisible(false);
        return chart;
    }
    
    /*private void testChart() {
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
            
            gbc.gridy++;
            contentPanel.add(chartPanel, gbc);
        }
    }*/
}
