package lorasim2;

/**
 * @author alex
 */
public class LoRaPacket {
    public LoRaNode src, dst;
    public float start_ms, end_ms;
    public boolean successful;
    public int payload_size;

    public LoRaPacket(LoRaNode src, LoRaNode dst, float start, float end, boolean succ, int payload_size) {
        this.src = src;
        this.dst = dst;
        start_ms = start;
        end_ms = end;
        successful = succ;
        this.payload_size = payload_size;
    }
}
