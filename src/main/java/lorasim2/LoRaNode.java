package lorasim2;

/**
 *
 * @author alex
 */
public class LoRaNode {
    private static int _global_id_counter = 0;
    public final int id;
    public final LoRaMarkovModel model;
    
    public LoRaNode(LoRaMarkovModel model) {
        this.id = LoRaNode._global_id_counter++;
        this.model = model;
    }
}
