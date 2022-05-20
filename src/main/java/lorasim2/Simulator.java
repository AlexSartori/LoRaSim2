package lorasim2;

import java.awt.Point;
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

public class Simulator {
    private SimConfig config;
    private SimulationStats stats;
    private float time_ms;
    private final ArrayList<LoRaNode> nodes;
    private final ArrayList<LoRaGateway> gateways;
    private final HashMap<LoRaNode, Point> node_locations;
    private final HashMap<LoRaLink, LoRaMarkovModel> link_models;
    private final PriorityQueue<SimulationEvent> events_queue;
    private final HashMap<LoRaNode, Float> open_transmissions;
    private final Random RNG;
    
    public Simulator() {
        config = SimConfig.getInstance();
        stats = new SimulationStats();
        nodes = new ArrayList<>();
        gateways = new ArrayList<>();
        node_locations = new HashMap<>();
        link_models = new HashMap<>();
        events_queue = new PriorityQueue<>(SimulationEvent.getComparator());
        open_transmissions = new HashMap<>();
        RNG = new Random();
        
        
        _createNodes();
    }
    
    private void _simLog(String msg) {
        System.out.println("[Simulator:" + (int)this.time_ms + "]: " + msg);
    }
    
    public ArrayList<LoRaNode> getNodes() {
        return nodes;
    }
    
    public ArrayList<LoRaGateway> getGateways() {
        return gateways;
    }
    
    public HashMap<LoRaNode, Point> getTopology() {
        return node_locations;
    }
    
    public Point getNodeLocation(LoRaNode n) {
        return node_locations.get(n);
    }
    
    private void _createNodes() {
        /* Create gateways */
        for (int i = 0; i < config.n_gateways; i++) {
            LoRaGateway g = new LoRaGateway();
            Point location = _getRandomPoint();
            gateways.add(g);
            node_locations.put(g, location);
        }
        
        for (int i = 0; i < config.n_nodes; i++) {
            /* Choose random point */
            Point location = _getRandomPoint();
            
            /* Find highest DR that fits */
            int highest_dr = -1;
            for (LoRaGateway g : gateways) {
                float dist = (float)location.distance(node_locations.get(g));
                int tmp = LoRaModelFactory.getBestDR(dist, 0.5f);
                if (tmp > highest_dr)
                    highest_dr = tmp;
            }
            
            /* Check if a suitable DR was found */
            if (highest_dr == -1) {
                i--;
                continue;
            }
            
            /* Create and add the node */
            LoRaNode n = new LoRaNode(highest_dr);
            nodes.add(n);
            node_locations.put(n, location);
            
            /* Connect it to the gateways */
            gateways.forEach(g -> {
                float distance = (float)location.distance(node_locations.get(g));
                LoRaMarkovModel model = LoRaModelFactory.getLinkModel(n, g, distance, 0);
                
                if (model != null)
                    _setLinkModel(n, g, model);
                else
                    _simLog("Warning: no model found for nodes: " + n.id + " --> " + g.id);
            });
        }
    }
    
    private Point _getRandomPoint() {
        return new Point(
            RNG.nextInt(config.max_node_distance_m),
            RNG.nextInt(config.max_node_distance_m)
        );
    }
    
    private void _setLinkModel(LoRaNode n, LoRaGateway gw, LoRaMarkovModel m) {
        if (!nodes.contains(n))
            _simLog("Error@setLinkModel: Unknown node: " + n.id);
        if (!gateways.contains(gw))
            _simLog("Error@setLinkModel: Unknown gateway: " + gw.id);
        
        link_models.put(new LoRaLink(n, gw), m);
    }
    
    public LoRaMarkovModel getLinkModel(LoRaNode n1, LoRaNode n2) {
        LoRaLink link = new LoRaLink(n1, n2);
        return link_models.get(link);
    }
    
    public SimulationStats runSimulation() {
        String progress_string = "";
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
                    open_transmissions.put(curr_event.link.src, time_ms);
                    for (LoRaLink l : link_models.keySet())
                        if (l.src == curr_event.link.src) {
                            stats.beginTransmission(l, time_ms);
                            _beginNodeTX(l);
                        }
                    break;
                case TX_END:
                    if (open_transmissions.remove(curr_event.link.src) != null)
                        // Only do this once for each TX_START (= many TX_ENDs)
                        _scheduleNextNodeTransmission(curr_event.link.src);
                    
                    _endNodeTX((TXEndEvent)curr_event);
                    break;
                case NO_TX_END:
                    _scheduleNextNodeTransmission(curr_event.link.src);
                    break;
            }
            
            /* Calculate progress but print it only if different from last time */
            float progress = time_ms*100/config.sim_duration_ms;
            String tmp = String.format("%5.1f%%", progress);
            if (tmp != progress_string) {
                progress_string = tmp;
                System.out.printf("Progress: %5.1f%%\r", progress);
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

        /* Update link model based on interference */
        LoRaMarkovModel m_prev = link_models.get(e.link);
        LoRaMarkovModel m_new = LoRaModelFactory.getLinkModel(e.link.src, e.link.dst, m_prev.distance_m, interference);
        link_models.put(e.link, m_new);
        m_new.setCurrentState(m_prev.getCurrentState());
        m_new.nextState(RNG);
        
        stats.endTransmission(e.link, time_ms, m_new.getCurrentState() == LoRaMarkovModel.MarkovState.SUCCESS, config.payload_size);
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
