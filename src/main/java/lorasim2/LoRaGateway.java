package lorasim2;

/**
 * @author alex
 */
public class LoRaGateway extends LoRaNode {
    public LoRaGateway() {
        super(-1);
    }
    
    @Override
    public int hashCode() {
        return this.id;
    }
    
    @Override
    public boolean equals(Object g) {
        if (g.getClass() != LoRaGateway.class)
            return false;
        return id == ((LoRaGateway)g).id;
    }
}
