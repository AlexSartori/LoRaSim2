package lorasim2;

/**
 *
 * @author alex
 */
public class LoRaMarkovModel {
    public final double[][] P;
    
    public LoRaMarkovModel() {
        P = new double[][] {
            { 0.5, 0.5 },
            { 0.5, 0.5 }
        };
    }
}
