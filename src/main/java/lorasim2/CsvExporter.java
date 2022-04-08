package lorasim2;

import java.io.FileWriter;
import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author alex
 */
public class CsvExporter {
    private final SimulationStats dataset;
    
    public CsvExporter(SimulationStats dataset) {
        this.dataset = dataset;
    }
    
    /**
     * Export node receptions to show superpositions and success/failure
     * @param fname_pattern Filename pattern ("{id}" = node ID)
     */
    public void exportReceptions(String fname_pattern) {
        HashMap<LoRaNode, ArrayList<LoRaPacket>> rx_data = SimStatsUtils.getReceptionsByNode(dataset);
        
        rx_data.forEach((dst_node, packets) -> {
            try {
                String f_name = fname_pattern.replaceAll("\\{id\\}", String.valueOf(dst_node.id));
                FileWriter writer = new FileWriter(f_name);
                
                writer.write("src_node_id,start_time_ms,end_time_ms,payload_len,succeeded\n");
                
                for (LoRaPacket pkt : packets)
                    writer.write(
                        String.valueOf(pkt.src.id) + ',' +
                        String.valueOf(pkt.start_ms) + ',' +
                        String.valueOf(pkt.end_ms) + ',' +
                        String.valueOf(pkt.payload_size) + ',' +
                        (pkt.successful ? '1' : '0') + '\n'
                    );
                
                writer.flush();
                writer.close();
            } catch (IOException ex) {
                System.err.println("[CSV-Exporter] Exception during export: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
   }
    
    /**
     * Export nodes throughput to the given CSV files.
     * @param fname_pattern Filename pattern ("{id}" = node ID)
     */
    public void exportThroughput(String fname_pattern) {
        HashMap<LoRaNode, ArrayList<SimpleEntry<Float, Float>>> thr_map = SimStatsUtils.getNodeThroughputs(dataset);
        
        thr_map.forEach((src_node, thr_data) -> {
            try {
                String f_name = fname_pattern.replaceAll("\\{id\\}", String.valueOf(src_node.id));
                FileWriter writer = new FileWriter(f_name);
                
                writer.write("time_ms,thr_bps\n");
                
                for (SimpleEntry entry : thr_data)
                    writer.write(String.valueOf(entry.getKey()) + ',' + String.valueOf(entry.getValue()) + '\n');
                
                writer.flush();
                writer.close();
            } catch (IOException ex) {
                System.err.println("[CSV-Exporter] Exception during export: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
    }
}
