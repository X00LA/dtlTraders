package net.dandielo.citizens.traders_v3.traders;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.citizensnpcs.api.npc.NPC;
import net.dandielo.citizens.traders_v3.TEntityStatus;
import net.dandielo.citizens.traders_v3.TradingEntity;
import net.dandielo.citizens.traders_v3.bukkit.Perms;
import net.dandielo.citizens.traders_v3.core.dB;
import net.dandielo.citizens.traders_v3.core.events.trader.TraderTransactionEvent;
import net.dandielo.citizens.traders_v3.core.locale.LocaleManager;
import net.dandielo.citizens.traders_v3.core.tools.StringTools;
import net.dandielo.citizens.traders_v3.traders.clicks.ClickHandler;
import net.dandielo.citizens.traders_v3.traders.limits.LimitManager;
import net.dandielo.citizens.traders_v3.traders.setting.GlobalSettings;
import net.dandielo.citizens.traders_v3.traders.setting.Settings;
import net.dandielo.citizens.traders_v3.traders.stock.Stock;
import net.dandielo.citizens.traders_v3.traders.stock.StockItem;
import net.dandielo.citizens.traders_v3.traders.transaction.ShopSession;
import net.dandielo.citizens.traders_v3.traders.transaction.Wallet;
import net.dandielo.citizens.traders_v3.traits.TraderTrait;
import net.dandielo.citizens.traders_v3.traits.WalletTrait;
import net.dandielo.citizens.traders_v3.utils.ItemUtils;
import net.dandielo.citizens.traders_v3.utils.items.attributes.PatternItem;
import net.dandielo.core.items.serialize.core.SpawnEgg;
import net.dandielo.core.items.serialize.flags.Lore;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SpawnEggMeta;

public abstract class Trader implements TradingEntity {

   private static Map handlers = new HashMap();
   protected Perms perms;
   protected LimitManager limits;
   protected LocaleManager locale;
   protected Settings settings;
   protected Wallet wallet;
   protected Stock stock;
   protected Player player;
   protected Inventory inventory;
   protected TEntityStatus baseStatus;
   protected TEntityStatus status;
   private int lastSlot;
   private StockItem selectedItem;
   protected ShopSession session;


   public static void registerHandlers(Class clazz) {
      dB.info(new Object[]{"Registering click handlers for trader type: ", clazz.getSimpleName()});
      ArrayList methods = new ArrayList();
      Method[] var2 = clazz.getMethods();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Method method = var2[var4];
         if(method.isAnnotationPresent(ClickHandler.class)) {
            methods.add(method);
         }
      }

      handlers.put(clazz, methods);
   }

   public Trader(TraderTrait trader, WalletTrait wallet, Player player) {
      this.perms = Perms.perms;
      this.limits = LimitManager.self;
      this.locale = LocaleManager.locale;
      this.lastSlot = -1;
      this.selectedItem = null;
      dB.low(new Object[]{"Creating a trader, for: ", player.getName()});
      this.settings = trader.getSettings();
      this.status = this.getDefaultStatus();
      this.stock = trader.getStock().toPlayerStock(player);
      this.wallet = wallet.getWallet();
      this.player = player;
      this.session = new ShopSession(this, player);
   }

   public Settings getSettings() {
      return this.settings;
   }

   public TEntityStatus getStatus() {
      return this.status;
   }

   public Stock getStock() {
      return this.stock;
   }

   public Player getPlayer() {
      return this.player;
   }

   public Wallet getWallet() {
      return this.wallet;
   }

   public NPC getNPC() {
      return this.settings.getNPC();
   }

   public TraderTransactionEvent transactionEvent(TraderTransactionEvent.TransactionResult result) {
      return (TraderTransactionEvent)(new TraderTransactionEvent(this, this.player, this.selectedItem, result)).callEvent();
   }

   public void parseStatus(TEntityStatus newStatus) {
      this.status = newStatus;
      this.baseStatus = TEntityStatus.parseBaseManageStatus(this.baseStatus, newStatus);
   }

   public void onManageInventoryClick(InventoryClickEvent e) {
      this.inventoryClickHandler(e);
   }

   public void onInventoryClick(InventoryClickEvent e) {
      this.inventoryClickHandler(e);
   }

   private void inventoryClickHandler(InventoryClickEvent e) {
      dB.low(new Object[]{"Handling click event"});
      boolean top = e.getView().convertSlot(e.getRawSlot()) == e.getRawSlot();
      List methods = (List)handlers.get(this.getClass());
      Iterator var4 = methods.iterator();

      while(var4.hasNext()) {
         Method method = (Method)var4.next();
         ClickHandler handler = (ClickHandler)method.getAnnotation(ClickHandler.class);
         if((handler.shift() || !e.isShiftClick()) && this.checkStatusWith(handler.status()) && handler.inventory().equals(top)) {
            try {
               dB.low(new Object[]{"Executing method: ", ChatColor.AQUA, method.getName()});
               method.invoke(this, new Object[]{e});
            } catch (Exception var8) {
               dB.critical(new Object[]{"While executing inventory click event"});
               dB.critical(new Object[]{"Exception: ", var8.getClass().getSimpleName()});
               dB.critical(new Object[]{"Method: ", method.getName()});
               dB.critical(new Object[]{"Trader: ", this.getSettings().getNPC().getName(), ", player: ", this.player.getName()});
               dB.critical(new Object[]{" "});
               dB.critical(new Object[]{"Exception message: ", var8.getMessage()});
               dB.high(new Object[]{"Stack trace: ", StringTools.stackTrace(var8.getStackTrace())});
               e.setCancelled(true);
            }
         }
      }

      dB.info(new Object[]{"Event cancelled: ", Boolean.valueOf(e.isCancelled())});
   }

   protected boolean sellTransaction() {
      return this.sellTransaction(0);
   }

   protected boolean sellTransaction(int slot) {
      int amount = this.selectedItem.getAmount(slot);
      if(this.session.allowTransaction("sell", this.selectedItem, amount)) {
         if(!this.session.finalizeTransaction("sell", this.selectedItem, amount)) {
            dB.critical(new Object[]{"Some thing went REALLLLLLY WRONG HERE! GOT RIGHT NOW TO THE DEV!"});
         }

         return true;
      } else {
         return false;
      }
   }

   protected boolean buyTransaction() {
      return this.buyTransaction(1);
   }

   protected boolean buyTransaction(int scale) {
      if(this.session.allowTransaction("buy", this.selectedItem, this.selectedItem.getAmount() * scale)) {
         if(!this.session.finalizeTransaction("buy", this.selectedItem, this.selectedItem.getAmount() * scale)) {
            dB.critical(new Object[]{"Some thing went REALLLLLLY WRONG HERE! GOT RIGHT NOW TO THE DEV!"});
         }

         return true;
      } else {
         return false;
      }
   }

   private static ItemStack CleanItem(ItemStack item) {
      return ItemUtils.createStockItem(item).getItem(true);
   }

   public void updatePlayerInventory() {
      PlayerInventory inv = this.player.getInventory();
      StockItem selected = this.selectedItem;
      int i = 0;
      ItemStack[] var4 = inv.getContents();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         ItemStack item = var4[var6];
         if(this.selectAndCheckItem(item, "buy")) {
            int amount = this.selectedItem.getAmount();
            int scale = item.getAmount() / amount;
            if(item.getAmount() >= amount) {
               ArrayList cleaned = new ArrayList();
               cleaned.addAll(this.session.getDescription("buy", this.selectedItem, this.selectedItem.getAmount() * scale));
               ItemStack nItem = Lore.addLore(CleanItem(item), cleaned);
               inv.setItem(i, nItem);
            } else {
               ItemStack var12 = CleanItem(item);
               inv.setItem(i, var12);
            }
         }

         ++i;
      }

      this.selectedItem = selected;
   }

   public void setSpecialBlockValues() {
      PlayerInventory inv = this.player.getInventory();
      StockItem selected = this.selectedItem;
      int i = 0;
      ItemStack[] var4 = inv.getContents();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         ItemStack item = var4[var6];
         if(this.selectAndCheckNewItem(item) && this.status.equals(TEntityStatus.MANAGE_PRICE)) {
            double var12 = GlobalSettings.getBlockValue(item);
            ArrayList lore1;
            if(item.getType().equals(Material.WOOD)) {
               lore1 = new ArrayList();
               lore1.add("§3§d§d§f" + ChatColor.GOLD + "Price value: " + ChatColor.YELLOW + String.format("%.2f", new Object[]{Double.valueOf(var12)}));
               inv.setItem(i, Lore.addLore(CleanItem(item), lore1));
            }

            if(var12 != 1.0D) {
               lore1 = new ArrayList();
               lore1.add("§3§d§d§f" + ChatColor.GOLD + "Price value: " + ChatColor.YELLOW + String.format("%.2f", new Object[]{Double.valueOf(var12)}));
               inv.setItem(i, Lore.addLore(CleanItem(item), lore1));
            }
         } else if(this.selectAndCheckNewItem(item) && this.status.equals(TEntityStatus.MANAGE_LIMIT)) {
            ArrayList lore = new ArrayList();
            long time = GlobalSettings.getBlockTimeoutValue(item);
            if(time != 1L) {
               lore.add("§3§d§d§f" + ChatColor.GOLD + "Time value: " + ChatColor.YELLOW + LimitManager.timeoutString(time));
            }

            int limit = (int)GlobalSettings.getBlockValue(item);
            if(limit > 1) {
               lore.add("§3§d§d§f" + ChatColor.GOLD + "Limit value: " + ChatColor.YELLOW + limit);
            }

            inv.setItem(i, Lore.addLore(CleanItem(item), lore));
         } else if(item != null) {
            inv.setItem(i, CleanItem(item));
         }

         ++i;
      }

      this.selectedItem = selected;
   }

   public void saveItemsUpponLocking() {
      dB.normal(new Object[]{"Clearing the stock to set it with new items"});
      List oldItems = this.stock.getStock(this.baseStatus.asStock());
      dB.low(new Object[]{"Old stock size: ", Integer.valueOf(oldItems.size())});
      this.stock.clearStock(this.baseStatus.asStock());
      dB.low(new Object[]{"Old stock size after clearing: ", Integer.valueOf(oldItems.size())});
      int slot = 0;
      ItemStack[] var3 = this.inventory.getContents();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         ItemStack bItem = var3[var5];
         dB.spec(dB.DebugLevel.S3_ATTRIB, new Object[]{"Item: ", bItem});
         if(bItem != null && !this.stock.isUiSlot(slot)) {
            StockItem sItem = ItemUtils.createStockItem(bItem);
            dB.spec(dB.DebugLevel.S3_ATTRIB, new Object[]{"Item: ", sItem});
            StockItem matchedItem = null;
            Iterator var9 = oldItems.iterator();

            while(var9.hasNext()) {
               StockItem item = (StockItem)var9.next();
               if(matchedItem == null && !item.hasAttribute(PatternItem.class) && item.getSlot() == slot) {
                  matchedItem = item;
               }
            }

            dB.spec(dB.DebugLevel.S3_ATTRIB, new Object[]{"Matched: ", matchedItem});
            if(matchedItem != null) {
               matchedItem.setSlot(slot);
               this.stock.addItem(matchedItem, this.baseStatus.asStock());
               dB.spec(dB.DebugLevel.S3_ATTRIB, new Object[]{"Stock size: ", Integer.valueOf(this.stock.getStock(this.baseStatus.asStock()).size())});
            } else {
               sItem.setSlot(slot);
               this.stock.addItem(sItem, this.baseStatus.asStock());
               dB.spec(dB.DebugLevel.S3_ATTRIB, new Object[]{"Stock size: ", Integer.valueOf(this.stock.getStock(this.baseStatus.asStock()).size())});
            }
         }

         ++slot;
      }

   }

   public void lockAndSave() {
      this.locale.sendMessage(this.player, "trader-managermode-stock-locked", new Object[0]);
      this.parseStatus(this.baseStatus);
      this.saveItemsUpponLocking();
   }

   protected final boolean inventoryHasPlace() {
      return this.inventoryHasPlace(0);
   }

   protected final boolean inventoryHasPlace(int slot) {
      dB.info(new Object[]{"Checking players inventory space"});
      dB.info(new Object[]{"Player: ", this.player.getName(), ", item: ", this.selectedItem.getItem(false).getType().name().toLowerCase()});
      return this._inventoryHasPlace(this.selectedItem.getAmount(slot));
   }

   private int sizeLeft(Inventory inv) {
      int size = 0;
      ItemStack[] var3 = inv.getContents();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         ItemStack item = var3[var5];
         if(item == null) {
            ++size;
         }
      }

      return size;
   }

   protected final boolean _inventoryHasPlace(int amount) {
      int sizeLeft = this.sizeLeft(this.player.getInventory());
      return this.inventory.firstEmpty() >= 0;
   }

   protected final boolean addToInventory() {
      return this.addToInventory(0);
   }

   protected final boolean addToInventory(int slot) {
      dB.info(new Object[]{"Adding item to players inventory"});
      dB.info(new Object[]{"Player: ", this.player.getName(), ", item: ", this.selectedItem.getItem(false).getType().name().toLowerCase()});
      return this._addToInventory(this.selectedItem.getAmount(slot));
   }

   private boolean _addToInventory(int amount) {
      PlayerInventory inventory = this.player.getInventory();
      int amountLeft = amount;
      ItemStack generatedItem = this.selectedItem.getItem(true);
      Iterator sizeLeft = inventory.all(generatedItem.getType()).values().iterator();

      ItemStack is;
      while(sizeLeft.hasNext()) {
         is = (ItemStack)sizeLeft.next();
         if(this.selectedItem.similar(ItemUtils.createStockItem(is))) {
            if(generatedItem.getType().equals(Material.MONSTER_EGG)) {
               SpawnEggMeta spawnEggMeta_1 = (SpawnEggMeta)generatedItem.getItemMeta();
               SpawnEggMeta spawnEggMeta_2 = (SpawnEggMeta)is.getItemMeta();
               if(spawnEggMeta_1.getSpawnedType() != spawnEggMeta_2.getSpawnedType()) {
                  continue;
               }

               if(is.getAmount() + amountLeft <= is.getMaxStackSize()) {
                  is.setAmount(is.getAmount() + amountLeft);
                  return true;
               }

               if(is.getAmount() < is.getMaxStackSize()) {
                  amountLeft = (is.getAmount() + amountLeft) % is.getMaxStackSize();
                  is.setAmount(is.getMaxStackSize());
               }

               if(amountLeft <= 0) {
                  return true;
               }
            }

            if(is.getAmount() + amountLeft <= is.getMaxStackSize()) {
               is.setAmount(is.getAmount() + amountLeft);
               return true;
            }

            if(is.getAmount() < is.getMaxStackSize()) {
               amountLeft = (is.getAmount() + amountLeft) % is.getMaxStackSize();
               is.setAmount(is.getMaxStackSize());
            }

            if(amountLeft <= 0) {
               return true;
            }
         }
      }

      int sizeLeft1 = this.sizeLeft(this.player.getInventory());
      if(sizeLeft1 > 0 && sizeLeft1 >= amountLeft / generatedItem.getMaxStackSize() && inventory.firstEmpty() >= 0) {
         for(; amountLeft > 0; inventory.addItem(new ItemStack[]{is})) {
            is = generatedItem.clone();
            is.setAmount(amountLeft > generatedItem.getMaxStackSize()?generatedItem.getMaxStackSize():amountLeft);
            amountLeft -= generatedItem.getMaxStackSize();
            if(this.selectedItem.hasAttribute(SpawnEgg.class)) {
               ((SpawnEgg)this.selectedItem.getAttribute(SpawnEgg.class, false)).onAssign(is, false);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   protected final void removeFromInventory(int slot) {
      this.removeFromInventory(slot, 1);
   }

   protected final void removeFromInventory(int slot, int scale) {
      dB.info(new Object[]{"Removing item from players inventory"});
      dB.info(new Object[]{"Player: ", this.player.getName(), ", item: ", this.selectedItem.getItem(false).getType().name().toLowerCase()});
      this._removeFromInventory(slot, this.selectedItem.getAmount(0) * scale);
   }

   private void _removeFromInventory(int slot, int amount) {
      PlayerInventory inventory = this.player.getInventory();
      ItemStack item = inventory.getItem(slot);
      if(item.getAmount() > amount) {
         item.setAmount(item.getAmount() - amount);
         inventory.setItem(slot, item);
      } else {
         inventory.setItem(slot, (ItemStack)null);
      }

   }

   protected boolean checkSellLimits() {
      return this.checkSellLimits(0);
   }

   protected boolean checkSellLimits(int i) {
      return this.limits.checkLimit(this.player, this.selectedItem, this.selectedItem.getAmount(i), "buy");
   }

   protected boolean checkBuyLimits() {
      return this.checkBuyLimits(1);
   }

   protected boolean checkBuyLimits(int scale) {
      return this.limits.checkLimit(this.player, this.selectedItem, this.selectedItem.getAmount(0) * scale, "sell");
   }

   protected void updateSellLimits() {
      this.updateSellLimits(0);
   }

   protected void updateSellLimits(int i) {
      this.limits.updateLimit(this.player, this.selectedItem, this.selectedItem.getAmount(i), "buy");
   }

   protected void updateBuyLimits() {
      this.updateBuyLimits(1);
   }

   protected void updateBuyLimits(int scale) {
      this.limits.updateLimit(this.player, this.selectedItem, -(this.selectedItem.getAmount(0) * scale), "sell");
   }

   protected void selectNewItem(ItemStack item) {
      this.selectedItem = item != null?ItemUtils.createStockItem(item):null;
   }

   protected boolean selectAndCheckNewItem(ItemStack item) {
      return (this.selectedItem = item != null && !item.getType().equals(Material.AIR)?ItemUtils.createStockItem(item):null) != null;
   }

   protected void clearSelection() {
      this.selectedItem = null;
   }

   protected StockItem selectItem(int slot) {
      return this.selectedItem = this.stock.getItem(slot, this.baseStatus.asStock());
   }

   protected StockItem selectItem(ItemStack item) {
      return this.stock.getItem(ItemUtils.createStockItem(item), this.baseStatus.asStock());
   }

   protected StockItem selectItem(ItemStack item, String bStock) {
      return this.stock.getItem(ItemUtils.createStockItem(item), bStock);
   }

   protected boolean checkItemAmount(int slot) {
      return this.selectedItem.getAmounts().size() > slot;
   }

   protected boolean selectAndCheckItem(int slot) {
      return (this.selectedItem = this.stock.getItem(slot, this.baseStatus.asStock())) != null;
   }

   protected boolean selectAndCheckItem(ItemStack item) {
      return item != null && !item.getType().equals(Material.AIR)?(this.selectedItem = this.stock.getItem(ItemUtils.createStockItem(item), this.baseStatus.asStock())) != null:false;
   }

   protected boolean selectAndCheckItem(ItemStack item, String bStock) {
      if(item != null && !item.getType().equals(Material.AIR)) {
         Iterator var3 = this.stock.getStock(bStock).iterator();

         while(var3.hasNext()) {
            StockItem stockItem = (StockItem)var3.next();
            StockItem _stockItem = ItemUtils.createStockItem(item);
            if(stockItem.similar(_stockItem)) {
               this.selectedItem = stockItem;
            }
         }

         return this.selectedItem != null;
      } else {
         return false;
      }
   }

   protected boolean hasSelectedItem() {
      return this.selectedItem != null;
   }

   protected StockItem getSelectedItem() {
      return this.selectedItem;
   }

   protected boolean checkStatusWith(TEntityStatus[] stat) {
      TEntityStatus[] var2 = stat;
      int var3 = stat.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         TEntityStatus s = var2[var4];
         if(s.equals(this.status)) {
            return true;
         }
      }

      return false;
   }

   protected boolean handleClick(int slot) {
      return Settings.dClickEvent()?this.lastSlot == (this.lastSlot = slot):true;
   }

   protected boolean hitTest(int slot, String item) {
      return ((ItemStack)Settings.getUiItems().get(item)).equals(this.inventory.getItem(slot));
   }

   protected TEntityStatus getDefaultStatus() {
      return TEntityStatus.baseStatus(this.settings.getStockStart());
   }

   protected TEntityStatus getDefaultManagementStatus() {
      return TEntityStatus.baseManagementStatus(this.settings.getManagerStockStart());
   }

   public boolean equals(NPC npc) {
      return this.settings.getNPC().getId() == npc.getId();
   }

   protected void sendTransactionMessage(String message, String action, double price) {
      this.locale.sendMessage(this.player, message, (Object[])(new String[]{"player", this.player.getName(), "trader", this.getNPC().getName(), "item", this.selectedItem.getName(), "amount", String.valueOf(this.selectedItem.getAmount()), "price", String.format("%.2f", new Object[]{Double.valueOf(price)}).replace(',', '.'), "action", action}));
   }

   protected void sendTransactionMessage(String message, String action, double price, int amount) {
      this.locale.sendMessage(this.player, message, (Object[])(new String[]{"player", this.player.getName(), "trader", this.getNPC().getName(), "item", this.selectedItem.getName(), "amount", String.valueOf(amount), "price", String.format("%.2f", new Object[]{Double.valueOf(price)}).replace(',', '.'), "action", action}));
   }

}
