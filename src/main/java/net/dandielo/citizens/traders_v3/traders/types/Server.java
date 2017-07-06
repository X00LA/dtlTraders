package net.dandielo.citizens.traders_v3.traders.types;

import net.dandielo.citizens.traders_v3.TEntityStatus;
import net.dandielo.citizens.traders_v3.tNpcManager;
import net.dandielo.citizens.traders_v3.tNpcType;
import net.dandielo.citizens.traders_v3.core.dB;
import net.dandielo.citizens.traders_v3.core.events.trader.TraderClickEvent;
import net.dandielo.citizens.traders_v3.core.events.trader.TraderOpenEvent;
import net.dandielo.citizens.traders_v3.core.events.trader.TraderTransactionEvent;
import net.dandielo.citizens.traders_v3.traders.Trader;
import net.dandielo.citizens.traders_v3.traders.clicks.ClickHandler;
import net.dandielo.citizens.traders_v3.traders.clicks.InventoryType;
import net.dandielo.citizens.traders_v3.traders.limits.LimitManager;
import net.dandielo.citizens.traders_v3.traders.setting.GlobalSettings;
import net.dandielo.citizens.traders_v3.traders.setting.Settings;
import net.dandielo.citizens.traders_v3.traders.stock.Stock;
import net.dandielo.citizens.traders_v3.traders.stock.StockItem;
import net.dandielo.citizens.traders_v3.traits.TraderTrait;
import net.dandielo.citizens.traders_v3.traits.WalletTrait;
import net.dandielo.citizens.traders_v3.utils.items.attributes.Limit;
import net.dandielo.citizens.traders_v3.utils.items.attributes.Price;
import net.dandielo.citizens.traders_v3.utils.items.flags.StackPrice;
import net.dandielo.core.bukkit.NBTUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

@tNpcType(
   name = "server",
   author = "dandielo"
)
public class Server extends Trader {

   public Server(TraderTrait trader, WalletTrait wallet, Player player) {
      super(trader, wallet, player);
   }

   public void onLeftClick(ItemStack itemInHand) {
      TraderClickEvent e = (TraderClickEvent)(new TraderClickEvent(this, this.player, !GlobalSettings.mmRightToggle(), true)).callEvent();
      if(e.isManagerToggling()) {
         if(this.perms.has(this.player, "dtl.trader.manage")) {
            ItemStack itemToToggle = GlobalSettings.mmItemToggle();
            if(itemInHand == null || itemToToggle.getType().equals(Material.AIR) || itemToToggle.getType().equals(itemInHand.getType())) {
               this.toggleManageMode("left");
            }
         }
      }
   }

   public boolean onRightClick(ItemStack itemInHand) {
      dB.info(new Object[]{"-------------------------------------"});
      dB.info(new Object[]{"Trader right click"});
      dB.info(new Object[]{"-------------------------------------"});
      if(GlobalSettings.mmRightToggle() && this.perms.has(this.player, "dtl.trader.manage")) {
         ItemStack event = GlobalSettings.mmItemToggle();
         if(event.getType().equals(Material.AIR)) {
            event.setType(Material.STICK);
         }

         if(itemInHand != null && event.getType().equals(itemInHand.getType())) {
            TraderClickEvent e = (TraderClickEvent)(new TraderClickEvent(this, this.player, true, false)).callEvent();
            if(e.isManagerToggling()) {
               this.toggleManageMode("right");
               return false;
            }
         } else {
            (new TraderClickEvent(this, this.player, false, false)).callEvent();
         }
      } else {
         (new TraderClickEvent(this, this.player, false, false)).callEvent();
      }

      dB.info(new Object[]{this.getClass().getSimpleName(), " Trader right click event, by: ", this.player.getName()});
      this.limits.refreshAll();
      if(this.status.inManagementMode()) {
         this.inventory = this.stock.getManagementInventory(this.baseStatus, this.status);
      } else {
         this.inventory = this.stock.getInventory(this.status);
      }

      this.parseStatus(this.status);
      this.updatePlayerInventory();
      tNpcManager.instance().registerOpenedInventory(this.player, this.inventory);
      this.player.openInventory(this.inventory);
      TraderOpenEvent event1 = new TraderOpenEvent(this, this.player);
      event1.callEvent();
      return true;
   }

   public void toggleManageMode(String clickEvent) {
      dB.info(new Object[]{this.getClass().getSimpleName(), " Trader ", clickEvent, " click event, by: ", this.player.getName()});
      if(this.status.inManagementMode()) {
         this.locale.sendMessage(this.player, "trader-managermode-disabled", new Object[]{"npc", this.getNPC().getName()});
         this.parseStatus(this.getDefaultStatus());
      } else {
         this.locale.sendMessage(this.player, "trader-managermode-enabled", new Object[]{"npc", this.getNPC().getName()});
         this.parseStatus(this.getDefaultManagementStatus());
      }

   }

   @ClickHandler(
      status = {TEntityStatus.SELL, TEntityStatus.BUY, TEntityStatus.SELL_AMOUNTS},
      inventory = InventoryType.TRADER
   )
   public void generalUI(InventoryClickEvent e) {
      dB.info(new Object[]{"General UI checking"});
      int slot = e.getSlot();
      if(this.stock.isUiSlot(slot)) {
         dB.info(new Object[]{"Hit tests"});
         if(this.hitTest(slot, "buy")) {
            dB.low(new Object[]{"Buy stock hit test"});
            this.locale.sendMessage(this.player, "trader-stock-toggled", new Object[]{"stock", "#stock-buy"});
            this.parseStatus(TEntityStatus.BUY);
         } else if(this.hitTest(slot, "sell")) {
            dB.low(new Object[]{"Sell stock hit test"});
            this.locale.sendMessage(this.player, "trader-stock-toggled", new Object[]{"stock", "#stock-sell"});
            this.parseStatus(TEntityStatus.SELL);
         } else if(this.hitTest(slot, "back")) {
            dB.low(new Object[]{"Babck to stock hit test"});
            this.locale.sendMessage(this.player, "trader-stock-back", new Object[0]);
            this.parseStatus(TEntityStatus.SELL);
         }

         this.stock.setInventory(this.inventory, this.getStatus());
      }

      e.setCancelled(true);
   }

   @ClickHandler(
      status = {TEntityStatus.MANAGE_SELL, TEntityStatus.MANAGE_BUY, TEntityStatus.MANAGE_UNLOCKED, TEntityStatus.MANAGE_AMOUNTS, TEntityStatus.MANAGE_PRICE, TEntityStatus.MANAGE_LIMIT, TEntityStatus.MANAGE_PLIMIT},
      inventory = InventoryType.TRADER
   )
   public void manageUI(InventoryClickEvent e) {
      int slot = e.getSlot();
      if(this.stock.isUiSlot(slot)) {
         if(this.hitTest(slot, "buy")) {
            this.locale.sendMessage(this.player, "trader-managermode-toggled", new Object[]{"mode", "#stock-buy"});
            this.parseStatus(TEntityStatus.MANAGE_BUY);
         } else if(this.hitTest(slot, "sell")) {
            this.locale.sendMessage(this.player, "trader-managermode-toggled", new Object[]{"mode", "#stock-sell"});
            this.parseStatus(TEntityStatus.MANAGE_SELL);
         } else if(this.hitTest(slot, "back")) {
            if(this.status.equals(TEntityStatus.MANAGE_AMOUNTS)) {
               Stock var10000 = this.stock;
               Stock.saveNewAmounts(this.inventory, this.getSelectedItem());
            }

            this.locale.sendMessage(this.player, "trader-managermode-toggled", new Object[]{"mode", "#stock"});
            this.parseStatus(this.baseStatus);
         } else if(this.hitTest(slot, "price")) {
            this.locale.sendMessage(this.player, "trader-managermode-toggled", new Object[]{"mode", "#price"});
            this.parseStatus(TEntityStatus.MANAGE_PRICE);
         } else if(this.hitTest(slot, "lock")) {
            this.locale.sendMessage(this.player, "trader-managermode-stock-locked", new Object[0]);
            this.parseStatus(this.baseStatus);
            this.saveItemsUpponLocking();
         } else if(this.hitTest(slot, "unlock")) {
            this.locale.sendMessage(this.player, "trader-managermode-stock-unlocked", new Object[0]);
            this.parseStatus(TEntityStatus.MANAGE_UNLOCKED);
         } else if(this.hitTest(slot, "plimit")) {
            this.locale.sendMessage(this.player, "trader-managermode-toggled", new Object[]{"mode", "#plimit"});
            this.parseStatus(TEntityStatus.MANAGE_PLIMIT);
         } else if(this.hitTest(slot, "limit")) {
            this.locale.sendMessage(this.player, "trader-managermode-toggled", new Object[]{"mode", "#limit"});
            this.parseStatus(TEntityStatus.MANAGE_LIMIT);
         }

         this.stock.setManagementInventory(this.inventory, this.baseStatus, this.status);
         this.setSpecialBlockValues();
         e.setCancelled(true);
      }

   }

   @ClickHandler(
      status = {TEntityStatus.SELL_AMOUNTS},
      inventory = InventoryType.TRADER
   )
   public void sellAmountsItems(InventoryClickEvent e) {
      e.setCancelled(true);
      if(this.perms.has(this.player, "dtl.trader.sell")) {
         int slot = e.getSlot();
         if(!this.stock.isUiSlot(slot)) {
            if(this.checkItemAmount(slot)) {
               StockItem item;
               double price;
               if(this.handleClick(e.getRawSlot())) {
                  if(!this.inventoryHasPlace(slot)) {
                     this.locale.sendMessage(this.player, "trader-transaction-failed-inventory", new Object[0]);
                     this.transactionEvent(TraderTransactionEvent.TransactionResult.INVENTORY_FULL);
                  } else if(!this.checkSellLimits(slot)) {
                     this.locale.sendMessage(this.player, "trader-transaction-failed-limit-reached", new Object[0]);
                     this.transactionEvent(TraderTransactionEvent.TransactionResult.LIMIT_REACHED);
                  } else if(!this.sellTransaction(slot)) {
                     this.locale.sendMessage(this.player, "trader-transaction-failed-player-money", new Object[0]);
                     this.transactionEvent(TraderTransactionEvent.TransactionResult.PLAYER_LACKS_MONEY);
                  } else {
                     if(this.transactionEvent(TraderTransactionEvent.TransactionResult.SUCCESS_PLAYER_BUY).isSaveToInv()) {
                        this.addToInventory(slot);
                     }

                     item = this.getSelectedItem();
                     price = this.session.getCurrencyValue("sell", item, item.getAmount(slot), Price.class);
                     this.sendTransactionMessage("trader-transaction-success", "#bought", price, item.getAmount(slot));
                     this.updateSellLimits(slot);
                     this.updatePlayerInventory();
                  }
               } else {
                  item = this.getSelectedItem();
                  price = this.session.getCurrencyValue("sell", item, item.getAmount(slot), Price.class);
                  this.sendTransactionMessage("trader-transaction-item", "#info", price);
               }
            }

         }
      }
   }

   @ClickHandler(
      status = {TEntityStatus.SELL},
      inventory = InventoryType.TRADER
   )
   public void sellItems(InventoryClickEvent e) {
      e.setCancelled(true);
      System.out.print(e.getCurrentItem());
      if(this.perms.has(this.player, "dtl.trader.sell")) {
         int slot = e.getSlot();
         if(!this.stock.isUiSlot(slot)) {
            StockItem item;
            double price;
            if(e.isLeftClick()) {
               if(this.selectAndCheckItem(slot)) {
                  if(this.getSelectedItem().hasMultipleAmounts()) {
                     this.locale.sendMessage(this.player, "trader-stock-toggled", new Object[]{"stock", "#stock-amounts"});
                     this.status = TEntityStatus.SELL_AMOUNTS;
                     this.stock.setAmountsInventory(this.inventory, this.status, this.getSelectedItem());
                  } else if(this.handleClick(e.getRawSlot())) {
                     if(!this.inventoryHasPlace()) {
                        this.locale.sendMessage(this.player, "trader-transaction-failed-inventory", new Object[0]);
                        this.transactionEvent(TraderTransactionEvent.TransactionResult.INVENTORY_FULL);
                     } else if(!this.checkSellLimits()) {
                        this.locale.sendMessage(this.player, "trader-transaction-failed-limit-reached", new Object[0]);
                        this.transactionEvent(TraderTransactionEvent.TransactionResult.LIMIT_REACHED);
                     } else if(!this.sellTransaction()) {
                        this.locale.sendMessage(this.player, "trader-transaction-failed-player-money", new Object[0]);
                        this.transactionEvent(TraderTransactionEvent.TransactionResult.PLAYER_LACKS_MONEY);
                     } else {
                        if(this.transactionEvent(TraderTransactionEvent.TransactionResult.SUCCESS_PLAYER_BUY).isSaveToInv()) {
                           this.addToInventory();
                        }

                        item = this.getSelectedItem();
                        price = this.session.getCurrencyValue("sell", item, item.getAmount(), Price.class);
                        this.sendTransactionMessage("trader-transaction-success", "#bought", price);
                        this.updateSellLimits();
                        this.updatePlayerInventory();
                     }
                  } else {
                     item = this.getSelectedItem();
                     price = this.session.getCurrencyValue("sell", item, item.getAmount(), Price.class);
                     this.sendTransactionMessage("trader-transaction-item", "#info", price);
                  }
               }
            } else if(this.selectAndCheckItem(slot)) {
               if(this.handleClick(e.getRawSlot())) {
                  if(!this.inventoryHasPlace()) {
                     this.locale.sendMessage(this.player, "trader-transaction-failed-inventory", new Object[0]);
                     this.transactionEvent(TraderTransactionEvent.TransactionResult.INVENTORY_FULL);
                  } else if(!this.checkSellLimits()) {
                     this.locale.sendMessage(this.player, "trader-transaction-failed-limit-reached", new Object[0]);
                     this.transactionEvent(TraderTransactionEvent.TransactionResult.LIMIT_REACHED);
                  } else if(!this.sellTransaction()) {
                     this.locale.sendMessage(this.player, "trader-transaction-failed-player-money", new Object[0]);
                     this.transactionEvent(TraderTransactionEvent.TransactionResult.PLAYER_LACKS_MONEY);
                  } else {
                     if(this.transactionEvent(TraderTransactionEvent.TransactionResult.SUCCESS_PLAYER_BUY).isSaveToInv()) {
                        this.addToInventory();
                     }

                     item = this.getSelectedItem();
                     price = this.session.getCurrencyValue("sell", item, item.getAmount(), Price.class);
                     this.sendTransactionMessage("trader-transaction-success", "#bought", price);
                     this.updateSellLimits();
                     this.updatePlayerInventory();
                  }
               } else {
                  item = this.getSelectedItem();
                  price = this.session.getCurrencyValue("sell", item, item.getAmount(), Price.class);
                  this.sendTransactionMessage("trader-transaction-item", "#info", price);
               }
            }

         }
      }
   }

   @ClickHandler(
      status = {TEntityStatus.SELL_AMOUNTS},
      inventory = InventoryType.PLAYER
   )
   public void sellAmountsSec(InventoryClickEvent e) {
      e.setCancelled(true);
   }

   @ClickHandler(
      status = {TEntityStatus.SELL, TEntityStatus.BUY},
      inventory = InventoryType.PLAYER
   )
   public void buyItems(InventoryClickEvent e) {
      e.setCancelled(true);
      if(this.perms.has(this.player, "dtl.trader.buy")) {
         this.clearSelection();
         int slot = e.getSlot();
         int scale;
         StockItem item;
         double price;
         if(e.isLeftClick()) {
            if(this.selectAndCheckItem(e.getCurrentItem(), "buy")) {
               scale = e.getCurrentItem().getAmount() / this.getSelectedItem().getAmount();
               if(scale == 0) {
                  return;
               }

               if(this.handleClick(e.getRawSlot())) {
                  if(!this.checkBuyLimits(scale)) {
                     this.locale.sendMessage(this.player, "trader-transaction-failed-limit-reached", new Object[0]);
                     this.transactionEvent(TraderTransactionEvent.TransactionResult.LIMIT_REACHED);
                  } else if(!this.buyTransaction(scale)) {
                     this.locale.sendMessage(this.player, "trader-transaction-failed-trader-money", new Object[]{"npc", this.settings.getNPC().getName()});
                     this.transactionEvent(TraderTransactionEvent.TransactionResult.TRADER_LACKS_MONEY);
                  } else {
                     this.transactionEvent(TraderTransactionEvent.TransactionResult.SUCCESS_PLAYER_SELL);
                     this.removeFromInventory(slot, scale);
                     item = this.getSelectedItem();
                     price = this.session.getCurrencyValue("sell", item, item.getAmount(), Price.class) * (double)scale;
                     this.sendTransactionMessage("trader-transaction-success", "#sold", price, item.getAmount() * scale);
                     this.updateBuyLimits(scale);
                     this.updatePlayerInventory();
                  }
               } else {
                  item = this.getSelectedItem();
                  price = this.session.getCurrencyValue("sell", item, item.getAmount(), Price.class) * (double)scale;
                  this.sendTransactionMessage("trader-transaction-item", "#info", price);
               }
            }
         } else if(this.selectAndCheckItem(e.getCurrentItem(), "buy")) {
            scale = e.getCurrentItem().getAmount() / this.getSelectedItem().getAmount();
            if(scale == 0) {
               return;
            }

            if(this.handleClick(e.getRawSlot())) {
               if(!this.checkBuyLimits()) {
                  this.locale.sendMessage(this.player, "trader-transaction-failed-limit-reached", new Object[0]);
                  this.transactionEvent(TraderTransactionEvent.TransactionResult.LIMIT_REACHED);
               } else if(!this.buyTransaction()) {
                  this.locale.sendMessage(this.player, "trader-transaction-failed-trader-money", new Object[]{"npc", this.settings.getNPC().getName()});
                  this.transactionEvent(TraderTransactionEvent.TransactionResult.TRADER_LACKS_MONEY);
               } else {
                  this.transactionEvent(TraderTransactionEvent.TransactionResult.SUCCESS_PLAYER_SELL);
                  this.removeFromInventory(slot);
                  item = this.getSelectedItem();
                  price = this.session.getCurrencyValue("sell", item, item.getAmount(), Price.class);
                  this.sendTransactionMessage("trader-transaction-success", "#sold", price, item.getAmount());
                  this.updateBuyLimits();
                  this.updatePlayerInventory();
               }
            } else {
               item = this.getSelectedItem();
               price = this.session.getCurrencyValue("sell", item, item.getAmount(), Price.class);
               this.sendTransactionMessage("trader-transaction-item", "#info", price);
            }
         }

      }
   }

   @ClickHandler(
      status = {TEntityStatus.MANAGE_UNLOCKED},
      inventory = InventoryType.TRADER
   )
   public void setStock(InventoryClickEvent e) {
      dB.info(new Object[]{"Unlocked stock click event"});
   }

   @ClickHandler(
      status = {TEntityStatus.MANAGE_UNLOCKED},
      inventory = InventoryType.PLAYER
   )
   public void getStock(InventoryClickEvent e) {
      dB.info(new Object[]{"Unlocked stock click event"});
   }

   @ClickHandler(
      status = {TEntityStatus.MANAGE_SELL, TEntityStatus.MANAGE_BUY},
      inventory = InventoryType.TRADER,
      shift = true
   )
   public void itemAttribs(InventoryClickEvent e) {
      dB.info(new Object[]{"Item managing click event"});
      if(this.selectAndCheckItem(e.getSlot())) {
         if(e.isShiftClick()) {
            if(e.isLeftClick()) {
               this.stock.setAmountsInventory(this.inventory, this.status, this.getSelectedItem());
               this.locale.sendMessage(this.player, "trader-managermode-toggled", new Object[]{"mode", "#amount"});
               this.parseStatus(TEntityStatus.MANAGE_AMOUNTS);
            }
         } else if(e.isLeftClick()) {
            if(this.getSelectedItem().hasFlag(StackPrice.class)) {
               this.getSelectedItem().removeFlag(StackPrice.class);
            } else {
               this.getSelectedItem().addFlag(".sp");
            }

            this.locale.sendMessage(this.player, "key-change", new Object[]{"key", "#stack-price", "value", this.locale.getKeyword(String.valueOf(this.getSelectedItem().hasFlag(StackPrice.class)))});
         }
      }

      e.setCancelled(true);
   }

   @ClickHandler(
      status = {TEntityStatus.MANAGE_SELL, TEntityStatus.MANAGE_BUY},
      inventory = InventoryType.PLAYER
   )
   public void itemsForStock(InventoryClickEvent e) {}

   @ClickHandler(
      status = {TEntityStatus.MANAGE_PRICE},
      inventory = InventoryType.TRADER
   )
   public void managePrices(InventoryClickEvent e) {
      dB.info(new Object[]{"Price managing click event"});
      if(this.selectAndCheckItem(e.getSlot())) {
         StockItem item = this.getSelectedItem();
         if(e.getCursor().getType().equals(Material.AIR)) {
            this.locale.sendMessage(this.player, "key-value", new Object[]{"key", "#price", "value", item.getPriceFormated()});
         } else {
            Price price = item.getPriceAttr();
            if(e.isLeftClick()) {
               price.increase(Settings.getBlockValue(e.getCursor()) * (double)e.getCursor().getAmount());
               this.locale.sendMessage(this.player, "key-change", new Object[]{"key", "#price", "value", item.getPriceFormated()});
            } else if(e.isRightClick()) {
               price.decrease(Settings.getBlockValue(e.getCursor()) * (double)e.getCursor().getAmount());
               this.locale.sendMessage(this.player, "key-change", new Object[]{"key", "#price", "value", item.getPriceFormated()});
            }

            ItemStack itemStack = item.getItem(false, item.getDescription(this.status));
            e.getInventory().setItem(item.getSlot(), NBTUtils.markItem(itemStack));
         }
      }

      e.setCancelled(true);
   }

   @ClickHandler(
      status = {TEntityStatus.MANAGE_LIMIT},
      inventory = InventoryType.TRADER,
      shift = true
   )
   public void manageLimits(InventoryClickEvent e) {
      dB.info(new Object[]{"Limit managing click event"});
      if(this.selectAndCheckItem(e.getSlot())) {
         StockItem item = this.getSelectedItem();
         if(!item.hasAttribute(Limit.class)) {
            item.addAttribute("l", this.settings.getNPC().getId() + "@" + this.baseStatus.asStock() + ":" + item.getSlot() + "/0/0s");
         }

         Limit limit = (Limit)item.getAttribute(Limit.class, false);
         if(e.getCursor().getType().equals(Material.AIR)) {
            this.locale.sendMessage(this.player, "key-value", new Object[]{"key", "#limit", "value", limit.getLimit() != 0?String.valueOf(limit.getLimit()):"none"});
            this.locale.sendMessage(this.player, "key-value", new Object[]{"key", "#timeout", "value", LimitManager.timeoutString(limit.getTimeout())});
         } else {
            if(!e.isShiftClick()) {
               if(e.isLeftClick()) {
                  limit.increaseLimit((int)Settings.getBlockValue(e.getCursor()) * e.getCursor().getAmount());
                  this.locale.sendMessage(this.player, "key-change", new Object[]{"key", "#limit", "value", limit.getLimit() != 0?String.valueOf(limit.getLimit()):"none"});
               } else if(e.isRightClick()) {
                  limit.decreaseLimit((int)Settings.getBlockValue(e.getCursor()) * e.getCursor().getAmount());
                  this.locale.sendMessage(this.player, "key-change", new Object[]{"key", "#limit", "value", limit.getLimit() != 0?String.valueOf(limit.getLimit()):"none"});
               }
            } else if(e.isLeftClick()) {
               limit.increaseTimeout(Settings.getBlockTimeoutValue(e.getCursor()) * (long)e.getCursor().getAmount());
               this.locale.sendMessage(this.player, "key-change", new Object[]{"key", "#timeout", "value", LimitManager.timeoutString(limit.getTimeout())});
            } else if(e.isRightClick()) {
               limit.decreaseTimeout(Settings.getBlockTimeoutValue(e.getCursor()) * (long)e.getCursor().getAmount());
               this.locale.sendMessage(this.player, "key-change", new Object[]{"key", "#timeout", "value", LimitManager.timeoutString(limit.getTimeout())});
            }

            ItemStack itemStack = item.getItem(false, item.getDescription(this.status));
            e.getInventory().setItem(item.getSlot(), NBTUtils.markItem(itemStack));
         }

         if(limit.getLimit() == 0 && limit.getPlayerLimit() == 0) {
            item.removeAttribute(Limit.class);
         }
      }

      e.setCancelled(true);
   }

   @ClickHandler(
      status = {TEntityStatus.MANAGE_PLIMIT},
      inventory = InventoryType.TRADER,
      shift = true
   )
   public void managePlayerLimits(InventoryClickEvent e) {
      dB.info(new Object[]{"Limit managing click event"});
      if(this.selectAndCheckItem(e.getSlot())) {
         StockItem item = this.getSelectedItem();
         if(!item.hasAttribute(Limit.class)) {
            item.addAttribute("l", this.settings.getNPC().getId() + "@" + this.baseStatus.asStock() + ":" + item.getSlot() + "/0/0s");
         }

         Limit limit = (Limit)item.getAttribute(Limit.class, false);
         if(e.getCursor().getType().equals(Material.AIR)) {
            this.locale.sendMessage(this.player, "key-value", new Object[]{"key", "#limit", "value", limit.getPlayerLimit() != 0?String.valueOf(limit.getPlayerLimit()):"none"});
            this.locale.sendMessage(this.player, "key-value", new Object[]{"key", "#timeout", "value", LimitManager.timeoutString(limit.getPlayerTimeout())});
         } else {
            if(!e.isShiftClick()) {
               if(e.isLeftClick()) {
                  limit.increasePlayerLimit((int)Settings.getBlockValue(e.getCursor()) * e.getCursor().getAmount());
                  this.locale.sendMessage(this.player, "key-change", new Object[]{"key", "#limit", "value", limit.getPlayerLimit() != 0?String.valueOf(limit.getPlayerLimit()):"none"});
               } else if(e.isRightClick()) {
                  limit.decreasePlayerLimit((int)Settings.getBlockValue(e.getCursor()) * e.getCursor().getAmount());
                  this.locale.sendMessage(this.player, "key-change", new Object[]{"key", "#limit", "value", limit.getPlayerLimit() != 0?String.valueOf(limit.getPlayerLimit()):"none"});
               }
            } else if(e.isLeftClick()) {
               limit.increasePlayerTimeout(Settings.getBlockTimeoutValue(e.getCursor()) * (long)e.getCursor().getAmount());
               this.locale.sendMessage(this.player, "key-change", new Object[]{"key", "#timeout", "value", LimitManager.timeoutString(limit.getPlayerTimeout())});
            } else if(e.isRightClick()) {
               limit.decreasePlayerTimeout(Settings.getBlockTimeoutValue(e.getCursor()) * (long)e.getCursor().getAmount());
               this.locale.sendMessage(this.player, "key-change", new Object[]{"key", "#timeout", "value", LimitManager.timeoutString(limit.getPlayerTimeout())});
            }

            ItemStack itemStack = item.getItem(false, item.getDescription(this.status));
            e.getInventory().setItem(item.getSlot(), NBTUtils.markItem(itemStack));
            if(limit.getLimit() == 0 && limit.getPlayerLimit() == 0) {
               item.removeAttribute(Limit.class);
            }
         }

         e.setCancelled(true);
      }

   }

   @ClickHandler(
      status = {TEntityStatus.SELL, TEntityStatus.BUY, TEntityStatus.SELL_AMOUNTS, TEntityStatus.MANAGE_BUY, TEntityStatus.MANAGE_SELL},
      shift = true,
      inventory = InventoryType.TRADER
   )
   public void topShift(InventoryClickEvent e) {
      if(e.isShiftClick()) {
         e.setCancelled(true);
      }

   }

   @ClickHandler(
      status = {TEntityStatus.SELL, TEntityStatus.BUY, TEntityStatus.SELL_AMOUNTS, TEntityStatus.MANAGE_BUY, TEntityStatus.MANAGE_SELL},
      shift = true,
      inventory = InventoryType.PLAYER
   )
   public void botShift(InventoryClickEvent e) {
      if(e.isShiftClick()) {
         e.setCancelled(true);
      }

   }

   @ClickHandler(
      status = {TEntityStatus.SELL, TEntityStatus.BUY, TEntityStatus.SELL_AMOUNTS, TEntityStatus.MANAGE_SELL, TEntityStatus.MANAGE_BUY, TEntityStatus.MANAGE_AMOUNTS, TEntityStatus.MANAGE_PRICE, TEntityStatus.MANAGE_LIMIT},
      shift = true,
      inventory = InventoryType.TRADER
   )
   public void topDebug(InventoryClickEvent e) {
      dB.info(new Object[]{"Inventory click, by: ", this.player.getName(), ", status: ", this.status.name().toLowerCase()});
      dB.info(new Object[]{"slot: ", Integer.valueOf(e.getSlot()), ", left: ", Boolean.valueOf(e.isLeftClick()), ", shift: ", Boolean.valueOf(e.isShiftClick())});
   }

   @ClickHandler(
      status = {TEntityStatus.SELL, TEntityStatus.BUY, TEntityStatus.SELL_AMOUNTS, TEntityStatus.MANAGE_SELL, TEntityStatus.MANAGE_BUY, TEntityStatus.MANAGE_AMOUNTS, TEntityStatus.MANAGE_PRICE, TEntityStatus.MANAGE_LIMIT},
      shift = true,
      inventory = InventoryType.PLAYER
   )
   public void botDebug(InventoryClickEvent e) {
      dB.info(new Object[]{"Inventory click, by: ", this.player.getName(), ", status: ", this.status.name().toLowerCase()});
      dB.info(new Object[]{"slot: ", Integer.valueOf(e.getSlot()), ", left: ", Boolean.valueOf(e.isLeftClick()), ", shift: ", Boolean.valueOf(e.isShiftClick())});
   }

   @ClickHandler(
      status = {TEntityStatus.SELL, TEntityStatus.BUY, TEntityStatus.SELL_AMOUNTS},
      inventory = InventoryType.TRADER
   )
   public void __topUpdate(InventoryClickEvent e) {
      this.limits.refreshAll();
      if(this.status.equals(TEntityStatus.SELL_AMOUNTS)) {
         this.stock.setAmountsInventory(this.inventory, this.status, this.getSelectedItem());
      } else {
         this.stock.setInventory(this.inventory, this.status);
      }

   }

   @ClickHandler(
      status = {TEntityStatus.SELL, TEntityStatus.BUY, TEntityStatus.SELL_AMOUNTS},
      inventory = InventoryType.PLAYER
   )
   public void __bottomUpdate(InventoryClickEvent e) {
      this.limits.refreshAll();
      if(this.status.equals(TEntityStatus.SELL_AMOUNTS)) {
         this.stock.setAmountsInventory(this.inventory, this.status, this.getSelectedItem());
      } else {
         this.stock.setInventory(this.inventory, this.status);
      }

   }

   @ClickHandler(
      status = {TEntityStatus.SELL, TEntityStatus.BUY, TEntityStatus.SELL_AMOUNTS},
      inventory = InventoryType.PLAYER
   )
   public void __last(InventoryClickEvent e) {
      if(e.isCancelled()) {
         ((Player)e.getWhoClicked()).updateInventory();
      }

   }
}
