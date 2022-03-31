package lorasim2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

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
     * @param fname_pattern Filename patter ("{id}" = node ID)
     */
    public void exportReceptions(String fname_pattern) {
        
    }
    
    /**
     * Export node throughputs on the given CSV files.
     * @param fname_pattern Filename pattern ("{id}" = node ID)
     */
    public void exportThroughput(String fname_pattern) {
        HashMap<LoRaNode, HashMap<Float, Float>> thr_map = SimStatsUtils.getNodeThroughputs(dataset);
        
        thr_map.forEach((src_node, thr_data) -> {
            try {
                String f_name = fname_pattern.replaceAll("\\{id\\}", String.valueOf(src_node.id));
                System.out.println("[CSV] Exporting to: " + f_name);
                FileWriter writer = new FileWriter(f_name);
                
                writer.write("time_ms,thr_bps\n");
                
                thr_data.forEach((timestamp, thr) -> {
                    writer.write(String.valueOf(timestamp) + ',' + String.valueOf(thr) + '\n');
                });
                
                writer.flush();
                writer.close();
            } catch (IOException ex) {
                System.err.println("[CSV-Exporter] Exception during export: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
    }
}
