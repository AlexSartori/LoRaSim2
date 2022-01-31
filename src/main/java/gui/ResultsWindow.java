package gui;

import java.awt.BasicStroke;
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
import lorasim2.LoRaPacket;
import lorasim2.SimulationStats;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.style.markers.SeriesMarkers;

/**
 *
 * @author alex
 */
public class ResultsWindow extends JFrame {
    private int CHART_WIDTH = 800;
    private JScrollPane scrollPane;
    private JPanel contentPanel;
    private GridBagConstraints gbc;
    
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
    
    public void showResults(SimulationStats r) {
        HashMap<LoRaNode, ArrayList<LoRaPacket>> rx_data = new HashMap<>();

        for (Entry<LoRaNode, ArrayList<LoRaPacket>> row : r.getTransmissions().entrySet()) {
            for (LoRaPacket p : row.getValue()) {
                if (!rx_data.containsKey(p.dst))
                    rx_data.put(p.dst, new ArrayList<>());
                rx_data.get(p.dst).add(p);
            }
        }
        
        for (Entry<LoRaNode, ArrayList<LoRaPacket>> row : rx_data.entrySet()) {
            XYChart c = createNodeReceptionsPlot(row.getKey(), row.getValue());
            JPanel p = new XChartPanel(c);
            
            gbc.gridy++;
            contentPanel.add(p, gbc);
        }
        
        this.setVisible(true);
    }
    
    private XYChart createNodeReceptionsPlot(LoRaNode n, ArrayList<LoRaPacket> data) {
        XYChart chart = new XYChartBuilder()
            .title("Node #" + n.id + " RX data").width(CHART_WIDTH).height(300)
            .xAxisTitle("Time (ms)").yAxisTitle("Source node ID")
            .build();
        
        int pkt_id = 0;
        for (LoRaPacket pkt : data) {
            chart.addSeries(
                "Node #" + n.id + " - Packet #" + pkt_id++,
                new float[]{ pkt.start_ms, pkt.end_ms },
                new float[]{ pkt.src.id, pkt.src.id }
            ).setMarker(SeriesMarkers.CIRCLE).setMarkerColor(pkt.successful ? Color.green : Color.red)
            .setLineWidth(3).setLineColor(pkt.successful ? Color.green : Color.red).setLineStyle(new BasicStroke());
        }
        
        chart.getStyler().setLegendVisible(false);
        return chart;
    }
}
