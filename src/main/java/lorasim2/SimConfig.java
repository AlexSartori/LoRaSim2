package lorasim2;

/**
 * @author alex
 */
public class SimConfig {
    public final int sim_duration_ms, tx_max_delay, payload_size;
    
    public SimConfig(int sim_duration_ms, int tx_max_delay, int payload_size) {
        this.sim_duration_ms = sim_duration_ms;
        this.tx_max_delay = tx_max_delay;
        this.payload_size = payload_size;
    }
}
