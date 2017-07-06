package net.dandielo.core.items.serialize.core;

import net.dandielo.core.items.dItem;
import net.dandielo.core.items.serialize.Attribute;
import net.dandielo.core.items.serialize.ItemAttribute;
import org.bukkit.inventory.ItemStack;

@Attribute(
   key = "d",
   name = "Durability"
)
public class Durability extends ItemAttribute {

   private double durabilityPercent = -1.0D;
   private short durability = 0;


   public Durability(dItem item, String key) {
      super(item, key);
   }

   public short getValue() {
      return this.durability;
   }

   public double getPercent() {
      return this.durabilityPercent;
   }

   public String serialize() {
      return this.durabilityPercent >= 0.0D?String.format("%.0f%%", new Object[]{Double.valueOf(this.durabilityPercent * 100.0D)}):String.valueOf(this.durability);
   }

   public boolean deserialize(String data) {
      try {
         if(data.endsWith("%")) {
            this.durabilityPercent = 1.0D - (double)Integer.parseInt(data.substring(0, data.length() - 1)) / 100.0D;
            this.durability = (short)((int)((double)this.item.getMaterial().getMaxDurability() * this.durabilityPercent));
         } else {
            this.durability = Short.parseShort(data.substring(0));
         }

         return true;
      } catch (NumberFormatException var3) {
         return false;
      }
   }

   public void onAssign(ItemStack item, boolean unused) {
      if(item.getType().getMaxDurability() > 0) {
         item.setDurability(this.durability);
      }

   }

   public boolean onRefactor(ItemStack item) {
      if(item.getType().getMaxDurability() == 0) {
         return false;
      } else {
         this.durability = item.getDurability();
         return true;
      }
   }

   public boolean similar(ItemAttribute attr) {
      return this.durability >= ((Durability)attr).durability;
   }

   public boolean same(ItemAttribute attr) {
      return this.durability == ((Durability)attr).durability;
   }
}
