package lorasim2;

import eu.hgweb.jini.Ini;
import eu.hgweb.jini.Section;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
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
        ArrayList<LoRaMarkovModel> candidates_by_dist = new ArrayList<>();
        
        for (LoRaMarkovModel m : candidates_by_DR) {
            if (candidates_by_dist.isEmpty())
                candidates_by_dist.add(m);
            else if (m.distance_m == candidates_by_dist.get(0).distance_m)
                candidates_by_dist.add(m);
            else {
                float old_delta = Math.abs(target_distance - candidates_by_dist.get(0).distance_m);
                float new_delta = Math.abs(target_distance - m.distance_m);
                
                if (new_delta < old_delta) {
                    candidates_by_dist.clear();
                    candidates_by_dist.add(m);
                }
            }
        }
        
        if (candidates_by_dist.size() == 0) {
            System.err.println("[ModelFactory]: Warning: No candidates available for dist = " + target_distance);
            return null;
        }
        
        /* Select the model with the closest interference rate -------------- */
        float closest_interf_delta = -1;
        LoRaMarkovModel closest_model = null;
        
        for (LoRaMarkovModel m : candidates_by_dist) {
            float new_delta = Math.abs(interf - m.interference_percent/100);
            if (closest_interf_delta == -1 || new_delta < closest_interf_delta) {
                closest_interf_delta = new_delta;
                closest_model = m;
            }
        }
        System.out.println("Returned model has interference delta = " + closest_interf_delta);
        System.out.println("  Target interf: " + interf + "  /  Model interf: " + closest_model.interference_percent/100);
            
        return closest_model;
    }
}
