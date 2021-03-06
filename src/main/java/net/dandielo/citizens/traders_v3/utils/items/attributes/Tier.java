package net.dandielo.citizens.traders_v3.utils.items.attributes;

import net.dandielo.citizens.traders_v3.utils.items.StockItemAttribute;
import net.dandielo.core.items.dItem;
import net.dandielo.core.items.serialize.Attribute;

@Attribute(
   name = "Tier",
   key = "t",
   priority = 25,
   standalone = true
)
public class Tier extends StockItemAttribute {

   private String tier;


   public Tier(dItem item, String key) {
      super(item, key);
   }

   public boolean deserialize(String data) {
      this.tier = data;
      return true;
   }

   public String serialize() {
      return this.tier;
   }
}
