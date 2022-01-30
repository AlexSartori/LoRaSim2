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
    TX_END_FAIL,
    NO_TX_END
};

class SimulationEvent {
    public final LoRaLink link;
    public float event_time;
    public EventType type;
    
    public SimulationEvent(LoRaLink link, float time, EventType event) {
        this.link = link;
        event_time = time;
        type = event;
    }
    
    public static Comparator<SimulationEvent> getComparator() {
        return (SimulationEvent t, SimulationEvent t1) -> (int)(t.event_time - t1.event_time);
    }
}

class TXEndEvent extends SimulationEvent {
    public final float tx_start_time;
    public TXEndEvent(LoRaLink link, float time, float tx_start) {
        super(link, time, EventType.TX_END);
        tx_start_time = tx_start;
    }
}

class LoRaLink {
    public LoRaNode src, dst;
    public LoRaLink(LoRaNode a, LoRaNode b) { src = a; dst = b; }
    @Override
    public boolean equals(Object l2) { return src.id == ((LoRaLink)l2).src.id && dst.id == ((LoRaLink)l2).dst.id; }
    @Override
    public int hashCode() { return src.hashCode() + dst.hashCode(); }
}

public class Simulator {
    private SimConfig config;
    private SimulationStats stats;
    private float time_ms;
    private ArrayList<LoRaNode> nodes;
    private ArrayList<LoRaGateway> gateways;
    private HashMap<LoRaLink, LoRaMarkovModel> link_models;
    private PriorityQueue<SimulationEvent> events_queue;
    private HashMap<LoRaNode, Float> open_transmissions;
    private final Random RNG;
    
    public Simulator() {
        stats = new SimulationStats();
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
        stats = new SimulationStats();
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
        _simLog("Added link " + n1.id + " <--> " + n2.id);
    }
    
    public LoRaMarkovModel getLinkModel(LoRaNode n1, LoRaNode n2) {
        LoRaLink link = new LoRaLink(n1, n2);
        return link_models.get(link);
    }
    
    public SimulationStats runSimulation(SimConfig conf) {
        this.config = conf;
        
        events_queue.add(new SimulationEvent(null, 0, EventType.SIMULATION_START));
        events_queue.add(new SimulationEvent(null, config.sim_duration_ms, EventType.SIMULATION_END));
        
        while (events_queue.size() > 0) {
            SimulationEvent curr_event = events_queue.remove();
            this.time_ms = curr_event.event_time;
            
            switch (curr_event.type) {
                case SIMULATION_START:
                    _simLog("Simulation started");
                    nodes.forEach(n -> { _scheduleNextNodeTransmission(n); });
                    break;
                case SIMULATION_END:
                    _simLog("Simulation ended");
                    events_queue.clear();
                    break;
                case TX_START:
                    _simLog("Beginning TX for node #" + curr_event.link.src.id);
                    
                    open_transmissions.put(curr_event.link.src, time_ms);
                    for (LoRaLink l : link_models.keySet())
                        if (l.src == curr_event.link.src) {
                            stats.beginTransmission(l, time_ms);
                            _beginNodeTX(l);
                        }
                    break;
                case TX_END:
                    _simLog("Ended TX for link " + curr_event.link.src.id + " <-> " + curr_event.link.dst.id);
                    
                    if (open_transmissions.remove(curr_event.link.src) != null)
                        // Only do this once for each TX_START (= many TX_ENDs)
                        _scheduleNextNodeTransmission(curr_event.link.src);
                    
                    _endNodeTX((TXEndEvent)curr_event);
                    break;
                case NO_TX_END:
                    _scheduleNextNodeTransmission(curr_event.link.src);
                    break;
            }
        }
        
        return stats;
    }
    
    private void _scheduleNextNodeTransmission(LoRaNode n) {
        float tx_time = this.time_ms + RNG.nextInt(config.tx_max_delay);
        EventType et = (RNG.nextFloat() <= n.tx_prob) ? EventType.TX_START : EventType.NO_TX_END;
        
        events_queue.add(
            new SimulationEvent(new LoRaLink(n, null), tx_time, et)
        );
    }
    
    private void _beginNodeTX(LoRaLink l) {
        float end_time = this.time_ms + _getPacketAirtime(l.src.DR);
        
        events_queue.add(
            new TXEndEvent(l, end_time, this.time_ms)
        );
    }
    
    private void _endNodeTX(TXEndEvent e) {
        float interference = stats.getRXSuperposition(e.link.dst, e.tx_start_time, this.time_ms);

        LoRaMarkovModel m_base = link_models.get(e.link);
        LoRaMarkovModel m = LoRaModelFactory.getLinkModel(e.link.src, e.link.dst, m_base.distance_m, interference);
        m.nextState(RNG);
        
        stats.endTransmission(e.link, time_ms, m.getCurrentState() == LoRaMarkovModel.MarkovState.SUCCESS);
    }
    
    private float _getPacketAirtime(int DR) {
        int CR = 1; // Coding Rate, from 1 to 4
        int BW = new int[]{125, 125, 125, 125, 125, 125, 250}[DR]; // Bandwidth
        int SF = new int[]{12, 11, 10, 9, 8, 7, 7}[DR]; // Spreading Factor
        double T_sym = Math.pow(2, SF) / BW; // Time for a symbol (ms)
        double bitrate = SF * (1/T_sym) * (4f/(4+CR));
        double time_on_air = config.payload_size * 8 / bitrate;
        return (float)time_on_air;
    }
}
