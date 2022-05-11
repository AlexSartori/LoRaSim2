package lorasim2;

import gui.MainWindow;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

/**
 * @author alex
 */
public class LoRaSim2 {

    public static void main(String[] args) {
        LoRaSim2.overrideConfWithArgs(args);
        
        Simulator sim = new Simulator();
        SimConfig conf = SimConfig.getInstance();
        SimulationStats res = null;
        
        if (conf.headless) {
            res = sim.runSimulation();
        } else {
            MainWindow win = new MainWindow(sim);
            win.setVisible(true);
            win.repaint();
            
            try {
                synchronized(win) {
                    win.wait();
                }
            } catch (InterruptedException ex) {
                System.err.println("Concurrency error while waiting for simulator result set");
            }
            
            res = win.getSimResult();
        }
        
        CsvExporter exporter = new CsvExporter(res);
        
        if (conf.final_thr_csv != null)
            exporter.exportFinalThroughput(conf.final_thr_csv);
        if (conf.per_node_rx_csv != null)
            exporter.exportReceptions(conf.per_node_rx_csv);
        if (conf.success_prob_csv != null)
            exporter.exportSuccessProbs(conf.success_prob_csv);
        if (conf.topology_csv != null)
            exporter.exportTopology(sim.getTopology(), conf.topology_csv);
    }
    
    private static void overrideConfWithArgs(String[] argv) {
        ArgumentParser parser = ArgumentParsers.newFor("LoRaSim2").build()
                .defaultHelp(true).description("Simulate LoRa networks");
        parser.addArgument("--num-nodes").help("Override number of nodes");
        parser.addArgument("--num-gateways").help("Override number of gateways");
        parser.addArgument("--final-thr-csv").help("Set the destination filename for the throughput data");

        Namespace ns = null;
        
        try {
            ns = parser.parseArgs(argv);
        } catch(ArgumentParserException ex) {
            parser.handleError(ex);
            return;
        }
        
        SimConfig conf = SimConfig.getInstance();
        
        String n_n = ns.getString("num_nodes");
        if (n_n != null) conf.n_nodes = Integer.parseInt(n_n);
        
        String n_gw = ns.getString("num_gateways");
        if (n_gw != null) conf.n_gateways = Integer.parseInt(n_gw);
        
        String thr_fname = ns.getString("final_thr_csv");
        if (thr_fname != null) conf.final_thr_csv = thr_fname;
    }
}
