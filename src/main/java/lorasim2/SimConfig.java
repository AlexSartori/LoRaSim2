package lorasim2;

import eu.hgweb.jini.Ini;
import eu.hgweb.jini.Section;
import java.io.IOException;

/**
 * @author alex
 */
public class SimConfig {
    private static SimConfig instance = null;
    
    public boolean headless;
    public String per_node_thr_csv,
               final_thr_csv,
               per_node_rx_csv,
               success_prob_csv,
               topology_csv,
               dr_rule;
    public int sim_duration_ms,
               pkts_per_hour,
               max_node_distance_m,
               payload_size,
               n_gateways,
               n_nodes;
    
    private SimConfig() {
        this.headless = false;
        this.sim_duration_ms = 1000;
        this.max_node_distance_m = 2000;
        this.payload_size = 8;
        this.n_gateways = 1;
        this.n_nodes = 5;
        this.dr_rule = null;
        this.pkts_per_hour = 100;
        
        this.per_node_thr_csv = null;
        this.final_thr_csv = null;
        this.per_node_rx_csv = null;
        this.success_prob_csv = null;
        this.topology_csv = null;
        
        this._loadConfigFile();
    }
    
    private void _loadConfigFile() {
        try {
            Ini file = new Ini("config.ini");
            
            Section s_env = file.section("environment");
            if (s_env.keyExists("headless"))
                this.headless = Boolean.parseBoolean(s_env.value("headless"));
            if (s_env.keyExists("sim_duration_ms"))
                this.sim_duration_ms = Integer.parseInt(s_env.value("sim_duration_ms"));
            
            Section s_nodes = file.section("nodes");
            if (s_nodes.keyExists("num_nodes"))
                this.n_nodes = Integer.parseInt(s_nodes.value("num_nodes"));
            if (s_nodes.keyExists("num_gateways"))
                this.n_gateways = Integer.parseInt(s_nodes.value("num_gateways"));
            if (s_nodes.keyExists("max_distance"))
                this.max_node_distance_m = Integer.parseInt(s_nodes.value("max_distance"));
            if (s_nodes.keyExists("payload_size"))
                this.payload_size = Integer.parseInt(s_nodes.value("payload_size"));
            if (s_nodes.keyExists("dr_rule"))
                this.dr_rule = s_nodes.value("dr_rule");
            if (s_nodes.keyExists("pkts_per_hour"))
                this.pkts_per_hour = Integer.parseInt(s_nodes.value("pkts_per_hour"));
            
            Section s_out = file.section("output");
            if (s_out.keyExists("per_node_thr_csv"))
                this.per_node_thr_csv = s_out.value("per_node_thr_csv");
            if (s_out.keyExists("final_thr_csv"))
                this.final_thr_csv = s_out.value("final_thr_csv");
            if (s_out.keyExists("per_node_rx_csv"))
                this.per_node_rx_csv = s_out.value("per_node_rx_csv");
            if (s_out.keyExists("success_prob_csv"))
                this.success_prob_csv = s_out.value("success_prob_csv");
            if (s_out.keyExists("topology_csv"))
                this.topology_csv = s_out.value("topology_csv");
            
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
