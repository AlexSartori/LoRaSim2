package lorasim2;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author alex
 */
public class SimulationResults {
    
    public class Packet {
        public float start_ms, end_ms;
        public boolean successful;

        public Packet(float start, float end, boolean succ) {
            start_ms = start;
            end_ms = end;
            successful = succ;
        }
    }
    
    private HashMap<LoRaNode, ArrayList<Packet>> tx_data, rx_data;
    private HashMap<LoRaNode, Float> open_transmissions, open_receptions;
    
    public SimulationResults() {
        tx_data = new HashMap<>();
        rx_data = new HashMap<>();
        open_transmissions = new HashMap<>();
        open_receptions = new HashMap<>();
    }
    
    public HashMap<LoRaNode, ArrayList<Packet>> getTransmissions() {
        return tx_data;
    }
    
    public HashMap<LoRaNode, ArrayList<Packet>> getReceptions() {
        return rx_data;
    }
    
    public void begin_tx(LoRaNode n, float time_ms) {
        open_transmissions.put(n, time_ms);
    }
    
    public void end_tx(LoRaNode n, float time_ms, boolean succ) {
        if (!tx_data.containsKey(n))
            tx_data.put(n, new ArrayList<>());
        
        float start = open_transmissions.remove(n);
        tx_data.get(n).add(
            new Packet(start, time_ms, succ)
        );
    }
    
    public void begin_rx(LoRaNode n, float time_ms) {
        open_receptions.put(n, time_ms);
    }
    
    public void end_rx(LoRaNode n, float time_ms, boolean succ) {
        if (!rx_data.containsKey(n))
            rx_data.put(n, new ArrayList<>());
        
        if (open_receptions.containsKey(n)) {
            float start = open_receptions.remove(n);
            rx_data.get(n).add(
                new Packet(start, time_ms, succ)
            );
        }
    }
    
}