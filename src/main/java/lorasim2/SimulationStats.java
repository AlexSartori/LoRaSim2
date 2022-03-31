package lorasim2;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author alex
 */
public class SimulationStats {
    private HashMap<LoRaNode, ArrayList<LoRaPacket>> tx_data;
    private HashMap<LoRaNode, Float> tx_start_times;
    
    public SimulationStats() {
        tx_data = new HashMap<>();
        tx_start_times = new HashMap<>();
    }
    
    public HashMap<LoRaNode, ArrayList<LoRaPacket>> getTransmissions() {
        return tx_data;
    }
    
    public void beginTransmission(LoRaLink link, float time_ms) {
        tx_start_times.put(link.src, time_ms);
    }
    
    public void endTransmission(LoRaLink link, float time_ms, boolean succ, int payload_size) {
        if (!tx_data.containsKey(link.src))
            tx_data.put(link.src, new ArrayList<>());
        
        float start = tx_start_times.get(link.src);
        tx_data.get(link.src).add(
            new LoRaPacket(link.src, link.dst, start, time_ms, succ, payload_size)
        );
    }
    
    public float getRXSuperposition(LoRaNode n, float start_ms, float end_ms) {
        HashMap<LoRaPacket, Float> packets = new HashMap<>();
        
        for (LoRaNode src : tx_data.keySet()) {
            if (src == n)  continue;
            
            for (LoRaPacket p : tx_data.get(src)) {
                if (p.dst != n || p.end_ms < start_ms) continue;
                
                if (p.start_ms >= start_ms || p.end_ms <= end_ms) {
                    float superposition = Math.min(p.end_ms, end_ms) - Math.max(p.start_ms, start_ms);
                    superposition /= end_ms - start_ms;
                    packets.put(p, superposition);
                }
            }
        }
        
        float max_overlap = 0;
        for (float s : packets.values())
            if (s > max_overlap)
                max_overlap = s;
        
        return max_overlap;
    }
}
