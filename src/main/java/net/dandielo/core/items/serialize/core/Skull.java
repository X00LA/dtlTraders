package net.dandielo.core.items.serialize.core;

import net.dandielo.core.items.dItem;
import net.dandielo.core.items.serialize.Attribute;
import net.dandielo.core.items.serialize.ItemAttribute;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

@Attribute(
   name = "Skull",
   key = "sk",
   priority = 5,
   items = {Material.SKULL, Material.SKULL_ITEM}
)
public class Skull extends ItemAttribute {

   private String owner;


   public Skull(dItem item, String key) {
      super(item, key);
   }

   public String serialize() {
      return this.owner;
   }

   public boolean deserialize(String data) {
      this.owner = data;
      return true;
   }

   public void onAssign(ItemStack item, boolean unused) {
      if(item.getItemMeta() instanceof SkullMeta) {
         SkullMeta meta = (SkullMeta)item.getItemMeta();
         meta.setOwner(this.owner);
         item.setItemMeta(meta);
      }

   }

   public boolean onRefactor(ItemStack item) {
      if(!(item.getItemMeta() instanceof SkullMeta)) {
         return false;
      } else {
         SkullMeta meta = (SkullMeta)item.getItemMeta();
         this.owner = meta.getOwner();
         item.setItemMeta(meta);
         return true;
      }
   }
}
