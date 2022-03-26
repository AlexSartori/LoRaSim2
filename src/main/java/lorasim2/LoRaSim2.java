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
        if (SimConfig.getInstance().headless) {
            SimulationStats res;
            
            Simulator sim = new Simulator();
            res = sim.runSimulation();
            res.getTransmissions();
        } else {
            MainWindow win = new MainWindow();
            win.setVisible(true);
        }
    }
    
}
