package net.dandielo.citizens.traders_v3.traders.stock;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.dandielo.citizens.traders_v3.TEntityStatus;
import net.dandielo.citizens.traders_v3.utils.items.StockItemAttribute;
import net.dandielo.citizens.traders_v3.utils.items.StockItemFlag;
import net.dandielo.citizens.traders_v3.utils.items.attributes.Limit;
import net.dandielo.citizens.traders_v3.utils.items.attributes.Multiplier;
import net.dandielo.citizens.traders_v3.utils.items.attributes.Price;
import net.dandielo.citizens.traders_v3.utils.items.attributes.Slot;
import net.dandielo.core.items.dItem;
import net.dandielo.core.items.serialize.ItemAttribute;
import net.dandielo.core.items.serialize.ItemFlag;
import net.dandielo.core.items.serialize.core.Amount;
import net.dandielo.core.items.serialize.flags.Lore;
import org.bukkit.inventory.ItemStack;

public final class StockItem extends dItem {

   public StockItem(ItemStack item) {
      super(item);
   }

   public StockItem(String format) {
      super(format);
   }

   public StockItem(String format, List list) {
      super(format, list);
   }

   public String toString() {
      return this.serialize();
   }

   public List getDescription(TEntityStatus status) {
      ArrayList lore = new ArrayList();
      if(this.hasFlag(Lore.class)) {
         lore.addAll(this.getLore());
      }

      Iterator var3 = this.attributes.iterator();

      while(var3.hasNext()) {
         ItemAttribute itemFlag = (ItemAttribute)var3.next();
         if(itemFlag instanceof StockItemAttribute) {
            StockItemAttribute siFlag = (StockItemAttribute)itemFlag;
            siFlag.getDescription(status, lore);
         }
      }

      var3 = this.flags.iterator();

      while(var3.hasNext()) {
         ItemFlag itemFlag1 = (ItemFlag)var3.next();
         if(itemFlag1 instanceof StockItemFlag) {
            StockItemFlag siFlag1 = (StockItemFlag)itemFlag1;
            siFlag1.getDescription(status, lore);
         }
      }

      return lore;
   }

   public boolean hasPrice() {
      return this.hasAttribute(Price.class);
   }

   public double getPrice() {
      return this.hasAttribute(Price.class)?((Price)this.getAttribute(Price.class, false)).getPrice():-1.0D;
   }

   public String getPriceFormated() {
      return this.hasPrice()?String.format("%.2f", new Object[]{Double.valueOf(this.getPrice())}):"none";
   }

   public Price getPriceAttr() {
      return (Price)this.getAttribute(Price.class, true);
   }

   public double getMultiplier() {
      return this.hasAttribute(Multiplier.class)?((Multiplier)this.getAttribute(Multiplier.class, false)).getMultiplier():-1.0D;
   }

   public boolean hasMultiplier() {
      return this.hasAttribute(Multiplier.class);
   }

   public Limit getLimitAttr() {
      return (Limit)this.getAttribute(Limit.class, true);
   }

   public int getSlot() {
      return this.hasAttribute(Slot.class)?((Slot)this.getAttribute(Slot.class, false)).getSlot():-1;
   }

   public boolean checkSlot(int slot) {
      return ((Slot)this.getAttribute(Slot.class, true)).getSlot() == slot;
   }

   public void setSlot(int slot) {
      ((Slot)this.getAttribute(Slot.class, true)).setSlot(slot);
   }

   public boolean hasMultipleAmounts() {
      return ((Amount)this.getAttribute(Amount.class, true)).hasMultipleAmounts();
   }

   public int getAmount(int i) {
      return ((Amount)this.getAttribute(Amount.class, true)).getAmount(i);
   }

   public List getAmounts() {
      return ((Amount)this.getAttribute(Amount.class, true)).getAmounts();
   }

   public void addAmount(int a) {
      ((Amount)this.getAttribute(Amount.class, true)).addAmount(a);
   }

   public List getLore() {
      return this.hasFlag(Lore.class)?((Lore)this.getFlag(Lore.class, false)).getLore():null;
   }

   public List getRawLore() {
      return this.hasFlag(Lore.class)?((Lore)this.getFlag(Lore.class, false)).getRawLore():null;
   }
}
