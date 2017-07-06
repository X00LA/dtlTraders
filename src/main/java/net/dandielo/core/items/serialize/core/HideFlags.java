package net.dandielo.core.items.serialize.core;

import net.dandielo.core.items.dItem;
import net.dandielo.core.items.serialize.Attribute;
import net.dandielo.core.items.serialize.ItemAttribute;
import net.dandielo.core.utils.NBTItemStack;
import org.bukkit.inventory.ItemStack;

@Attribute(
   name = "HideFlags",
   key = ".hidef",
   priority = 5
)
public class HideFlags extends ItemAttribute {

   private int flags = 0;


   public HideFlags(dItem item, String key) {
      super(item, key);
   }

   public ItemStack onNativeAssign(ItemStack item, boolean unused) {
      NBTItemStack helper = new NBTItemStack(item);
      helper.setInt("HideFlags", this.flags);
      return helper.getItemStack();
   }

   public boolean onRefactor(ItemStack item) {
      NBTItemStack helper = new NBTItemStack(item);
      this.flags = helper.getInt("HideFlags");
      return this.flags != 0;
   }

   public String serialize() {
      return Integer.toString(this.flags);
   }

   public boolean deserialize(String data) {
      boolean result = false;

      try {
         this.flags = Integer.parseInt(data);
         result = true;
      } catch (Exception var4) {
         ;
      }

      return result;
   }
}
