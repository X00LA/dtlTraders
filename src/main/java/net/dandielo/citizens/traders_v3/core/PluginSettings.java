package net.dandielo.citizens.traders_v3.core;

import net.dandielo.citizens.traders_v3.bukkit.DtlTraders;
import net.dandielo.citizens.traders_v3.core.dB;
import org.bukkit.configuration.file.FileConfiguration;

public class PluginSettings {

   protected static FileConfiguration config;
   private static String debugLevel = "none";
   private static String locale;
   private static boolean localeAutoUpdate;
   private static int cleaningTimeout = 4;
   private static boolean statistics = false;


   public static void initPluginSettings() {
      config = DtlTraders.getInstance().getConfig();
      debugLevel = config.getString("debug", "normal");
      dB.info(new Object[]{"Loading plugin settings"});
      locale = config.getString("locale.load", "en");
      localeAutoUpdate = config.getBoolean("locale.auto-update", true);
      cleaningTimeout = config.getInt("trader.transaction.cleaning", 4);
      statistics = config.getBoolean("general.statistics", false);
   }

   public static String getLocale() {
      return locale;
   }

   public static boolean autoUpdateLocale() {
      return localeAutoUpdate;
   }

   public static boolean statisticsEnabled() {
      return statistics;
   }

   public static int cleaningTimeout() {
      return cleaningTimeout;
   }

   public static String debugLevel() {
      return debugLevel.toUpperCase();
   }

}
