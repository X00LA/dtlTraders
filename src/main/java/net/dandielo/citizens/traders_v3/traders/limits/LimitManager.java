package net.dandielo.citizens.traders_v3.traders.limits;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.dandielo.citizens.traders_v3.core.dB;
import net.dandielo.citizens.traders_v3.traders.limits.LimitEntry;
import net.dandielo.citizens.traders_v3.traders.stock.StockItem;
import net.dandielo.citizens.traders_v3.utils.items.attributes.Limit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class LimitManager {

   public static final LimitManager self = new LimitManager();
   private File limits_file;
   private FileConfiguration limits_yaml;
   private Map limits = new HashMap();


   public void init() {
      String path = "plugins/dtlTraders";
      this.limits_file = new File(path, "limits.yml");
      if(!this.limits_file.exists()) {
         try {
            this.limits_file.createNewFile();
         } catch (IOException var3) {
            dB.high(new Object[]{"Cannot create limits.yml file for the limits feature"});
         }
      }

      this.load();
   }

   void load() {
      this.limits_yaml = new YamlConfiguration();

      try {
         this.limits_yaml.load(this.limits_file);
         Iterator e = this.limits_yaml.getKeys(false).iterator();

         while(e.hasNext()) {
            String id = (String)e.next();
            ConfigurationSection cs = this.limits_yaml.getConfigurationSection(id);
            LimitEntry entry = new LimitEntry(id, cs.getInt("limit"), cs.getLong("timeout"), cs.getInt("plimit"), cs.getLong("ptimeout"));
            ConfigurationSection pl = cs.getConfigurationSection("players");
            Iterator var6 = pl.getKeys(false).iterator();

            while(var6.hasNext()) {
               String player = (String)var6.next();
               ConfigurationSection en = pl.getConfigurationSection(player);
               Iterator var9 = en.getKeys(false).iterator();

               while(var9.hasNext()) {
                  String time = (String)var9.next();
                  entry.playerLoad(player, Long.parseLong(time), en.getInt(time));
               }
            }

            this.limits.put(id, entry);
         }
      } catch (Exception var11) {
         var11.printStackTrace();
      }

   }

   public void save() {
      try {
         this.limits_yaml = new YamlConfiguration();
         Iterator e = this.limits.entrySet().iterator();

         while(e.hasNext()) {
            Entry entry = (Entry)e.next();
            String id = (String)entry.getKey();
            LimitEntry limit = (LimitEntry)entry.getValue();
            this.limits_yaml.set(id + ".limit", Integer.valueOf(limit.getLimit()));
            this.limits_yaml.set(id + ".timeout", Long.valueOf(limit.getTimeout()));
            this.limits_yaml.set(id + ".plimit", Integer.valueOf(limit.getPlayerLimit()));
            this.limits_yaml.set(id + ".ptimeout", Long.valueOf(limit.getPlayerTimeout()));
            this.limits_yaml.set(id + ".players", limit.playerEntries());
         }

         this.limits_yaml.save(this.limits_file);
      } catch (IOException var5) {
         dB.high(new Object[]{"Cannot save to limits.yml!"});
      }

   }

   public void refreshAll() {
      Iterator var1 = this.limits.values().iterator();

      while(var1.hasNext()) {
         LimitEntry entry = (LimitEntry)var1.next();
         entry.limitRefresh();
      }

   }

   public boolean checkLimit(Player player, StockItem item, int amount, String type) {
      if(item.hasAttribute(Limit.class)) {
         Limit lm = (Limit)item.getAttribute(Limit.class, false);
         LimitEntry entry = (LimitEntry)this.limits.get(lm.getID());
         if(entry == null) {
            entry = new LimitEntry(lm.getID(), lm.getLimit(), lm.getTimeout());
         }

         entry.limitRefresh();
         boolean result = true;
         result = entry.isAvailable(type, amount);
         result = result?entry.isPlayerAvailable(player.getName(), type, amount):result;
         this.limits.put(lm.getID(), entry);
         return result;
      } else {
         return true;
      }
   }

   public void updateLimit(Player player, StockItem item, int amount, String type) {
      if(item.hasAttribute(Limit.class)) {
         Limit lm = (Limit)item.getAttribute(Limit.class, false);
         LimitEntry entry = (LimitEntry)this.limits.get(lm.getID());
         if(entry == null) {
            entry = new LimitEntry(lm.getID(), lm.getLimit(), lm.getTimeout());
         }

         entry.playerUpdate(player.getName(), type, amount);
         this.limits.put(lm.getID(), entry);
      }

   }

   public long getTotalLimit(StockItem item) {
      if(item.hasAttribute(Limit.class)) {
         Limit lm = (Limit)item.getAttribute(Limit.class, false);
         LimitEntry entry = (LimitEntry)this.limits.get(lm.getID());
         if(entry == null) {
            entry = new LimitEntry(lm.getID(), lm.getLimit(), lm.getTimeout());
         }

         return (long)entry.getLimit();
      } else {
         return 0L;
      }
   }

   public long getPlayerLimit(StockItem item) {
      if(item.hasAttribute(Limit.class)) {
         Limit lm = (Limit)item.getAttribute(Limit.class, false);
         LimitEntry entry = (LimitEntry)this.limits.get(lm.getID());
         if(entry == null) {
            entry = new LimitEntry(lm.getID(), lm.getLimit(), lm.getTimeout());
         }

         return (long)entry.getPlayerLimit();
      } else {
         return 0L;
      }
   }

   public int getTotalUsed(StockItem item) {
      if(item.hasAttribute(Limit.class)) {
         Limit lm = (Limit)item.getAttribute(Limit.class, false);
         LimitEntry entry = (LimitEntry)this.limits.get(lm.getID());
         if(entry == null) {
            entry = new LimitEntry(lm.getID(), lm.getLimit(), lm.getTimeout());
         }

         return entry.totalUsed();
      } else {
         return 0;
      }
   }

   public int getPlayerUsed(String player, StockItem item) {
      if(item.hasAttribute(Limit.class)) {
         Limit lm = (Limit)item.getAttribute(Limit.class, false);
         LimitEntry entry = (LimitEntry)this.limits.get(lm.getID());
         if(entry == null) {
            entry = new LimitEntry(lm.getID(), lm.getLimit(), lm.getTimeout());
         }

         return entry.totalPlayer(player);
      } else {
         return 0;
      }
   }

   public static long parseTimeout(String raw) {
      long result = 0L;
      Matcher m = Pattern.compile("(?:(\\d*)d)*(?:(\\d*)h)*(?:(\\d*)m)*(?:(\\d*)s)*").matcher(raw);
      if(m.matches()) {
         if(m.group(1) != null) {
            result += Long.parseLong(m.group(1)) * 24L * 60L * 60L;
         }

         if(m.group(2) != null) {
            result += Long.parseLong(m.group(2)) * 60L * 60L;
         }

         if(m.group(3) != null) {
            result += Long.parseLong(m.group(3)) * 60L;
         }

         if(m.group(4) != null) {
            result += Long.parseLong(m.group(4));
         }
      }

      return result;
   }

   public static String timeoutString(long raw) {
      String result = "";
      if(raw % 60L != 0L) {
         result = raw % 60L + "s";
      }

      if((raw /= 60L) % 60L != 0L) {
         result = raw % 60L + "m" + result;
      }

      if((raw /= 60L) % 24L != 0L) {
         result = raw % 24L + "h" + result;
      }

      if((raw /= 24L) != 0L) {
         result = raw + "d" + result;
      }

      return result;
   }

}
