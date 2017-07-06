package net.dandielo.citizens.traders_v3.traders.stock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.citizensnpcs.api.util.DataKey;
import net.dandielo.citizens.traders_v3.TEntityStatus;
import net.dandielo.citizens.traders_v3.bukkit.Perms;
import net.dandielo.citizens.traders_v3.core.dB;
import net.dandielo.citizens.traders_v3.traders.patterns.Pattern;
import net.dandielo.citizens.traders_v3.traders.patterns.PatternManager;
import net.dandielo.citizens.traders_v3.traders.patterns.types.ItemPattern;
import net.dandielo.citizens.traders_v3.traders.setting.Settings;
import net.dandielo.citizens.traders_v3.traders.stock.StockItem;
import net.dandielo.citizens.traders_v3.traders.stock.StockPlayer;
import net.dandielo.citizens.traders_v3.utils.items.attributes.PatternItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public abstract class Stock implements InventoryHolder {

   protected Map stock = new HashMap();
   protected Settings settings;


   protected Stock(Settings settings) {
      int size = settings.getStockSize();
      dB.info(new Object[]{"Creating stock with name: ", settings.getStockName(), ", size: ", Integer.valueOf(size)});
      if(size > 0 && size <= 6) {
         this.settings = settings;
         this.stock.put("sell", new ArrayList());
         this.stock.put("buy", new ArrayList());
      } else {
         throw new IllegalArgumentException("Size must be between 1 and 6");
      }
   }

   public StockPlayer toPlayerStock(Player player) {
      dB.high(new Object[]{"Creating player specific stock"});
      StockPlayer stock = new StockPlayer(this.settings, player);
      stock.stock = this.stock;
      dB.high(new Object[]{"Creating player specific stock"});
      Iterator it = ((List)stock.stock.get("sell")).iterator();

      while(it.hasNext()) {
         if(((StockItem)it.next()).hasAttribute(PatternItem.class)) {
            it.remove();
         }
      }

      it = ((List)stock.stock.get("buy")).iterator();

      while(it.hasNext()) {
         if(((StockItem)it.next()).hasAttribute(PatternItem.class)) {
            it.remove();
         }
      }

      dB.high(new Object[]{"Creating player specific stock"});
      if(this.settings.getPatterns() != null && !this.settings.getPatterns().isEmpty()) {
         dB.high(new Object[]{"Loaded all needed patterns"});
         List patterns = PatternManager.getPatterns(this.settings.getPatterns());
         if(!patterns.isEmpty()) {
            Iterator var5 = patterns.iterator();

            while(var5.hasNext()) {
               Pattern pattern = (Pattern)var5.next();
               dB.high(new Object[]{"Check permission for pattern: " + pattern.getName()});
               if(pattern.getType().equals(Pattern.Type.ITEM) && Perms.hasPerm(player, "dtl.trader.patterns." + pattern.getName())) {
                  dB.high(new Object[]{"Update stock with pattern: " + pattern.getName()});
                  ((ItemPattern)pattern).updateStock((List)stock.stock.get("sell"), "sell", player);
                  ((ItemPattern)pattern).updateStock((List)stock.stock.get("buy"), "buy", player);
               }
            }
         }
      }

      return stock;
   }

   public final int getFinalInventorySize() {
      return this.settings.getStockSize() * 9;
   }

   public boolean isUiSlot(int slot) {
      return slot < this.getFinalInventorySize() && slot >= this.getFinalInventorySize() - 4;
   }

   public void clearStock(String stock) {
      this.stock.put(stock, new ArrayList());
   }

   public void load(DataKey data) {}

   public void save(DataKey data) {}

   public StockItem getItem(int slot, String stock) {
      StockItem resultItem = null;
      Iterator var4 = ((List)this.stock.get(stock)).iterator();

      while(var4.hasNext()) {
         StockItem stockItem = (StockItem)var4.next();
         if(stockItem.getSlot() == slot) {
            resultItem = stockItem;
         }
      }

      return resultItem;
   }

   public StockItem getItem(StockItem item, String stock) {
      Iterator var3 = ((List)this.stock.get(stock)).iterator();

      StockItem sItem;
      do {
         if(!var3.hasNext()) {
            return null;
         }

         sItem = (StockItem)var3.next();
      } while(!sItem.getItem().getType().equals(item.getItem().getType()) && !sItem.similar(item));

      return sItem;
   }

   public static void saveNewAmounts(Inventory inventory, StockItem si) {
      si.getAmounts().clear();
      ItemStack[] var2 = inventory.getContents();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         ItemStack is = var2[var4];
         if(is != null) {
            si.addAmount(is.getAmount());
         }
      }

      if(si.getAmounts().size() > 1) {
         si.getAmounts().remove(si.getAmounts().size() - 1);
      }

      si.setAmount(si.getAmount());
   }

   public abstract void addItem(StockItem var1, String var2);

   public abstract void removeItem(StockItem var1, String var2);

   public abstract Inventory getInventory(TEntityStatus var1);

   public abstract Inventory getManagementInventory(TEntityStatus var1, TEntityStatus var2);

   public abstract void setInventory(Inventory var1, TEntityStatus var2);

   public abstract void setAmountsInventory(Inventory var1, TEntityStatus var2, StockItem var3);

   public abstract void setManagementInventory(Inventory var1, TEntityStatus var2, TEntityStatus var3);

   public static String opositeStock(String stock) {
      return stock.equals("sell")?"buy":"sell";
   }

   public List getStock(String stock) {
      return (List)this.stock.get(stock);
   }
}
