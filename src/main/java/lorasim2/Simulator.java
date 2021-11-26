package lorasim2;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

/**
 * @author alex
 */

enum EventType {
    SIMULATION_START,
    SIMULATION_END
};

class SimulationEvent {
    public float event_time;
    public EventType type;
    
    public SimulationEvent(float time, EventType event) {
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
    private ArrayList<LoRaNode> nodes;
    private ArrayList<LoRaGateway> gateways;
    private HashMap<LoRaNode, HashMap<LoRaNode, LoRaMarkovModel>> link_models;
    private PriorityQueue<SimulationEvent> events_queue;
    
    public Simulator() {
        nodes = new ArrayList<>();
        gateways = new ArrayList<>();
        link_models = new HashMap<>();
        events_queue = new PriorityQueue<>(SimulationEvent.getComparator());
        resetSimulation();
    }
    
    public ArrayList<LoRaNode> getNodes() {
        return (ArrayList<LoRaNode>)nodes.clone();
    }
    
    public ArrayList<LoRaGateway> getGateway() {
        return (ArrayList<LoRaGateway>)gateways.clone();
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
        System.out.println("[Simulator]: Added link " + n1.id + " <--> " + n2.id);
    }
    
    public LoRaMarkovModel getLinkModel(LoRaNode n1, LoRaNode n2) {
        if (link_models.containsKey(n1) && link_models.get(n1).containsKey(n2))
            return link_models.get(n1).get(n2);
        return null;
    }
    
    public void runSimulation() {
        events_queue.add(new SimulationEvent(0, EventType.SIMULATION_START));
        events_queue.add(new SimulationEvent(10, EventType.SIMULATION_END));
        
        while (events_queue.size() > 0) {
            SimulationEvent curr_event = events_queue.remove();
            System.out.println("[Simulator]: Event: " + curr_event.event_time);
        }
    }
}
