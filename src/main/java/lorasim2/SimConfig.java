package lorasim2;

import eu.hgweb.jini.Ini;
import eu.hgweb.jini.Section;
import java.io.IOException;

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
        
        this._loadConfigFile();
    }
    
    private void _loadConfigFile() {
        try {
            Ini file = new Ini("config.ini");
            
            Section s_env = file.section("environment");
            if (s_env.keyExists("sim_duration"))
                this.sim_duration_ms = Integer.parseInt(s_env.value("sim_duration"));
            
            Section s_nodes = file.section("nodes");
            if (s_nodes.keyExists("num_nodes"))
                this.n_nodes = Integer.parseInt(s_nodes.value("num_nodes"));
            if (s_nodes.keyExists("num_gateways"))
                this.n_gateways = Integer.parseInt(s_nodes.value("num_gateways"));
            if (s_nodes.keyExists("max_distance"))
                this.max_node_distance_m = Integer.parseInt(s_nodes.value("max_distance"));
            if (s_nodes.keyExists("payload_size"))
                this.payload_size = Integer.parseInt(s_nodes.value("payload_size"));
            
        } catch (IOException ex) {
            System.err.println("Simulator config file not found, using default values.");
        }
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
