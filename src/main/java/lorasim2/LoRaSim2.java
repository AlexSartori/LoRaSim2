package lorasim2;

import gui.MainWindow;

/**
 * @author alex
 */
public class LoRaSim2 {

    public static void main(String[] args) {
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
        
        if (conf.throughput_csv)
            exporter.exportThroughput("sim_res/node_{id}_throughput.csv");
        if (conf.per_node_rx_csv)
            exporter.exportReceptions("sim_res/node_{id}_rx_data.csv");
        if (conf.success_prob_csv)
            exporter.exportSuccessProbs("sim_res/node_{id}_succ_prob.csv");
    }
    
}
