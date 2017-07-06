package net.dandielo.citizens.traders_v3.traders.stock;

import java.util.Iterator;
import java.util.List;
import net.dandielo.citizens.traders_v3.TEntityStatus;
import net.dandielo.citizens.traders_v3.tNpcManager;
import net.dandielo.citizens.traders_v3.core.dB;
import net.dandielo.citizens.traders_v3.traders.Trader;
import net.dandielo.citizens.traders_v3.traders.setting.Settings;
import net.dandielo.citizens.traders_v3.traders.stock.StockItem;
import net.dandielo.citizens.traders_v3.traders.stock.StockTrader;
import net.dandielo.citizens.traders_v3.traders.transaction.ShopSession;
import net.dandielo.citizens.traders_v3.utils.items.attributes.Limit;
import net.dandielo.core.bukkit.NBTUtils;
import net.dandielo.core.items.serialize.core.Enchants;
import net.dandielo.core.items.serialize.core.Name;
import net.dandielo.core.items.serialize.core.Skull;
import net.dandielo.core.items.serialize.core.StoredEnchant;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class StockPlayer extends StockTrader {

   private Player player;


   public StockPlayer(Settings settings, Player player) {
      super(settings);
      this.player = player;
   }

   public Inventory getInventory(TEntityStatus status) {
      Inventory inventory = this.getInventory();
      this.setInventory(inventory, status);
      return inventory;
   }

   public void setInventory(Inventory inventory, TEntityStatus status) {
      Trader trader = tNpcManager.instance().getTraderRelation(this.player);
      dB.info(new Object[]{"Setting inventory, status: ", status.name().toLowerCase()});
      inventory.clear();

      StockItem item;
      ItemStack itemStack;
      for(Iterator var4 = ((List)this.stock.get(status.asStock())).iterator(); var4.hasNext(); inventory.setItem(item.getSlot(), NBTUtils.markItem(itemStack))) {
         item = (StockItem)var4.next();
         if(item.getSlot() < 0) {
            item.setSlot(inventory.firstEmpty());
         }

         List lore = item.getDescription(status);
         lore = Limit.loreRequest(this.player.getName(), item, lore, status);
         lore.addAll((new ShopSession(trader, this.player)).getDescription(status.asStock(), item, item.getAmount()));
         itemStack = item.getItem(false, lore);
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
      }

      this.setUi(inventory, (TEntityStatus)null, status);
   }

   public void setAmountsInventory(Inventory inventory, TEntityStatus status, StockItem item) {
      Trader trader = tNpcManager.instance().getTraderRelation(this.player);
      dB.info(new Object[]{"Setting inventory, status: ", TEntityStatus.SELL_AMOUNTS.name().toLowerCase()});
      inventory.clear();
      Iterator var5 = item.getAmounts().iterator();

      while(var5.hasNext()) {
         Integer amount = (Integer)var5.next();
         List lore = item.getDescription(status);
         lore = Limit.loreRequest(this.player.getName(), item, lore, status);
         lore.addAll((new ShopSession(trader, this.player)).getDescription("sell", item, amount.intValue()));
         ItemStack itemStack = item.getItem(false, lore);
         itemStack.setAmount(amount.intValue());
         ItemMeta meta = itemStack.getItemMeta();
         meta.setLore(lore);
         itemStack.setItemMeta(meta);
         inventory.setItem(inventory.firstEmpty(), NBTUtils.markItem(itemStack));
      }

      this.setUi(inventory, (TEntityStatus)null, TEntityStatus.SELL_AMOUNTS);
   }

   public Player getPlayer() {
      return this.player;
   }

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
      } while(!sItem.similar(item));

      return sItem;
   }
}
