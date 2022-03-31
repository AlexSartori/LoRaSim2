package lorasim2;

import java.util.HashMap;

/**
 * @author alex
 */
public class SimStatsUtils {
    public static HashMap<LoRaNode, HashMap<Float, Float>> getNodeThroughputs(SimulationStats stats) {
        HashMap<LoRaNode, HashMap<Float, Float>> result = new HashMap<>();
        
        stats.getTransmissions().forEach((src_node, packets) -> {
            int transmitted_bytes = 0;
            float elapsed_time_ms = 0;
            HashMap<Float, Float> time_to_thr = new HashMap<>();
            
            for (LoRaPacket pkt : packets) {
                if (pkt.successful)
                    transmitted_bytes += pkt.payload_size;
                elapsed_time_ms += (pkt.end_ms - pkt.start_ms);
                float thr = transmitted_bytes / elapsed_time_ms * 1000;
                time_to_thr.put(pkt.end_ms, thr);
            }
            
            result.put(src_node, time_to_thr);
        });
        
        return result;
    }
    
}
