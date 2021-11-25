package lorasim2;

/**
 * @author alex
 */
public class LoRaMarkovModel {
    public final double[][] P;
    public final int DR;
    public final int distance_m;
    public final float interference_percent;
    
    public LoRaMarkovModel(int dr, float distance) {
        P = new double[][] {
            { 0.5, 0.5 },
            { 0.5, 0.5 }
        };
        DR = dr;
        distance_m = (int)distance;
        interference_percent = 20;
    }
}
