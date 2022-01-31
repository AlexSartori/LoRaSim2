package lorasim2;

/**
 * @author alex
 */
public class LoRaLink {
    public LoRaNode src, dst;
    public LoRaLink(LoRaNode a, LoRaNode b) { src = a; dst = b; }
    
    @Override
    public boolean equals(Object l2) { return src.id == ((LoRaLink)l2).src.id && dst.id == ((LoRaLink)l2).dst.id; }
    @Override
    public int hashCode() { return src.hashCode() + dst.hashCode(); }    
}
