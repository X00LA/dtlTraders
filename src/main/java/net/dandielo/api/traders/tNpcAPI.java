package net.dandielo.api.traders;

import net.dandielo.citizens.traders_v3.tNpcManager;
import org.bukkit.entity.Player;

public class tNpcAPI {

   private static tNpcManager manager = tNpcManager.instance();


   public static boolean isTNpcInventory(Player player) {
      return manager.tNpcInventoryOpened(player);
   }

}
