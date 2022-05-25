package lorasim2;

import eu.hgweb.jini.Ini;
import eu.hgweb.jini.Section;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author alex
 */
public class LoRaModelFactory {
    private static ArrayList<LoRaMarkovModel> models = null;
    
    private static void _loadModels() {
        models = new ArrayList<>();
        
        try {
            ClassLoader classLoader = ClassLoader.getSystemClassLoader();
            ZipInputStream zis = new ZipInputStream(classLoader.getResourceAsStream("models_v2.zip"));
            
            for (ZipEntry ze = zis.getNextEntry(); ze != null; ze = zis.getNextEntry()) {
                // 'zis' is the input stream and will yield an 'EOF' before the next entry
                Ini ini_model = new Ini(zis, true);
                Section s = ini_model.section("");
                
                float[][] P = new float[][] {
                    { Float.parseFloat(s.value("p00")), Float.parseFloat(s.value("p01")) },
                    { Float.parseFloat(s.value("p10")), Float.parseFloat(s.value("p11")) }
                };
                
                LoRaMarkovModel m_model = new LoRaMarkovModel(
                    P,
                    Integer.parseInt(s.value("dr")),
                    Float.parseFloat(s.value("distance_TX_RX")),
                    Float.parseFloat(s.value("pr_Int"))
                );
                models.add(m_model);
                // System.out.println("[ModelFactory] Loaded model from file: " + ze.getName());
            }
        } catch (IOException ex) {
            System.err.println("[ModelFactory]: Error loading models: " + ex.getMessage());
        }
    }
    
    public static LoRaMarkovModel getLinkModel(LoRaNode n1, LoRaNode n2, float target_distance, float interf) {
        if (models == null) _loadModels();
        if (n1.DR != -1 && n2.DR != -1 && n1.DR != n2.DR) return null;
                
        /* Gather all models for the correct Datarate ----------------------- */
        int DR = n1 instanceof LoRaGateway ? n2.DR : n1.DR;
        ArrayList<LoRaMarkovModel> candidates_by_DR = new ArrayList<>();
        models.forEach(m -> { if (m.DR == DR) candidates_by_DR.add(m); });
        
        if (candidates_by_DR.size() == 0) {
            System.err.println("[ModelFactory]: Warning: No candidates available for DR = " + DR);
            return null;
        }
        
        /* Select only those models with the best matching distance --------- */
        ArrayList<LoRaMarkovModel> candidates_by_dist = LoRaModelFactory.filterModelsByDistance(target_distance, candidates_by_DR);
        
        if (candidates_by_dist.size() == 0) {
            System.err.println("[ModelFactory]: Warning: No candidates available for dist = " + target_distance);
            return null;
        }
        
        /* Select the model with the closest interference rate -------------- */
        float closest_interf_delta = -1;
        LoRaMarkovModel closest_model = null;
        
        for (LoRaMarkovModel m : candidates_by_DR) {
            float new_delta = Math.abs(interf - m.interference_percent/100);
            if (closest_interf_delta == -1 || new_delta < closest_interf_delta) {
                closest_interf_delta = new_delta;
                closest_model = m;
            }
        }
            
        return closest_model;
    }
    
    public static int chooseDRBySuccProb(float dist, float min_succ_prob) {
        if (models == null) _loadModels();
        
        ArrayList<LoRaMarkovModel> candidates = new ArrayList<>();
        for (LoRaMarkovModel m : LoRaModelFactory.filterModelsByDistance(dist, models)) {
            float pi_0 = m.getProbMatrix()[1][0] / (m.getProbMatrix()[0][1] + m.getProbMatrix()[1][0]);
            
            if (pi_0 >= min_succ_prob)
                candidates.add(m);
        }
        
        int highest_DR = -1;
        for (LoRaMarkovModel m : candidates)
            if (m.DR > highest_DR)
                highest_DR = m.DR;
        
        return highest_DR;
    }
    
    public static int chooseDRByDistance(float dist) {
        if (models == null) _loadModels();
        
        if (dist <= 500)
            return 3;
        if (dist <= 1000)
            return 2;
        if (dist <= 1500)
            return 1;
        return 0;
    }
    
    public static ArrayList<LoRaMarkovModel> filterModelsByDistance(float dist_m, ArrayList<LoRaMarkovModel> model_set) {
        ArrayList<LoRaMarkovModel> res = new ArrayList<>();
        
        for (LoRaMarkovModel m : model_set) {
            if (res.isEmpty())
                res.add(m);
            else if (m.distance_m == res.get(0).distance_m)
                res.add(m);
            else {
                float old_delta = Math.abs(dist_m - res.get(0).distance_m);
                float new_delta = Math.abs(dist_m - m.distance_m);
                
                if (new_delta < old_delta) {
                    res.clear();
                    res.add(m);
                }
            }
        }
        
        return res;
    }
}
