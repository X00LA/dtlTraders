package net.dandielo.citizens.traders_v3.traders.setting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.dandielo.citizens.traders_v3.core.PluginSettings;
import net.dandielo.citizens.traders_v3.core.dB;
import net.dandielo.citizens.traders_v3.core.locale.LocaleManager;
import net.dandielo.citizens.traders_v3.core.tools.StringTools;
import net.dandielo.citizens.traders_v3.traders.limits.LimitManager;
import net.dandielo.citizens.traders_v3.utils.ItemUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GlobalSettings extends PluginSettings {

   protected static ConfigurationSection tConfig;
   protected static boolean doubleClick;
   protected static Map specialBlocks = new HashMap();
   protected static Map timeoutBlocks = new HashMap();
   protected static String mmStockStart;
   protected static ItemStack mmItemToggle;
   protected static boolean mmRightToggle;
   protected static boolean mmEnableDamage;
   protected static int stockSize;
   protected static String stockNameFormat;
   protected static String stockStart;
   protected static String walletType;
   protected static double walletMoney;
   protected static List defaultPatterns;
   protected static String patternFile;
   protected static int playerTraderLimit;
   protected static int playerStockSize;
   protected static String playerStockNameFormat;
   protected static Map uiItems = new HashMap();


   public static void initGlobalSettings() {
      dB.info(new Object[]{"Loading general trader configuration"});
      tConfig = config.getConfigurationSection("trader");
      doubleClick = tConfig.getBoolean("transaction.double-click", false);
      mmStockStart = tConfig.getString("managing.start-stock", "sell");
      mmItemToggle = ItemUtils.createItemStack(tConfig.getString("managing.item", "air"));
      mmRightToggle = tConfig.getBoolean("managing.right-click", false);

      List e;
      Iterator var1;
      String entry;
      String[] data;
      try {
         e = tConfig.getList("managing.special-blocks");
         var1 = e.iterator();

         while(var1.hasNext()) {
            entry = (String)var1.next();
            data = entry.split(" ");
            specialBlocks.put(ItemUtils.createItemStack(data[0]), Double.valueOf(Double.parseDouble(data[1])));
         }
      } catch (Exception var6) {
         dB.high(new Object[]{"While loading special blocks, a exception occured"});
         dB.high(new Object[]{"Exception: ", var6.getClass().getSimpleName()});
         dB.normal(new Object[]{"Exception message: ", var6.getMessage()});
         dB.normal(new Object[]{"StackTrace: ", var6.getStackTrace()});
      }

      try {
         e = tConfig.getList("managing.time-blocks");
         var1 = e.iterator();

         while(var1.hasNext()) {
            entry = (String)var1.next();
            data = entry.split(" ", 2);
            timeoutBlocks.put(ItemUtils.createItemStack(data[0]), Long.valueOf(LimitManager.parseTimeout(data[1])));
         }
      } catch (Exception var5) {
         dB.high(new Object[]{"While loading timeout-blocks blocks, a exception occured"});
         dB.high(new Object[]{"Exception: ", var5.getClass().getSimpleName()});
         dB.normal(new Object[]{"Exception message: ", var5.getMessage()});
         dB.normal(new Object[]{"StackTrace: ", StringTools.stackTrace(var5.getStackTrace())});
      }

      stockStart = tConfig.getString("stock.start-stock", "sell");
      stockSize = tConfig.getInt("stock.size", 6);
      stockNameFormat = tConfig.getString("stock.format", "{npc}\'s shop");
      walletType = tConfig.getString("wallet.type", "infinite");
      walletMoney = tConfig.getDouble("wallet.money", 0.0D);

      try {
         defaultPatterns = tConfig.getList("pattern.defaults", new ArrayList());
         patternFile = tConfig.getString("pattern.file", "patterns.yml");
      } catch (Exception var4) {
         dB.high(new Object[]{"While loading pattern defaults, a exception occured"});
         dB.high(new Object[]{"Exception: ", var4.getClass().getSimpleName()});
         dB.normal(new Object[]{"Exception message: ", var4.getMessage()});
         dB.normal(new Object[]{"StackTrace: ", StringTools.stackTrace(var4.getStackTrace())});
      }

      playerTraderLimit = tConfig.getInt("player.limit", 1);
      playerStockSize = tConfig.getInt("player.size", 6);
      playerStockNameFormat = tConfig.getString("player.format", "{npc}\'s shop");
      uiItems.put("sell", asUIItem("ui.sell", "wool:1"));
      uiItems.put("buy", asUIItem("ui.buy", "wool:2"));
      uiItems.put("back", asUIItem("ui.back", "wool:14"));
      uiItems.put("price", asUIItem("ui.price", "wool:15"));
      uiItems.put("limit", asUIItem("ui.limit", "wool:3"));
      uiItems.put("plimit", asUIItem("ui.plimit", "wool:6"));
      uiItems.put("lock", asUIItem("ui.lock", "wool:4"));
      uiItems.put("unlock", asUIItem("ui.unlock", "wool:5"));
   }

   private static ItemStack asUIItem(String ID, String defID) {
      ItemStack item = ItemUtils.createItemStack(tConfig.getString(ID, defID));
      ItemMeta meta = item.getItemMeta();
      meta.setDisplayName(LocaleManager.locale.getName(ID.substring(3)));
      meta.setLore(LocaleManager.locale.getLore(ID.substring(3)));
      item.setItemMeta(meta);
      return item;
   }

   public static String getGlobalStockNameFormat() {
      return stockNameFormat;
   }

   public static String getGlobalStockStart() {
      return stockStart;
   }

   public static int getGlobalStockSize() {
      return stockSize;
   }

   public static boolean mmRightToggle() {
      return mmRightToggle;
   }

   public static boolean mmEnableDamage() {
      return mmEnableDamage;
   }

   public static ItemStack mmItemToggle() {
      return mmItemToggle;
   }

   public static boolean dClickEvent() {
      return doubleClick;
   }

   public static double getBlockValue(ItemStack item) {
      ItemStack tempItem = new ItemStack(item.getType());
      tempItem.setAmount(1);
      return specialBlocks.containsKey(tempItem)?((Double)specialBlocks.get(tempItem)).doubleValue():1.0D;
   }

   public static long getBlockTimeoutValue(ItemStack item) {
      ItemStack tempItem = new ItemStack(item.getType());
      tempItem.setAmount(1);
      return timeoutBlocks.containsKey(tempItem)?((Long)timeoutBlocks.get(tempItem)).longValue():1L;
   }

   public static Map getUiItems() {
      return uiItems;
   }

   public static String getDefaultWallet() {
      return walletType;
   }

   public static double getWalletStartBalance() {
      return walletMoney;
   }

   public static String getPatternFile() {
      return patternFile;
   }

   public static List defaultPatterns() {
      return defaultPatterns;
   }

}
