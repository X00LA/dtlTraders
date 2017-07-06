package net.dandielo.citizens.traders_v3.utils.items.attributes;

import net.dandielo.citizens.traders_v3.utils.items.StockItemAttribute;
import net.dandielo.core.items.dItem;
import net.dandielo.core.items.serialize.Attribute;

@Attribute(
   name = "Multiplier",
   key = "m",
   standalone = true,
   priority = 0
)
public class Multiplier extends StockItemAttribute {

   private double multiplier;


   public Multiplier(dItem item, String key) {
      super(item, key);
   }

   public double getMultiplier() {
      return this.multiplier;
   }

   public boolean deserialize(String data) {
      try {
         this.multiplier = Double.parseDouble(data);
         return true;
      } catch (Exception var3) {
         return false;
      }
   }

   public String serialize() {
      return String.format("%.2f", new Object[]{Double.valueOf(this.multiplier)});
   }
}
