package lorasim2;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author alex
 */
public class SimStatsUtils {
    /**
     * Create a map that associates to each node a list of timestamp/throughput pairs
     */
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
                time_to_thr.put(pkt.end_ms, thr); // TODO: this should be a touple
            }
            
            result.put(src_node, time_to_thr);
        });
        
        return result;
    }
    
    /**
     * Create a map that associates to each node an array of the received packets
     */
    public static HashMap<LoRaNode, ArrayList<LoRaPacket>> getReceptionsByNode(SimulationStats stats) {
        HashMap<LoRaNode, ArrayList<LoRaPacket>> result = new HashMap<>();
        
        stats.getTransmissions().forEach((src_node, packets) -> {
            packets.forEach((pkt) -> {
                if (!result.containsKey(pkt.dst))
                    result.put(pkt.dst, new ArrayList<LoRaPacket>());
                result.get(pkt.dst).add(pkt);
            });
        });
        
        return result;
    }
}
