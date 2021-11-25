package lorasim2;

import java.util.ArrayList;

/**
 * @author alex
 */
public class Simulator {
    private ArrayList<LoRaNode> nodes;
    
    public Simulator() {
        nodes = new ArrayList<>();
    }
    
    public void addNode(LoRaNode node) {
        nodes.add(node);
    }
    
    public void runSimulation() {
    }
}
