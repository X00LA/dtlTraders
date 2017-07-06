package net.dandielo.citizens.traders_v3.utils.items.attributes;

import net.dandielo.citizens.traders_v3.utils.items.StockItemAttribute;
import net.dandielo.core.items.dItem;
import net.dandielo.core.items.serialize.Attribute;
import net.dandielo.core.items.serialize.ItemAttribute;

@Attribute(
   name = "PatternItem",
   key = "pat",
   standalone = true
)
public class PatternItem extends StockItemAttribute {

   private int priority = 0;


   public PatternItem(dItem item, String key) {
      super(item, key);
   }

   public boolean deserialize(String data) {
      this.priority = Integer.parseInt(data);
      return true;
   }

   public String serialize() {
      return String.valueOf(this.priority);
   }

   public boolean similar(ItemAttribute attr) {
      return this.equals(attr);
   }

   public boolean equals(ItemAttribute attr) {
      return this.priority >= ((PatternItem)attr).priority;
   }
}
