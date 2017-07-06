package net.dandielo.citizens.traders_v3.core.locale;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.dandielo.citizens.traders_v3.bukkit.DtlTraders;
import net.dandielo.citizens.traders_v3.core.PluginSettings;
import net.dandielo.citizens.traders_v3.core.dB;
import net.dandielo.citizens.traders_v3.core.locale.LocaleEntry;
import net.dandielo.citizens.traders_v3.core.locale.LocaleItem;
import net.dandielo.citizens.traders_v3.core.locale.LocaleUpdater;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class LocaleManager {

   public static final LocaleManager locale = new LocaleManager();
   public static final String localeVersion = "1.1.3";
   private Map messages;
   private Map keywords;
   private Map ui;
   private LocaleUpdater updater;
   protected static final char PATH_SEPARATOR = '/';
   protected FileConfiguration localeYaml;
   protected File localeFile;


   private LocaleManager() {
      dB.info(new Object[]{"Initializing locale manager"});
      this.updater = new LocaleUpdater(this.localeChangeConfiguration().getDefaults());
      this.messages = new HashMap();
      this.keywords = new LocaleManager.KeywordMap();
      this.ui = new HashMap();
      this.loadFile();
   }

   public void loadFile() {
      dB.info(new Object[]{"Loading locale file"});
      String name = "locale." + PluginSettings.getLocale() + ".yml";
      String path = "plugins/dtlTraders/locale";
      File baseDirectory = new File(path);
      if(!baseDirectory.exists()) {
         baseDirectory.mkdirs();
      }

      this.localeFile = new File(path, name);
      if(!this.localeFile.exists()) {
         try {
            this.localeFile.createNewFile();
            InputStream e = DtlTraders.getInstance().getResource("locales/locale." + PluginSettings.getLocale() + ".yml");
            if(e != null) {
               YamlConfiguration yconfig = YamlConfiguration.loadConfiguration(new InputStreamReader(e));
               this.localeYaml = new YamlConfiguration();
               this.localeYaml.setDefaults(yconfig);
               this.localeYaml.options().copyDefaults(true);
            }

            this.save();
         } catch (IOException var6) {
            dB.critical(new Object[]{"While loading locale file, an exception occured"});
            dB.normal(new Object[]{"Exception message: ", var6.getClass().getSimpleName()});
            dB.high(new Object[]{"Filename: ", name, ", path to file", path});
            dB.normal(new Object[]{"Exception message: ", var6.getMessage()});
            dB.normal(new Object[]{"StackTrace: ", var6.getStackTrace()});
         }
      }

      this.load();
   }

   public YamlConfiguration localeChangeConfiguration() {
      dB.info(new Object[]{"Loading locale changes"});
      InputStream stream = DtlTraders.getInstance().getResource("locale.changes");
      YamlConfiguration locale = null;
      if(stream != null) {
         YamlConfiguration yconfig = YamlConfiguration.loadConfiguration(new InputStreamReader(stream));
         locale = new YamlConfiguration();
         locale.setDefaults(yconfig);
         locale.options().copyDefaults(true);
      }

      if(locale != null) {
         locale.options().pathSeparator('/');
      }

      return locale;
   }

   public void load() {
      dB.info(new Object[]{"Loading yaml configuration"});
      this.load(PluginSettings.autoUpdateLocale());
   }

   public void load(boolean update) {
      this.localeYaml = new YamlConfiguration();
      this.localeYaml.options().pathSeparator('/');

      try {
         FileInputStream e = new FileInputStream(this.localeFile);
         this.localeYaml.load(new InputStreamReader(e));
         String currentVersion = this.localeYaml.getString("ver");
         this.messages.clear();
         this.keywords.clear();
         this.ui.clear();
         this.loadMessages(currentVersion, this.localeYaml.getConfigurationSection("messages"));
         this.loadKeywords(currentVersion, this.localeYaml.getConfigurationSection("keywords"));
         this.loadUIConfigs(currentVersion, this.localeYaml.getConfigurationSection("ui"));
         if((currentVersion == null || !currentVersion.equals("1.1.3")) && update) {
            this.updater.update(this.messages, this.keywords, this.ui, this.localeFile);
            this.load(false);
         }
      } catch (Exception var4) {
         dB.critical(new Object[]{"While reading the locale file, an exception occured"});
         dB.high(new Object[]{"Exception message: ", var4.getClass().getSimpleName()});
         dB.high(new Object[]{"On update: ", Boolean.valueOf(update)});
         dB.normal(new Object[]{"Exception message: ", var4.getMessage()});
         dB.normal(new Object[]{"StackTrace: ", var4.getStackTrace()});
      }

   }

   protected void loadMessages(String currentVersion, ConfigurationSection config) {
      dB.info(new Object[]{"Loading locale messages"});
      if(config != null) {
         Iterator var3 = config.getKeys(false).iterator();

         while(var3.hasNext()) {
            String key = (String)var3.next();
            this.messages.put(new LocaleEntry(key, currentVersion), config.getString(key));
         }

      }
   }

   protected void loadKeywords(String currentVersion, ConfigurationSection config) {
      dB.info(new Object[]{"Loading locale keywords"});
      if(config != null) {
         Iterator var3 = config.getKeys(false).iterator();

         while(var3.hasNext()) {
            String key = (String)var3.next();
            this.keywords.put(new LocaleEntry("#" + key, currentVersion), config.getString(key));
         }

      }
   }

   protected void loadUIConfigs(String currentVersion, ConfigurationSection config) {
      dB.info(new Object[]{"Loading locale UI configs"});
      if(config != null) {
         Iterator var3 = config.getKeys(false).iterator();

         while(var3.hasNext()) {
            String key = (String)var3.next();
            this.ui.put(new LocaleEntry(key, currentVersion), new LocaleItem(config.getString(buildPath(new String[]{key, "name"})), config.getStringList(buildPath(new String[]{key, "lore"}))));
         }

      }
   }

   public void save() {
      dB.info(new Object[]{"Saving locale YAML configuration to file"});

      try {
         this.localeYaml.save(this.localeFile);
      } catch (IOException var2) {
         dB.high(new Object[]{"While saving the locale YAML configuration, an exception occured"});
         dB.high(new Object[]{"Exception: ", var2.getClass().getSimpleName()});
         dB.normal(new Object[]{"Exception message: ", var2.getMessage()});
         dB.normal(new Object[]{"Stack trace: ", var2.getStackTrace()});
      }

   }

   public void sendMessage(CommandSender sender, String key, Object ... args) {
      dB.low(new Object[]{"Preparing message to: ", sender.getName(), ", message key: ", key});
      dB.low(new Object[]{"With arguments: ", args});
      this.checkMessageKey(key);
      String message = (String)this.messages.get(new LocaleEntry(key, "1.1.3"));
      int i = 0;

      while(i < args.length) {
         dB.info(new Object[]{"Checking ", Integer.valueOf(i + 1), " message argument: ", args[i]});
         dB.info(new Object[]{"Checking ", Integer.valueOf(i + 2), " message argument: ", args[i + 1]});
         if(args[i] instanceof String) {
            dB.info(new Object[]{"Valid tag"});
            this.checkKeywordKey((String)args[i + 1]);
            message = message.replaceAll("\\{" + (String)args[i] + "\\}", (String)this.keywords.get(new LocaleEntry((String)args[i + 1], "1.1.3")));
            i += 2;
         } else {
            ++i;
         }
      }

      dB.low(new Object[]{"Sending message to: ", sender.getName(), ", message key: ", key});
      sender.sendMessage(message.replace('^', '\u00a7'));
   }

   public String getMessage(String key, Object ... args) {
      dB.low(new Object[]{"Preparing message, key: ", key});
      dB.low(new Object[]{"With arguments: ", args});
      this.checkMessageKey(key);
      String message = (String)this.messages.get(new LocaleEntry(key, "1.1.3"));
      int i = 0;

      while(i < args.length) {
         if(args[i] instanceof String) {
            this.checkKeywordKey((String)args[i + 1]);
            message = message.replaceAll("\\{" + (String)args[i] + "\\}", (String)this.keywords.get(new LocaleEntry((String)args[i + 1], "1.1.3")));
            i += 2;
         } else {
            ++i;
         }
      }

      return message.replace('^', '\u00a7').replace('^', '\u00a7');
   }

   public String getKeyword(String keyword) {
      return (String)this.keywords.get(new LocaleEntry("#" + keyword, "1.1.3"));
   }

   public void checkMessageKey(String key) {
      dB.info(new Object[]{"Checking message key: ", key});
      if(!this.messages.containsKey(new LocaleEntry(key, "1.1.3"))) {
         dB.low(new Object[]{"Message key not found: ", key});
         this.localeYaml.set(buildPath(new String[]{"messages", key}), "^3Check the locale, this message is not set!");
         this.messages.put(new LocaleEntry(key, "1.1.3"), "^3Check the locale, this message is not set!");
         this.save();
      }

   }

   public void checkKeywordKey(String key) {
      dB.info(new Object[]{"Checking keyword key: ", key});
      if(!this.keywords.containsKey(new LocaleEntry(key, "1.1.3")) && key.startsWith("#")) {
         dB.low(new Object[]{"Keyword key not found: ", key});
         this.localeYaml.set(buildPath(new String[]{"keywords", key.substring(1)}), "^3Invalid keyword!");
         this.keywords.put(new LocaleEntry(key, "1.1.3"), "^3Invalid keyword!");
         this.save();
      }

   }

   public List getLore(String key) {
      dB.info(new Object[]{"Getting lore for UI item: ", key});
      ArrayList list = new ArrayList();
      if(this.ui.containsKey(new LocaleEntry(key, "1.1.3"))) {
         Iterator var3 = ((LocaleItem)this.ui.get(new LocaleEntry(key, "1.1.3"))).lore().iterator();

         while(var3.hasNext()) {
            String l = (String)var3.next();
            list.add("§3§d§d§f" + l.replace('&', '\u00a7').replace('^', '\u00a7'));
         }
      } else {
         dB.high(new Object[]{"Missing Locale: " + key});
      }

      return list;
   }

   public String getName(String key) {
      dB.info(new Object[]{"Getting name for UI item: ", key});
      String name = "";
      if(this.ui.containsKey(new LocaleEntry(key, "1.1.3"))) {
         name = ((LocaleItem)this.ui.get(new LocaleEntry(key, "1.1.3"))).name();
      } else {
         dB.high(new Object[]{"Missing Locale: " + key});
      }

      return name.replace('^', '\u00a7').replace('^', '\u00a7');
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


   protected class KeywordMap extends HashMap {

      private static final long serialVersionUID = -3449939627787377766L;


      public String get(Object key) {
         return ((LocaleEntry)key).key().startsWith("#")?(String)super.get(key):((LocaleEntry)key).key();
      }
   }
}
