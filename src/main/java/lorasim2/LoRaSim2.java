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
            
            System.out.println("Sim finished");
        }
        
        if (conf.throughput_csv) {
            new CsvExporter(res).exportThroughput("node_{id}_throughput.csv");
        }
    }
    
}
