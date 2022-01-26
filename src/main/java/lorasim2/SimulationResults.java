package lorasim2;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author alex
 */
public class SimulationResults {
    
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
    
    public SimulationResults() {
        tx_data = new HashMap<>();
        tx_start_times = new HashMap<>();
    }
    
    public HashMap<LoRaNode, ArrayList<Packet>> getTransmissions() {
        return tx_data;
    }
    
    public void begin_transmission(LoRaLink link, float time_ms) {
        tx_start_times.put(link.n1, time_ms);
    }
    
    public void end_transmission(LoRaLink link, float time_ms, boolean succ) {
        if (!tx_data.containsKey(link.n1))
            tx_data.put(link.n1, new ArrayList<>());
        
        float start = tx_start_times.get(link.n1);
        tx_data.get(link.n1).add(
            new Packet(link.n1, link.n2, start, time_ms, succ)
        );
    }
    
}
