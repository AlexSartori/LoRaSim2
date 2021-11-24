package lorasim2;

import eu.hgweb.jini.Ini;
import java.io.File;
import java.io.FileInputStream;
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
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        File archive = new File(classLoader.getResource("models_v1.zip").toURI());
        
        try {
            ZipInputStream zis = new ZipInputStream(
                new FileInputStream(archive)
            );
            
            for (ZipEntry ze = zis.getNextEntry(); ze != null; ze = zis.getNextEntry()) {
                // 'zis' is the input stream and will yield an 'EOF' before the next entry
                Ini ini_model = new Ini(zis, true);
                LoRaMarkovModel m_model = new LoRaMarkovModel(
                    Integer.parseInt(ini_model.section("").value("dr"))
                );
                models.add(m_model);
            }
        } catch (IOException ex) {
            System.err.println("Error loading models: " + ex.getMessage());
        }
    }
    
    public static LoRaMarkovModel getLinkModel(LoRaNode n1, LoRaNode n2) {
        if (models == null)
            _loadModels();
        
        return models.get(0);
    }
}
