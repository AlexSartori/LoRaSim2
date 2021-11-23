package lorasim2;

/**
 * @author alex
 */
public class LoRaMarkovModel {
    public final double[][] P;
    public final int DR;
    public final int distance_m;
    public final float interference_percent;
    
    public LoRaMarkovModel() {
        P = new double[][] {
            { 0.5, 0.5 },
            { 0.5, 0.5 }
        };
        DR = 2;
        distance_m = 1000;
        interference_percent = 20;
    }
}
