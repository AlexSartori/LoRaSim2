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
        for (LoRaNode n : nodes) {
            System.out.println("Node #" + n.id);
            System.out.println("  [" + n.model.P[0][0] + ", " + n.model.P[0][1] + "]");
            System.out.println("  [" + n.model.P[1][0] + ", " + n.model.P[1][1] + "]");
        }
    }
}
