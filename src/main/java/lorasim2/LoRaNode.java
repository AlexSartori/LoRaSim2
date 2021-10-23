package lorasim2;

/**
 *
 * @author alex
 */
public class LoRaNode {
    public final int id;
    public final LoRaMarkovModel model;
    
    public LoRaNode(int id, LoRaMarkovModel model) {
        this.id = id;
        this.model = model;
    }
}
