package net.dandielo.citizens.traders_v3.utils.items.attributes;

import net.dandielo.citizens.traders_v3.utils.items.StockItemAttribute;
import net.dandielo.core.items.dItem;
import net.dandielo.core.items.serialize.Attribute;

@Attribute(
   name = "Slot",
   key = "s",
   required = true,
   priority = 0
)
public class Slot extends StockItemAttribute {

   private int slot = -1;


   public Slot(dItem item, String key) {
      super(item, key);
   }

   public void setSlot(int slot) {
      this.slot = slot;
   }

   public int getSlot() {
      return this.slot;
   }

   public boolean deserialize(String data) {
      try {
         this.slot = Integer.parseInt(data);
         return true;
      } catch (NumberFormatException var3) {
         return false;
      }
   }

   public String serialize() {
      return String.valueOf(this.slot);
   }
}
