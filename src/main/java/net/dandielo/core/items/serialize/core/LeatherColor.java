package net.dandielo.core.items.serialize.core;

import net.dandielo.core.items.dItem;
import net.dandielo.core.items.serialize.Attribute;
import net.dandielo.core.items.serialize.ItemAttribute;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

@Attribute(
   name = "Leather color",
   key = "lc",
   priority = 5,
   standalone = true,
   items = {Material.LEATHER_BOOTS, Material.LEATHER_CHESTPLATE, Material.LEATHER_HELMET, Material.LEATHER_LEGGINGS}
)
public class LeatherColor extends ItemAttribute {

   private Color color;


   public LeatherColor(dItem item, String key) {
      super(item, key);
   }

   public boolean deserialize(String data) {
      try {
         String[] e = data.split("\\^", 3);
         this.color = Color.fromRGB(Integer.parseInt(e[0]), Integer.parseInt(e[1]), Integer.parseInt(e[2]));
         return true;
      } catch (Exception var3) {
         return false;
      }
   }

   public String serialize() {
      return this.color.getRed() + "^" + this.color.getGreen() + "^" + this.color.getBlue();
   }

   public void onAssign(ItemStack item, boolean unused) {
      if(isLeatherArmor(item)) {
         LeatherArmorMeta meta = (LeatherArmorMeta)item.getItemMeta();
         meta.setColor(this.color);
         item.setItemMeta(meta);
      }
   }

   public boolean onRefactor(ItemStack item) {
      if(isLeatherArmor(item) && item.hasItemMeta()) {
         this.color = ((LeatherArmorMeta)item.getItemMeta()).getColor();
         return true;
      } else {
         return false;
      }
   }

   public boolean same(ItemAttribute data) {
      return ((LeatherColor)data).color.getBlue() == this.color.getBlue() && ((LeatherColor)data).color.getGreen() == this.color.getGreen() && ((LeatherColor)data).color.getRed() == this.color.getRed();
   }

   public boolean similar(ItemAttribute data) {
      return this.same(data);
   }

   private static boolean isLeatherArmor(ItemStack item) {
      Material mat = item.getType();
      return mat.equals(Material.LEATHER_BOOTS) || mat.equals(Material.LEATHER_CHESTPLATE) || mat.equals(Material.LEATHER_HELMET) || mat.equals(Material.LEATHER_LEGGINGS);
   }
}
