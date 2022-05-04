package lorasim2;

import eu.hgweb.jini.Ini;
import eu.hgweb.jini.Section;
import java.io.IOException;

/**
 * @author alex
 */
public class SimConfig {
    private static SimConfig instance = null;
    
    public boolean headless,
               throughput_csv,
               per_node_rx_csv,
               success_prob_csv;
    public int sim_duration_ms,
               max_node_distance_m,
               tx_max_delay,
               payload_size,
               n_gateways,
               n_nodes;
    public String thr_out_fname,
            node_rx_out_fname,
            succ_p_out_fname;
    
    private SimConfig() {
        this.headless = false;
        this.sim_duration_ms = 1000;
        this.max_node_distance_m = 2000;
        this.tx_max_delay = 200;
        this.payload_size = 8;
        this.n_gateways = 1;
        this.n_nodes = 5;
        
        this.throughput_csv = true;
        this.per_node_rx_csv = true;
        this.success_prob_csv = true;
        
        this.thr_out_fname = "sim_res/node_{id}_throughput.csv";
        this.node_rx_out_fname = "sim_res/node_{id}_rx_data.csv";
        this.succ_p_out_fname = "sim_res/node_{id}_succ_prob.csv";
        
        this._loadConfigFile();
    }
    
    private void _loadConfigFile() {
        try {
            Ini file = new Ini("config.ini");
            
            Section s_env = file.section("environment");
            if (s_env.keyExists("headless"))
                this.headless = Boolean.parseBoolean(s_env.value("headless"));
            if (s_env.keyExists("sim_duration"))
                this.sim_duration_ms = Integer.parseInt(s_env.value("sim_duration"));
            if (s_env.keyExists("tx_max_delay"))
                this.tx_max_delay = Integer.parseInt(s_env.value("tx_max_delay"));
            
            Section s_nodes = file.section("nodes");
            if (s_nodes.keyExists("num_nodes"))
                this.n_nodes = Integer.parseInt(s_nodes.value("num_nodes"));
            if (s_nodes.keyExists("num_gateways"))
                this.n_gateways = Integer.parseInt(s_nodes.value("num_gateways"));
            if (s_nodes.keyExists("max_distance"))
                this.max_node_distance_m = Integer.parseInt(s_nodes.value("max_distance"));
            if (s_nodes.keyExists("payload_size"))
                this.payload_size = Integer.parseInt(s_nodes.value("payload_size"));
            
            Section s_out = file.section("output");
            if (s_out.keyExists("throughput_csv"))
                this.throughput_csv = Boolean.parseBoolean(s_out.value("throughput_csv"));
            if (s_out.keyExists("per_node_rx_csv"))
                this.per_node_rx_csv = Boolean.parseBoolean(s_out.value("per_node_rx_csv"));
            if (s_out.keyExists("success_prob_csv"))
                this.success_prob_csv = Boolean.parseBoolean(s_out.value("success_prob_csv"));
            
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
