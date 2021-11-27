package lorasim2;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Random;

/**
 * @author alex
 */

enum EventType {
    SIMULATION_START,
    SIMULATION_END,
    TX_START,
    TX_END
};

class SimulationEvent {
    public final LoRaNode node;
    public float event_time;
    public EventType type;
    
    public SimulationEvent(LoRaNode node, float time, EventType event) {
        this.node = node;
        event_time = time;
        type = event;
    }
    
    public static Comparator<SimulationEvent> getComparator() {
        return new Comparator<SimulationEvent>() {
            @Override
            public int compare(SimulationEvent t, SimulationEvent t1) {
                return (int)(t.event_time - t1.event_time);
            }
        };
    }
}

public class Simulator {
    private float time_ms;
    private int payload_size;
    private ArrayList<LoRaNode> nodes;
    private ArrayList<LoRaGateway> gateways;
    private HashMap<LoRaNode, HashMap<LoRaNode, LoRaMarkovModel>> link_models;
    private PriorityQueue<SimulationEvent> events_queue;
    private final Random RNG;
    
    public Simulator() {
        nodes = new ArrayList<>();
        gateways = new ArrayList<>();
        link_models = new HashMap<>();
        events_queue = new PriorityQueue<>(SimulationEvent.getComparator());
        RNG = new Random();
        resetSimulation();
    }
    
    private void _simLog(String msg) {
        System.out.println("[Simulator:" + (int)this.time_ms + "]: " + msg);
    }
    
    public ArrayList<LoRaNode> getNodes() {
        return (ArrayList<LoRaNode>)nodes.clone();
    }
    
    public ArrayList<LoRaGateway> getGateway() {
        return (ArrayList<LoRaGateway>)gateways.clone();
    }
    
    public void setPayloadSize(int ps) {
        this.payload_size = ps;
    }
    
    public void resetSimulation() {
        time_ms = 0;
    }
    
    public void addNode(LoRaNode node) {
        nodes.add(node);
    }
    
    public void addGateway(LoRaGateway gw) {
        gateways.add(gw);
    }
    
    public void setLinkModel(LoRaNode n1, LoRaNode n2, LoRaMarkovModel m) throws Exception {
        if (!nodes.contains(n1) && !gateways.contains(n1))
            throw new Exception("[Simulator]: Unknown node: " + n1.id);
        if (!nodes.contains(n2) && !gateways.contains(n2))
            throw new Exception("[Simulator]: Unknown node: " + n2.id);
        
        if (!link_models.containsKey(n1))
            link_models.put(n1, new HashMap<>());
        
        link_models.get(n1).put(n2, m);
        _simLog("[Simulator]: Added link " + n1.id + " <--> " + n2.id);
    }
    
    public LoRaMarkovModel getLinkModel(LoRaNode n1, LoRaNode n2) {
        if (link_models.containsKey(n1) && link_models.get(n1).containsKey(n2))
            return link_models.get(n1).get(n2);
        return null;
    }
    
    public SimulationResults runSimulation() {
        SimulationResults result = new SimulationResults();
        
        events_queue.add(new SimulationEvent(null, 0, EventType.SIMULATION_START));
        events_queue.add(new SimulationEvent(null, 3E3f, EventType.SIMULATION_END));
        
        while (events_queue.size() > 0) {
            SimulationEvent curr_event = events_queue.remove();
            this.time_ms = curr_event.event_time;
            _simLog("Event: " + curr_event.event_time);
            
            switch (curr_event.type) {
                case SIMULATION_START:
                    _simLog("Simulation started");
                    nodes.forEach(n -> { _scheduleNextTransmission(n); });
                    break;
                case SIMULATION_END:
                    _simLog("Simulation ended");
                    break;
                case TX_START:
                    _simLog("Beginning TX for node #" + curr_event.node.id);
                    _transmitPacket(curr_event.node);
                    break;
                case TX_END:
                    _simLog("Ended TX for node #" + curr_event.node.id);
                    events_queue.clear();
                    break;
            }
        }
        
        return result;
    }
    
    private void _scheduleNextTransmission(LoRaNode n) {
        if (RNG.nextFloat() <= n.tx_prob && link_models.containsKey(n))
            events_queue.add(
                new SimulationEvent(n, this.time_ms, EventType.TX_START)
            );
    }
    
    private void _transmitPacket(LoRaNode n) {
        // set node as busy
        events_queue.add(
            new SimulationEvent(n, this.time_ms + _getPacketAirtime(n.DR), EventType.TX_END)
        );
    }
    
    private float _getPacketAirtime(int DR) {
        int CR = 1; // Coding Rate, from 1 to 4
        int BW = new int[]{125, 125, 125, 125, 125, 125, 250}[DR]; // Bandwidth
        int SF = new int[]{12, 11, 10, 9, 8, 7, 7}[DR]; // Spreading Factor
        double T_sym = Math.pow(2, SF) / BW; // Time for a symbol (ms)
        double bitrate = SF * (1/T_sym) * (4f/(4+CR));
        double time_on_air = this.payload_size * 8 * bitrate;
        return (float)time_on_air;
    }
}
