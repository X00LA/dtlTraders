package net.dandielo.citizens.traders_v3;

import net.dandielo.citizens.traders_v3.TEntityStatus;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public interface TradingEntity {

   void lockAndSave();

   void onLeftClick(ItemStack var1);

   boolean onRightClick(ItemStack var1);

   void onInventoryClick(InventoryClickEvent var1);

   void onManageInventoryClick(InventoryClickEvent var1);

   TEntityStatus getStatus();
}
