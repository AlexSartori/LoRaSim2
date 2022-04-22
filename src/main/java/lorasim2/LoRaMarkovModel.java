package lorasim2;

import java.util.Random;

/**
 * @author alex
 */
public class LoRaMarkovModel {
    public enum MarkovState { SUCCESS, FAIL };
    
    private final float[][] P;
    private MarkovState current_state;
    public final int DR;
    public final int distance_m;
    public final float interference_percent;
    
    public LoRaMarkovModel(float[][] p, int dr, float distance, float int_percent) {
        current_state = MarkovState.SUCCESS;
        P = p;
        DR = dr;
        distance_m = (int)distance;
        interference_percent = int_percent;
    }
    
    public void setCurrentState(MarkovState s) {
        current_state = s;
    }
    
    public MarkovState getCurrentState() {
        return current_state;
    }
    
    public MarkovState nextState(Random rng) {
        if (current_state == MarkovState.SUCCESS && rng.nextFloat() <= P[0][1])
            current_state = MarkovState.FAIL;
        else if (current_state == MarkovState.FAIL && rng.nextFloat() <= P[1][0])
            current_state = MarkovState.SUCCESS;
        return current_state;
    }
}
