package lorasim2;

/**
 * @author alex
 */
public class LoRaNode {
    private static int _global_id_counter = 0;
    public final int id;
    public final int DR;
    public final float tx_prob;

    public LoRaNode(int dr) {
        this.id = LoRaNode._global_id_counter++;
        this.DR = dr;
        this.tx_prob = 0.6f;
    }
    
    @Override
    public int hashCode() {
        return this.id;
    }
    
    @Override
    public boolean equals(Object n) {
        return id == ((LoRaNode)n).id;
    }
}
