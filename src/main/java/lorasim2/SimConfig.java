package lorasim2;

/**
 * @author alex
 */
public class SimConfig {
    private static SimConfig instance = null;
    
    public int sim_duration_ms,
               max_node_distance_m,
               tx_max_delay,
               payload_size,
               n_gateways,
               n_nodes;
    
    private SimConfig() {
        this.sim_duration_ms = 1000;
        this.max_node_distance_m = 2000;
        this.tx_max_delay = 200;
        this.payload_size = 8;
        this.n_gateways = 1;
        this.n_nodes = 5;
    }
    
    /**
     * Return the global simulator configuration
     */
    public static SimConfig getInstance() {
        if (instance == null)
            instance = new SimConfig();
        return instance;
    }
}
