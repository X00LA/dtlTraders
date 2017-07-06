package net.dandielo.api.traders;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.MobType;
import net.dandielo.citizens.traders_v3.TEntityStatus;
import net.dandielo.citizens.traders_v3.tNpcManager;
import net.dandielo.citizens.traders_v3.traders.Trader;
import net.dandielo.citizens.traders_v3.traders.setting.GlobalSettings;
import net.dandielo.citizens.traders_v3.traders.stock.StockItem;
import net.dandielo.citizens.traders_v3.traits.TraderTrait;
import net.dandielo.citizens.traders_v3.traits.WalletTrait;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class TraderAPI {

   private static tNpcManager manager = tNpcManager.instance();


   public static TraderTrait createTrader(Location loc, String name, String type) {
      return createTrader(loc, name, type, EntityType.PLAYER);
   }

   public static TraderTrait createTrader(Location location, String name, String type, EntityType entity) {
      NPC npc = CitizensAPI.getNPCRegistry().createNPC(entity, name);
      npc.addTrait(TraderTrait.class);
      npc.addTrait(WalletTrait.class);
      WalletTrait wallet = (WalletTrait)npc.getTrait(WalletTrait.class);
      wallet.setType(GlobalSettings.getDefaultWallet());
      wallet.setMoney(GlobalSettings.getWalletStartBalance());
      npc.addTrait(MobType.class);
      ((MobType)npc.getTrait(MobType.class)).setType(entity);
      npc.spawn(location);
      TraderTrait trader = (TraderTrait)npc.getTrait(TraderTrait.class);
      trader.getSettings().setType(type);
      return trader;
   }

   public static boolean removeTrader(int id) {
      NPC npc = CitizensAPI.getNPCRegistry().getById(id);
      if(npc == null) {
         return false;
      } else {
         npc.destroy();
         return true;
      }
   }

   public static boolean toggleStatus(Player player, TEntityStatus status) {
      Trader trader = tNpcManager.instance().getTraderRelation(player);
      if(trader == null) {
         return false;
      } else {
         trader.parseStatus(status);
         return true;
      }
   }

   public static boolean openTrader(Player player, TraderTrait trait, TEntityStatus status, boolean openInv) {
      Trader trader = manager.getTraderRelation(player);
      if(trader != null && !trader.equals(trait.getNPC())) {
         player.closeInventory();
         manager.removeRelation(player);

         try {
            trader = (Trader)tNpcManager.create_tNpc(trait.getNPC(), trait.getType(), player, TraderTrait.class);
         } catch (Exception var6) {
            return false;
         }
      }

      trader.parseStatus(status);
      if(openInv) {
         trader.updatePlayerInventory();
         Inventory inventory = trader.getStock().getInventory(status);
         player.openInventory(inventory);
         tNpcManager.instance().registerOpenedInventory(player, inventory);
      }

      return true;
   }

   public static boolean closeTrader(Player player) {
      Trader trader = manager.getTraderRelation(player);
      if(trader == null) {
         return false;
      } else {
         player.closeInventory();
         manager.removeRelation(player);
         return true;
      }
   }

   public boolean sellItem(Player player, Trader trader, StockItem item) {
      return false;
   }

   public boolean buyItem(Player player, Trader trader, StockItem item) {
      return false;
   }

   public void removeItem(Trader trader, String stock, ItemStack item) {}

   public void removeItem(Trader trader, String stock, StockItem item) {}

   public void removeItem(Trader trader, String stock, String item) {}

   public void addItem(Trader trader, String stock, ItemStack item) {}

   public void addItem(Trader trader, String stock, StockItem item) {}

   public void addItem(Trader trader, String stock, String item) {}

   public boolean hasItem(Trader trader, String stock, ItemStack item) {
      return false;
   }

   public boolean hasItem(Trader trader, String stock, StockItem item) {
      return false;
   }

   public boolean hasItem(Trader trader, String stock, String item) {
      return false;
   }

   public StockItem getItem(Trader trader, String stock, ItemStack item) {
      return null;
   }

   public StockItem getItem(Trader trader, String stock, StockItem item) {
      return null;
   }

   public StockItem getItem(Trader trader, String stock, String item) {
      return null;
   }

}
