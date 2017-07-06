package net.dandielo.citizens.traders_v3.utils.items;

import java.util.List;
import net.dandielo.citizens.traders_v3.TEntityStatus;
import net.dandielo.citizens.traders_v3.utils.items.ShopStatus;
import net.dandielo.core.items.dItem;
import net.dandielo.core.items.serialize.ItemAttribute;

public abstract class StockItemAttribute extends ItemAttribute {

   protected ShopStatus status;


   public StockItemAttribute(dItem item, String key) {
      super(item, key);
   }

   public StockItemAttribute(dItem item, String key, String sub) {
      super(item, key, sub);
   }

   public void getDescription(TEntityStatus status, List lore) {}

   public ShopStatus getShopStatus() {
      return this.status;
   }

   public final int hashCode() {
      return (this.key + (this.sub != null?"." + this.sub:"")).hashCode();
   }
}
