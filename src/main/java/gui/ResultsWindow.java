package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import lorasim2.LoRaNode;
import lorasim2.LoRaPacket;
import lorasim2.SimStatsUtils;
import lorasim2.SimulationStats;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.style.AxesChartStyler;
import org.knowm.xchart.style.markers.SeriesMarkers;

/**
 *
 * @author alex
 */
public class ResultsWindow extends JFrame {
    private int CHART_WIDTH = 600;
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
        HashMap<LoRaNode, ArrayList<LoRaPacket>> rx_data = SimStatsUtils.getReceptionsByNode(r);
        
        for (Entry<LoRaNode, ArrayList<LoRaPacket>> row : rx_data.entrySet()) {
            XYChart c1 = createNodeReceptionsPlot(row.getKey(), row.getValue());
            JPanel p1 = new XChartPanel(c1);
            
            XYChart c2 = createThroughputChart(row.getKey(), row.getValue());
            JPanel p2 = new XChartPanel(c2);
            
            gbc.gridx = 0;
            contentPanel.add(p1, gbc);
            gbc.gridx++;
            contentPanel.add(p2, gbc);
            gbc.gridy++;

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
                "Packet #" + pkt_id++,
                new float[]{ pkt.start_ms, pkt.end_ms },
                new float[]{ pkt.src.id, pkt.src.id }
            ).setMarker(SeriesMarkers.CIRCLE).setMarkerColor(pkt.successful ? Color.green : Color.red)
            .setLineWidth(3).setLineColor(pkt.successful ? Color.green : Color.red).setLineStyle(new BasicStroke());
        }
        
        chart.getStyler().setCursorEnabled(true).setLegendVisible(false);
        ((AxesChartStyler)chart.getStyler()).setXAxisMin(0.0);
        return chart;
    }
    
    private XYChart createThroughputChart(LoRaNode n, ArrayList<LoRaPacket> data) {
        XYChart chart = new XYChartBuilder()
            .title("Node #" + n.id + " Average throughput").width(CHART_WIDTH).height(300)
            .xAxisTitle("Time (ms)").yAxisTitle("Throughput (byte/s)")
            .build();
        
        int bytes_transmitted = 0;
        float tot_time = 0;
        ArrayList<Float> y_vals = new ArrayList<>(),
                         x_vals = new ArrayList<>();
        
        data.sort((var a, var b) -> (int)(a.end_ms - b.end_ms));
        float y_min = -1, y_max = -1;
        
        for (LoRaPacket pkt : data) {
            if (pkt.successful)
                bytes_transmitted += pkt.payload_size;
            tot_time += pkt.end_ms - pkt.start_ms;
            float y = bytes_transmitted/tot_time*1000;
            x_vals.add(pkt.end_ms);
            y_vals.add(y);
            if (y < y_min || y_min == -1) y_min = y; if(y > y_max) y_max = y;
        }
        chart.addSeries("AVG throughput for Node #" + n.id, x_vals, y_vals)
        .setLineWidth(2);
        
        chart.getStyler().setXAxisMin(0.0).setLegendVisible(false);
        chart.getStyler().setYAxisMin((double)(int)(y_min - 1)).setYAxisMax((double)(int)(y_max + 1));
        return chart;
    }
}
