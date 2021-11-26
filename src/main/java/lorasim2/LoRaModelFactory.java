package lorasim2;

import eu.hgweb.jini.Ini;
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
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        
        try {
            File archive = new File(classLoader.getResource("models_v1.zip").toURI());
        
            ZipInputStream zis = new ZipInputStream(
                new FileInputStream(archive)
            );
            
            for (ZipEntry ze = zis.getNextEntry(); ze != null; ze = zis.getNextEntry()) {
                // 'zis' is the input stream and will yield an 'EOF' before the next entry
                Ini ini_model = new Ini(zis, true);
                LoRaMarkovModel m_model = new LoRaMarkovModel(
                    Integer.parseInt(ini_model.section("").value("dr")),
                    Float.parseFloat(ini_model.section("").value("distance_TX_RX"))
                );
                models.add(m_model);
            }
        } catch (IOException ex) {
            System.err.println("[ModelFactory]: Error loading models: " + ex.getMessage());
        } catch (URISyntaxException ex) {
            System.err.println("[ModelFactory]: Couldn't find models archive: " + ex.getMessage());
        }
    }
    
    public static LoRaMarkovModel getLinkModel(LoRaNode n1, LoRaNode n2, float target_distance) {
        if (models == null)
            _loadModels();
        
        if (n1.DR != -1 && n2.DR != -1 && n1.DR != n2.DR)
            return null;
        
        int DR = n1 instanceof LoRaGateway ? n2.DR : n1.DR;
        
        ArrayList<LoRaMarkovModel> candidates = new ArrayList<>();
        models.forEach(m -> { if (m.DR == DR) candidates.add(m); });
        
        if (candidates.size() == 0)
            System.err.println("[ModelFactory]: No candidates for DR = " + DR);
        
        LoRaMarkovModel closest_model = null;
        float smallest_delta = -1;
        
        for (LoRaMarkovModel m : candidates) {
            if (smallest_delta == -1 || Math.abs(target_distance - m.distance_m) < smallest_delta) {
                smallest_delta = Math.abs(target_distance - m.distance_m);
                closest_model = m;
            }
        }
        
        return closest_model;
    }
}
