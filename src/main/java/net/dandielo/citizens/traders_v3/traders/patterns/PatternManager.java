package net.dandielo.citizens.traders_v3.traders.patterns;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import net.dandielo.citizens.traders_v3.core.dB;
import net.dandielo.citizens.traders_v3.traders.patterns.Pattern;
import net.dandielo.citizens.traders_v3.traders.patterns.types.ItemPattern;
import net.dandielo.citizens.traders_v3.traders.patterns.types.PricePattern;
import net.dandielo.citizens.traders_v3.traders.setting.GlobalSettings;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class PatternManager {

   public static PatternManager instance = new PatternManager();
   private HashMap patterns = new HashMap();
   protected File patternsFile;
   protected FileConfiguration patternsConfig;


   private PatternManager() {
      String fileName = GlobalSettings.getPatternFile();
      String baseDir = "plugins/dtlTraders";
      if(baseDir.contains("\\") && !"\\".equals(File.separator)) {
         baseDir = baseDir.replace("\\", File.separator);
      }

      File baseDirectory = new File(baseDir);
      if(!baseDirectory.exists()) {
         baseDirectory.mkdirs();
      }

      this.patternsFile = new File(baseDir, fileName);
      if(!this.patternsFile.exists()) {
         try {
            this.patternsFile.createNewFile();
         } catch (IOException var5) {
            throw new RuntimeException(var5);
         }
      }

      this.reload();
   }

   public void reload() {
      this.patternsConfig = new YamlConfiguration();

      try {
         this.patternsConfig.load(this.patternsFile);
         Iterator e = this.patternsConfig.getKeys(false).iterator();

         while(e.hasNext()) {
            String patternName = (String)e.next();
            Pattern pattern = createPattern(patternName, this.patternsConfig.getString(patternName + ".type"));
            dB.normal(new Object[]{patternName});
            pattern.loadItems(this.patternsConfig.getConfigurationSection(patternName));
            this.patterns.put(patternName.toLowerCase(), pattern);
         }

      } catch (Exception var4) {
         throw new IllegalStateException("Error loading patterns file", var4);
      }
   }

   private static Pattern createPattern(String name, String type) {
      return (Pattern)(type == null?null:(type.equals("price")?new PricePattern(name):(type.equals("item")?new ItemPattern(name):null)));
   }

   public static List getPatterns(List names) {
      ArrayList result = new ArrayList();
      Iterator var2 = instance.patterns.entrySet().iterator();

      while(var2.hasNext()) {
         Entry e = (Entry)var2.next();
         if(names.contains(e.getKey())) {
            result.add(e.getValue());
         }
      }

      return result;
   }

}
