package lorasim2;

import gui.CanvasWindow;

/**
 *
 * @author alex
 */
public class LoRaSim2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        CanvasWindow win = new CanvasWindow();
        win.setVisible(true);
        
        Simulator sim = new Simulator();
        sim.addNode(
            new LoRaNode(0, new LoRaMarkovModel())
        );
        sim.addNode(
            new LoRaNode(1, new LoRaMarkovModel())
        );
        sim.runSimulation();
    }
    
}
