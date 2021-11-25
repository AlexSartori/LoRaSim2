package lorasim2;

/**
 * @author alex
 */
public class LoRaNode {
    private static int _global_id_counter = 0;
    public final int id;
    public final int DR;

    public LoRaNode(int dr) {
        this.id = LoRaNode._global_id_counter++;
        this.DR = dr;
    }
    
    @Override
    public int hashCode() {
        return this.id;
    }
}
