package net.dandielo.citizens.traders_v3.utils;

import net.dandielo.citizens.traders_v3.traders.stock.StockItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemUtils {

   public static boolean itemHasDurability(ItemStack item) {
      int id = item.getTypeId();
      return id >= 256 && id <= 259 || id == 261 || id >= 267 && id <= 279 || id >= 283 && id <= 286 || id >= 290 && id <= 294 || id >= 298 && id <= 317 || id == 398;
   }

   public static StockItem createStockItem(ItemStack vItem) {
      ItemStack clone = vItem.clone();
      StockItem sItem = new StockItem(clone);
      return sItem;
   }

   public static ItemStack createItemStack(String data) {
      String[] d = data.split(":", 2);
      Material mat = Material.getMaterial(d[0].toUpperCase());
      if(mat == null) {
         mat = Material.getMaterial(Integer.parseInt(d[0]));
      }

      return d.length > 1?new ItemStack(mat, 1, Short.parseShort(d[1])):new ItemStack(mat);
   }
}
