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
    TX_END,
    NO_TX_END,
    RX_START,
    RX_END_OK,
    RX_END_FAIL
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
        return (SimulationEvent t, SimulationEvent t1) -> (int)(t.event_time - t1.event_time);
    }
}

class LoRaLink {
    public LoRaNode n1, n2;
    public LoRaLink(LoRaNode a, LoRaNode b) { n1 = a; n2 = b; }
    @Override
    public boolean equals(Object l2) { return n1.id == ((LoRaLink)l2).n1.id && n2.id == ((LoRaLink)l2).n2.id; }
    @Override
    public int hashCode() { return n1.hashCode() + n2.hashCode(); }
}

public class Simulator {
    private SimConfig config;
    private float time_ms;
    private ArrayList<LoRaNode> nodes;
    private ArrayList<LoRaGateway> gateways;
    private HashMap<LoRaLink, LoRaMarkovModel> link_models;
    private PriorityQueue<SimulationEvent> events_queue;
    private HashMap<LoRaNode, Float> open_transmissions;
    private final Random RNG;
    
    public Simulator() {
        nodes = new ArrayList<>();
        gateways = new ArrayList<>();
        link_models = new HashMap<>();
        events_queue = new PriorityQueue<>(SimulationEvent.getComparator());
        open_transmissions = new HashMap<>();
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
    
    public void resetSimulation() {
        time_ms = 0;
        nodes.clear();
        gateways.clear();
        link_models.clear();
        events_queue.clear();
        open_transmissions.clear();
        config = null;
    }
    
    public void addNode(LoRaNode node) {
        nodes.add(node);
    }
    
    public void addGateway(LoRaGateway gw) {
        gateways.add(gw);
    }
    
    public void setLinkModel(LoRaNode n1, LoRaNode n2, LoRaMarkovModel m) throws Exception {
        if (!nodes.contains(n1) && !gateways.contains(n1))
            throw new Exception("Unknown node: " + n1.id);
        if (!nodes.contains(n2) && !gateways.contains(n2))
            throw new Exception("Unknown node: " + n2.id);
        
        link_models.put(new LoRaLink(n1, n2), m);
        _simLog("[Simulator]: Added link " + n1.id + " <--> " + n2.id);
    }
    
    public LoRaMarkovModel getLinkModel(LoRaNode n1, LoRaNode n2) {
        LoRaLink link = new LoRaLink(n1, n2);
        return link_models.get(link);
    }
    
    public SimulationResults runSimulation(SimConfig conf) {
        this.config = conf;
        SimulationResults result = new SimulationResults();
        
        events_queue.add(new SimulationEvent(null, 0, EventType.SIMULATION_START));
        events_queue.add(new SimulationEvent(null, config.sim_duration_ms, EventType.SIMULATION_END));
        
        while (events_queue.size() > 0) {
            SimulationEvent curr_event = events_queue.remove();
            this.time_ms = curr_event.event_time;
            
            switch (curr_event.type) {
                case SIMULATION_START:
                    _simLog("Simulation started");
                    nodes.forEach(n -> { _scheduleNextTransmission(n); });
                    break;
                case SIMULATION_END:
                    _simLog("Simulation ended");
                    events_queue.clear();
                    break;
                case TX_START:
                    _simLog("Beginning TX for node #" + curr_event.node.id);
                    open_transmissions.put(curr_event.node, time_ms);
                    result.begin_tx(curr_event.node, time_ms);
                    _transmitPacket(curr_event.node);
                    break;
                case TX_END:
                    _simLog("Ended TX for node #" + curr_event.node.id);
                    open_transmissions.remove(curr_event.node);
                    result.end_tx(curr_event.node, time_ms, true);
                    _scheduleNextTransmission(curr_event.node);
                    break;
                case NO_TX_END:
                    _scheduleNextTransmission(curr_event.node);
                    break;
                case RX_START:
                    _simLog("Beginning RX for node #" + curr_event.node.id);
                    result.begin_rx(curr_event.node, time_ms);
                    break;
                case RX_END_OK:
                    _simLog("Ended RX (SUCC) for node #" + curr_event.node.id);
                    result.end_rx(curr_event.node, time_ms, true);
                    break;
                case RX_END_FAIL:
                    _simLog("Ended RX (FAIL) for node #" + curr_event.node.id);
                    result.end_rx(curr_event.node, time_ms, false);
                    break;
            }
        }
        
        return result;
    }
    
    private void _scheduleNextTransmission(LoRaNode n) {
        float tx_time = this.time_ms + RNG.nextInt(config.tx_max_delay);
        EventType et = (RNG.nextFloat() <= n.tx_prob) ? EventType.TX_START : EventType.NO_TX_END;
        
        events_queue.add(
            new SimulationEvent(n, tx_time, et)
        );
    }
    
    private void _transmitPacket(LoRaNode n) {
        float end_time = this.time_ms + _getPacketAirtime(n.DR);
        
        for (LoRaLink l : link_models.keySet()) {
            if (l.n1 != n) continue;
            
            events_queue.add(new SimulationEvent(l.n2, this.time_ms, EventType.RX_START));
            
            LoRaMarkovModel m = link_models.get(l);
            m.nextState(RNG);
            
            events_queue.add(
                new SimulationEvent(
                    l.n2, end_time,
                    m.getSCurrentState() == LoRaMarkovModel.MarkovState.SUCCESS ? EventType.RX_END_OK : EventType.RX_END_FAIL
                )
            );
        }
        
        events_queue.add(new SimulationEvent(n, end_time, EventType.TX_END));
    }
    
    private float _getPacketAirtime(int DR) {
        int CR = 1; // Coding Rate, from 1 to 4
        int BW = new int[]{125, 125, 125, 125, 125, 125, 250}[DR]; // Bandwidth
        int SF = new int[]{12, 11, 10, 9, 8, 7, 7}[DR]; // Spreading Factor
        double T_sym = Math.pow(2, SF) / BW; // Time for a symbol (ms)
        double bitrate = SF * (1/T_sym) * (4f/(4+CR));
        double time_on_air = config.payload_size * 8 * bitrate;
        return (float)time_on_air;
    }
}
