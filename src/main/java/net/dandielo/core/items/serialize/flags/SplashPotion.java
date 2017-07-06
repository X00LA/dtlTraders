package net.dandielo.core.items.serialize.flags;

import net.dandielo.core.items.dItem;
import net.dandielo.core.items.serialize.Attribute;
import net.dandielo.core.items.serialize.ItemFlag;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Attribute(
   name = "SplashPotion",
   key = ".splash",
   items = {},
   priority = 5
)
public class SplashPotion extends ItemFlag {

   public SplashPotion(dItem item, String key) {
      super(item, key);
   }

   public void onAssign(ItemStack item, boolean unused) {
      if(item.getType().equals(Material.SPLASH_POTION)) {
         ;
      }
   }

   public boolean onRefactor(ItemStack item) {
      return false;
   }
}
