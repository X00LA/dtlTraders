package net.dandielo.core.items.serialize.core;

import net.dandielo.core.items.dItem;
import net.dandielo.core.items.serialize.Attribute;
import net.dandielo.core.items.serialize.ItemAttribute;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Attribute(
   name = "Map",
   key = "map",
   priority = 5,
   items = {Material.MAP}
)
public class Map extends ItemAttribute {

   public Map(dItem item, String key) {
      super(item, key);
   }

   public boolean deserialize(String data) {
      return true;
   }

   public String serialize() {
      return null;
   }

   public void onAssign(ItemStack item, boolean unused) {}

   public boolean onRefactor(ItemStack item) {
      return false;
   }
}
