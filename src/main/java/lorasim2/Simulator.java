package lorasim2;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author alex
 */
public class Simulator {
    private ArrayList<LoRaNode> nodes;
    private HashMap<LoRaNode, HashMap<LoRaNode, LoRaMarkovModel>> link_models;
    
    public Simulator() {
        nodes = new ArrayList<>();
        link_models = new HashMap<>();
    }
    
    public void addNode(LoRaNode node) {
        nodes.add(node);
        link_models.put(node, new HashMap<>());
    }
    
    public void setLinkModel(LoRaNode n1, LoRaNode n2, LoRaMarkovModel m) throws Exception {
        if (!link_models.containsKey(n1))
            throw new Exception("Unknown node: " + n1.id);
        if (!link_models.get(n1).containsKey(n2))
            throw new Exception("Unknown node: " + n2.id);
        
        link_models.get(n1).put(n2, m);
    }
    
    public LoRaMarkovModel getLinkModel(LoRaNode n1, LoRaNode n2) {
        if (link_models.containsKey(n1) && link_models.get(n1).containsKey(n2))
            return link_models.get(n1).get(n2);
        return null;
    }
    
    public void runSimulation() {
    }
}
