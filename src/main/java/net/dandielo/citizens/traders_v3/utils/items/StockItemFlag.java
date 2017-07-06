package net.dandielo.citizens.traders_v3.utils.items;

import java.util.List;
import net.dandielo.citizens.traders_v3.TEntityStatus;
import net.dandielo.citizens.traders_v3.traders.stock.StockItem;
import net.dandielo.citizens.traders_v3.utils.items.ShopStatus;
import net.dandielo.core.items.dItem;
import net.dandielo.core.items.serialize.ItemFlag;

public abstract class StockItemFlag extends ItemFlag {

   private ShopStatus shopStatus;


   public StockItemFlag(dItem item, String key) {
      super(item, key);
   }

   public void getDescription(TEntityStatus status, List lore) {}

   public ShopStatus getShopStatus() {
      return this.shopStatus;
   }

   public void setItem(StockItem item) {
      this.item = item;
   }

   public String getKey() {
      return this.key;
   }

   public int hashCode() {
      return this.key.hashCode();
   }
}
