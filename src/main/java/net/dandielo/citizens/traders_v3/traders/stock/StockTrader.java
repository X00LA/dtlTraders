package net.dandielo.citizens.traders_v3.traders.stock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.citizensnpcs.api.util.DataKey;
import net.dandielo.citizens.traders_v3.TEntityStatus;
import net.dandielo.citizens.traders_v3.core.dB;
import net.dandielo.citizens.traders_v3.traders.setting.GlobalSettings;
import net.dandielo.citizens.traders_v3.traders.setting.Settings;
import net.dandielo.citizens.traders_v3.utils.items.attributes.PatternItem;
import net.dandielo.core.bukkit.NBTUtils;
import net.dandielo.core.items.serialize.core.Enchants;
import net.dandielo.core.items.serialize.core.Name;
import net.dandielo.core.items.serialize.core.Skull;
import net.dandielo.core.items.serialize.core.StoredEnchant;
import net.dandielo.core.items.serialize.flags.Lore;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class StockTrader extends Stock {

   public StockTrader(Settings settings) {
      super(settings);
   }

   public void addItem(StockItem item, String stock) {
      ((List)this.stock.get(stock)).add(item);
   }

   public void removeItem(StockItem item, String stock) {
      ((List)this.stock.get(stock)).remove(item);
   }

   public void load(DataKey data) {
      dB.info(new Object[]{"Loading trader specific stock"});
      Iterator var2;
      Object item;
      StockItem stockItem;
      Iterator var5;
      Entry entry;
      if(data.keyExists("sell")) {
         var2 = ((List)data.getRaw("sell")).iterator();

         while(var2.hasNext()) {
            item = var2.next();
            if(item instanceof String) {
               stockItem = new StockItem((String)item);
               if(stockItem.getSlot() < 0) {
                  ((List)this.stock.get("sell")).add(stockItem);
               } else {
                  ((List)this.stock.get("sell")).add(0, stockItem);
               }
            } else {
               stockItem = null;

               for(var5 = ((Map)item).entrySet().iterator(); var5.hasNext(); stockItem = new StockItem((String)entry.getKey(), (List)entry.getValue())) {
                  entry = (Entry)var5.next();
               }

               if(stockItem.getSlot() < 0) {
                  ((List)this.stock.get("sell")).add(stockItem);
               } else {
                  ((List)this.stock.get("sell")).add(0, stockItem);
               }
            }
         }
      }

      if(data.keyExists("buy")) {
         var2 = ((List)data.getRaw("buy")).iterator();

         while(var2.hasNext()) {
            item = var2.next();
            if(item instanceof String) {
               stockItem = new StockItem((String)item);
               if(stockItem.getSlot() < 0) {
                  ((List)this.stock.get("buy")).add(stockItem);
               } else {
                  ((List)this.stock.get("buy")).add(0, stockItem);
               }
            } else {
               stockItem = null;

               for(var5 = ((Map)item).entrySet().iterator(); var5.hasNext(); stockItem = new StockItem((String)entry.getKey(), (List)entry.getValue())) {
                  entry = (Entry)var5.next();
               }

               if(stockItem.getSlot() < 0) {
                  ((List)this.stock.get("buy")).add(stockItem);
               } else {
                  ((List)this.stock.get("buy")).add(0, stockItem);
               }
            }
         }
      }

   }

   public void save(DataKey data) {
      dB.info(new Object[]{"Saving traders stock"});
      ArrayList sellList = new ArrayList();
      Iterator buyList = ((List)this.stock.get("sell")).iterator();

      while(buyList.hasNext()) {
         StockItem item = (StockItem)buyList.next();
         if(!item.hasAttribute(PatternItem.class)) {
            if(item.hasFlag(Lore.class)) {
               HashMap item1 = new HashMap();
               item1.put(item.toString(), this.escapeLore(item.getLore()));
               sellList.add(item1);
            } else {
               sellList.add(item.toString());
            }
         }
      }

      ArrayList buyList1 = new ArrayList();
      Iterator item2 = ((List)this.stock.get("buy")).iterator();

      while(item2.hasNext()) {
         StockItem item3 = (StockItem)item2.next();
         if(!item3.hasAttribute(PatternItem.class)) {
            if(item3.hasFlag(Lore.class)) {
               HashMap temp = new HashMap();
               temp.put(item3.toString(), this.escapeLore(item3.getLore()));
               buyList1.add(temp);
            } else {
               buyList1.add(item3.toString());
            }
         }
      }

      data.setRaw("sell", sellList);
      data.setRaw("buy", buyList1);
   }

   protected List escapeLore(List lore) {
      if(lore == null) {
         return null;
      } else {
         ArrayList escaped = new ArrayList();
         Iterator var3 = lore.iterator();

         while(var3.hasNext()) {
            String loreLine = (String)var3.next();
            escaped.add(loreLine.replace('\u00a7', '^'));
         }

         return escaped;
      }
   }

   public Inventory getInventory() {
      return Bukkit.createInventory(this, this.getFinalInventorySize(), this.settings.getStockName());
   }

   public Inventory getInventory(TEntityStatus status) {
      Inventory inventory = this.getInventory();
      this.setInventory(inventory, status);
      return inventory;
   }

   public Inventory getManagementInventory(TEntityStatus baseStatus, TEntityStatus status) {
      Inventory inventory = this.getInventory();
      this.setManagementInventory(inventory, baseStatus, status);
      return inventory;
   }

   public void setInventory(Inventory inventory, TEntityStatus status) {
      dB.info(new Object[]{"Setting inventory, status: ", status.name().toLowerCase()});
      inventory.clear();
      Iterator var3 = ((List)this.stock.get(status.asStock())).iterator();

      while(var3.hasNext()) {
         StockItem item = (StockItem)var3.next();
         if(item.getSlot() < 0) {
            item.setSlot(inventory.firstEmpty());
         }

         ItemStack itemStack = item.getItem(false, item.getDescription(status));
         inventory.setItem(item.getSlot(), NBTUtils.markItem(itemStack));
      }

      this.setUi(inventory, (TEntityStatus)null, status);
   }

   public void setAmountsInventory(Inventory inventory, TEntityStatus status, StockItem item) {
      dB.info(new Object[]{"Setting inventory, status: ", TEntityStatus.SELL_AMOUNTS.name().toLowerCase()});
      inventory.clear();
      Iterator var4 = item.getAmounts().iterator();

      while(var4.hasNext()) {
         Integer amount = (Integer)var4.next();
         ItemStack itemStack = item.getItem(false, item.getDescription(status));
         itemStack.setAmount(amount.intValue());
         inventory.setItem(inventory.firstEmpty(), NBTUtils.markItem(itemStack));
      }

      this.setUi(inventory, (TEntityStatus)null, TEntityStatus.SELL_AMOUNTS);
   }

   public void setManagementInventory(Inventory inventory, TEntityStatus baseStatus, TEntityStatus status) {
      dB.info(new Object[]{"Setting management inventory, status: ", status.name().toLowerCase(), ", base status: ", baseStatus.name().toLowerCase()});
      inventory.clear();
      Iterator var4 = ((List)this.stock.get(baseStatus.asStock())).iterator();

      while(var4.hasNext()) {
         StockItem item = (StockItem)var4.next();
         dB.spec(dB.DebugLevel.S3_ATTRIB, new Object[]{"Set inv: ", item});
         if(!item.hasAttribute(PatternItem.class)) {
            if(item.getSlot() < 0) {
               item.setSlot(inventory.firstEmpty());
            }

            ItemStack itemStack = item.getItem(false, item.getDescription(status));
            Name name = (Name)item.getAttribute(Name.class, false);
            if(name != null) {
               name.onAssign(itemStack, false);
            }

            StoredEnchant storedEnchant = (StoredEnchant)item.getAttribute(StoredEnchant.class, false);
            if(storedEnchant != null) {
               storedEnchant.onAssign(itemStack, false);
            }

            Enchants enchants = (Enchants)item.getAttribute(Enchants.class, false);
            if(enchants != null) {
               enchants.onAssign(itemStack, false);
            }

            Skull skull = (Skull)item.getAttribute(Skull.class, false);
            if(skull != null) {
               skull.onAssign(itemStack, false);
            }

            dB.spec(dB.DebugLevel.S3_ATTRIB, new Object[]{"End item: ", item});
            inventory.setItem(item.getSlot(), NBTUtils.markItem(itemStack));
         }
      }

      this.setUi(inventory, baseStatus, status);
   }

   public void setUi(Inventory inventory, TEntityStatus baseStatus, TEntityStatus status) {
      Map items = GlobalSettings.getUiItems();
      switch(status) {
         case SELL:
         if(this.stock.get("buy") != null && ((List)this.stock.get("buy")).size() > 0) {
            inventory.setItem(this.getFinalInventorySize() - 1, (ItemStack)items.get("buy"));
         }
         break;
         case SELL_AMOUNTS:
         inventory.setItem(this.getFinalInventorySize() - 1, (ItemStack)items.get("back"));
         break;
         case BUY:
         inventory.setItem(this.getFinalInventorySize() - 1, (ItemStack)items.get("sell"));
         break;
         case MANAGE_SELL:
         inventory.setItem(this.getFinalInventorySize() - 1, (ItemStack)items.get("buy"));
         inventory.setItem(this.getFinalInventorySize() - 2, (ItemStack)items.get("price"));
         inventory.setItem(this.getFinalInventorySize() - 3, (ItemStack)items.get("limit"));
         inventory.setItem(this.getFinalInventorySize() - 4, (ItemStack)items.get("unlock"));
         break;
         case MANAGE_BUY:
         inventory.setItem(this.getFinalInventorySize() - 1, (ItemStack)items.get("sell"));
         inventory.setItem(this.getFinalInventorySize() - 2, (ItemStack)items.get("price"));
         inventory.setItem(this.getFinalInventorySize() - 3, (ItemStack)items.get("limit"));
         inventory.setItem(this.getFinalInventorySize() - 4, (ItemStack)items.get("unlock"));
         break;
         case MANAGE_UNLOCKED:
         inventory.setItem(this.getFinalInventorySize() - 4, (ItemStack)items.get("lock"));
         break;
         case MANAGE_PRICE:
         inventory.setItem(this.getFinalInventorySize() - 2, (ItemStack)items.get("back"));
         inventory.setItem(this.getFinalInventorySize() - 1, (ItemStack)items.get(Stock.opositeStock(baseStatus.asStock())));
         break;
         case MANAGE_AMOUNTS:
         inventory.setItem(this.getFinalInventorySize() - 1, (ItemStack)items.get("back"));
         break;
         case MANAGE_LIMIT:
         inventory.setItem(this.getFinalInventorySize() - 3, (ItemStack)items.get("back"));
         inventory.setItem(this.getFinalInventorySize() - 2, (ItemStack)items.get("plimit"));
         inventory.setItem(this.getFinalInventorySize() - 1, (ItemStack)items.get(Stock.opositeStock(baseStatus.asStock())));
         break;
         case MANAGE_PLIMIT:
         inventory.setItem(this.getFinalInventorySize() - 3, (ItemStack)items.get("back"));
         inventory.setItem(this.getFinalInventorySize() - 2, (ItemStack)items.get("limit"));
         inventory.setItem(this.getFinalInventorySize() - 1, (ItemStack)items.get(Stock.opositeStock(baseStatus.asStock())));
      }

   }
}
