package lorasim2;

import gui.MainWindow;

/**
 * @author alex
 */
public class LoRaSim2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Simulator sim = new Simulator();
        
        if (SimConfig.getInstance().headless) {
            SimulationStats res = sim.runSimulation();
            res.getTransmissions();
        } else {
            MainWindow win = new MainWindow(sim);
            win.setVisible(true);
            win.repaint();
        }
    }
    
}
