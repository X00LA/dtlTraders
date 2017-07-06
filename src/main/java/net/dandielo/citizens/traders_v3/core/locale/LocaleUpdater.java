package net.dandielo.citizens.traders_v3.core.locale;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.dandielo.citizens.traders_v3.core.locale.LocaleEntry;
import net.dandielo.citizens.traders_v3.core.locale.LocaleItem;
import net.dandielo.citizens.traders_v3.core.locale.LocaleManager;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class LocaleUpdater {

   private Map messages;
   private Map keywords;
   private Map ui;


   LocaleUpdater(Configuration config) {
      config.options().pathSeparator('/');
      this.messages = new HashMap();
      this.keywords = new HashMap();
      this.ui = new HashMap();
      this.loadMessages("1.1.3", config.getConfigurationSection(buildPath(new String[]{"messages"})));
      this.loadKeywords("1.1.3", config.getConfigurationSection(buildPath(new String[]{"keywords"})));
      this.loadUIConfigs("1.1.3", config.getConfigurationSection(buildPath(new String[]{"ui"})));
   }

   protected void loadMessages(String currentVersion, ConfigurationSection config) {
      if(config != null) {
         Iterator var3 = config.getKeys(false).iterator();

         while(var3.hasNext()) {
            String key = (String)var3.next();
            this.messages.put(new LocaleEntry(key, config.getString(buildPath(new String[]{key, "new"}), ""), currentVersion), config.getString(buildPath(new String[]{key, "message"})));
         }

      }
   }

   protected void loadKeywords(String currentVersion, ConfigurationSection config) {
      if(config != null) {
         Iterator var3 = config.getKeys(false).iterator();

         while(var3.hasNext()) {
            String key = (String)var3.next();
            this.keywords.put(new LocaleEntry("#" + key, config.getString(buildPath(new String[]{key, "new"}), ""), currentVersion), config.getString(LocaleManager.buildPath(new String[]{key, "keyword"})));
         }

      }
   }

   protected void loadUIConfigs(String currentVersion, ConfigurationSection config) {
      if(config != null) {
         Iterator var3 = config.getKeys(false).iterator();

         while(var3.hasNext()) {
            String key = (String)var3.next();
            this.ui.put(new LocaleEntry(key, config.getString(buildPath(new String[]{key, "new"}), ""), currentVersion), new LocaleItem(config.getString(buildPath(new String[]{key, "name"})), config.getStringList(buildPath(new String[]{key, "lore"}))));
         }

      }
   }

   public void update(Map messages, Map keywords, Map uiSettings, File file) {
      YamlConfiguration newLocaleYaml = new YamlConfiguration();
      newLocaleYaml.set("ver", "1.1.3");
      newLocaleYaml.set("messages", "");
      newLocaleYaml.set("keywords", "");
      newLocaleYaml.set("ui", "");
      newLocaleYaml.options().pathSeparator('/');
      Iterator e = this.messages.entrySet().iterator();

      Entry entry;
      String key;
      while(e.hasNext()) {
         entry = (Entry)e.next();
         key = ((LocaleEntry)entry.getKey()).key();
         if(messages.containsKey(entry.getKey())) {
            newLocaleYaml.set(buildPath(new String[]{"backup", "messages", key}), messages.get(entry.getKey()));
            messages.put(new LocaleEntry(((LocaleEntry)entry.getKey()).newkey(), "1.1.3"), entry.getValue());
            if(((LocaleEntry)entry.getKey()).hasNewkey()) {
               messages.remove(entry.getKey());
            }
         } else {
            newLocaleYaml.set(buildPath(new String[]{"messages", key}), entry.getValue());
         }
      }

      e = this.keywords.entrySet().iterator();

      while(e.hasNext()) {
         entry = (Entry)e.next();
         key = ((LocaleEntry)entry.getKey()).key();
         if(keywords.containsKey(entry.getKey())) {
            newLocaleYaml.set(buildPath(new String[]{"backup", "keywords", key.substring(1)}), keywords.get(entry.getKey()));
            String newKey = ((LocaleEntry)entry.getKey()).newkey();
            if(newKey.startsWith("#")) {
               newKey = newKey.substring(1);
            }

            keywords.put(new LocaleEntry("#" + newKey, "1.1.3"), entry.getValue());
            if(((LocaleEntry)entry.getKey()).hasNewkey()) {
               keywords.remove(entry.getKey());
            }
         } else {
            newLocaleYaml.set(buildPath(new String[]{"keywords", key.substring(1)}), entry.getValue());
         }
      }

      e = this.ui.entrySet().iterator();

      while(e.hasNext()) {
         entry = (Entry)e.next();
         key = ((LocaleEntry)entry.getKey()).key();
         if(uiSettings.containsKey(entry.getKey())) {
            newLocaleYaml.set(buildPath(new String[]{"backup", "lores", key, "name"}), ((LocaleItem)uiSettings.get(entry.getKey())).name());
            newLocaleYaml.set(buildPath(new String[]{"backup", "lores", key, "lore"}), ((LocaleItem)uiSettings.get(entry.getKey())).lore());
            uiSettings.put(new LocaleEntry(((LocaleEntry)entry.getKey()).newkey(), "1.1.3"), entry.getValue());
            if(((LocaleEntry)entry.getKey()).hasNewkey()) {
               messages.remove(entry.getKey());
            }
         } else {
            newLocaleYaml.set(buildPath(new String[]{"ui", ((LocaleEntry)entry.getKey()).key(), "name"}), ((LocaleItem)entry.getValue()).name());
            newLocaleYaml.set(buildPath(new String[]{"ui", ((LocaleEntry)entry.getKey()).key(), "lore"}), ((LocaleItem)entry.getValue()).lore());
         }
      }

      e = messages.entrySet().iterator();

      while(e.hasNext()) {
         entry = (Entry)e.next();
         newLocaleYaml.set(buildPath(new String[]{"messages", ((LocaleEntry)entry.getKey()).key()}), entry.getValue());
      }

      e = keywords.entrySet().iterator();

      while(e.hasNext()) {
         entry = (Entry)e.next();
         newLocaleYaml.set(buildPath(new String[]{"keywords", ((LocaleEntry)entry.getKey()).key().substring(1)}), entry.getValue());
      }

      e = uiSettings.entrySet().iterator();

      while(e.hasNext()) {
         entry = (Entry)e.next();
         newLocaleYaml.set(buildPath(new String[]{"ui", ((LocaleEntry)entry.getKey()).key(), "name"}), ((LocaleItem)entry.getValue()).name());
         newLocaleYaml.set(buildPath(new String[]{"ui", ((LocaleEntry)entry.getKey()).key(), "lore"}), ((LocaleItem)entry.getValue()).lore());
      }

      try {
         newLocaleYaml.save(file);
      } catch (Exception var10) {
         var10.printStackTrace();
      }

   }

   public static String buildPath(String ... path) {
      StringBuilder builder = new StringBuilder();
      boolean first = true;
      char separator = 47;
      String[] var4 = path;
      int var5 = path.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         String node = var4[var6];
         if(!first) {
            builder.append(separator);
         }

         builder.append(node);
         first = false;
      }

      return builder.toString();
   }
}
