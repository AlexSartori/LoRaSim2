package lorasim2;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author alex
 */
public class SimulationStats {
    
    public class Packet {
        public LoRaNode src, dst;
        public float start_ms, end_ms;
        public boolean successful;

        public Packet(LoRaNode src, LoRaNode dst, float start, float end, boolean succ) {
            this.src = src;
            this.dst = dst;
            start_ms = start;
            end_ms = end;
            successful = succ;
        }
    }
    
    private HashMap<LoRaNode, ArrayList<Packet>> tx_data;
    private HashMap<LoRaNode, Float> tx_start_times;
    
    public SimulationStats() {
        tx_data = new HashMap<>();
        tx_start_times = new HashMap<>();
    }
    
    public HashMap<LoRaNode, ArrayList<Packet>> getTransmissions() {
        return tx_data;
    }
    
    public void beginTransmission(LoRaLink link, float time_ms) {
        tx_start_times.put(link.src, time_ms);
    }
    
    public void endTransmission(LoRaLink link, float time_ms, boolean succ) {
        if (!tx_data.containsKey(link.src))
            tx_data.put(link.src, new ArrayList<>());
        
        float start = tx_start_times.get(link.src);
        tx_data.get(link.src).add(
            new Packet(link.src, link.dst, start, time_ms, succ)
        );
    }
    
    public float getRXSuperposition(LoRaNode n, float start_ms, float end_ms) {
        HashMap<Packet, Float> packets = new HashMap<>();
        
        for (LoRaNode src : tx_data.keySet()) {
            if (src == n)  continue;
            
            for (Packet p : tx_data.get(src)) {
                if (p.dst != n || p.end_ms < start_ms) continue;
                
                if (p.start_ms >= start_ms || p.end_ms <= end_ms) {
                    float superposition = Math.min(p.end_ms, end_ms) - Math.max(p.start_ms, start_ms);
                    superposition /= end_ms - start_ms;
                    packets.put(p, superposition);
                }
            }
        }
        
        System.out.println("RX to node #" + n.id + " overlaps with " + packets.size() + " other packets");
        
        float max_overlap = 0;
        for (float s : packets.values())
            if (s > max_overlap)
                max_overlap = s;
        
        System.out.println("   -> Max overlap is " + (int)(max_overlap*100) + "%");
        return max_overlap;
    }
}
