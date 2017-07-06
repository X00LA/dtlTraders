package net.dandielo.core.items.serialize.core;

import java.util.ArrayList;
import java.util.List;
import net.dandielo.core.items.dItem;
import net.dandielo.core.items.serialize.Attribute;
import net.dandielo.core.items.serialize.ItemAttribute;
import org.bukkit.inventory.ItemStack;

@Attribute(
   key = "a",
   name = "Amount",
   required = true,
   priority = 5
)
public class Amount extends ItemAttribute {

   private List amounts = new ArrayList();


   public Amount(dItem item, String key) {
      super(item, key);
      this.amounts.add(Integer.valueOf(1));
   }

   public int getAmount() {
      return ((Integer)this.amounts.get(0)).intValue();
   }

   public int getAmount(int at) {
      return ((Integer)this.amounts.get(at)).intValue();
   }

   public void addAmount(int a) {
      this.amounts.add(Integer.valueOf(a));
   }

   public void setAmount(int amount) {
      this.amounts.set(0, Integer.valueOf(amount));
   }

   public boolean hasMultipleAmounts() {
      return this.amounts.size() > 1;
   }

   public List getAmounts() {
      return this.amounts;
   }

   public String serialize() {
      String result = "";

      for(int i = 0; i < this.amounts.size(); ++i) {
         result = result + this.amounts.get(i) + (i + 1 < this.amounts.size()?",":"");
      }

      return result;
   }

   public boolean deserialize(String data) {
      this.amounts.clear();

      try {
         String[] e = data.split(",");
         int var3 = e.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            String amout = e[var4];
            this.amounts.add(Integer.valueOf(Integer.parseInt(amout) < 1?1:Integer.parseInt(amout)));
         }

         return true;
      } catch (NumberFormatException var6) {
         return false;
      }
   }

   public boolean onRefactor(ItemStack item) {
      this.amounts.clear();
      this.amounts.add(Integer.valueOf(item.getAmount()));
      return true;
   }

   public void onAssign(ItemStack item, boolean abstrac) {
      item.setAmount(((Integer)this.amounts.get(0)).intValue());
   }

   public boolean same(ItemAttribute that) {
      return that == null?false:((Amount)that).getAmount() == this.getAmount();
   }

   public boolean similar(ItemAttribute that) {
      return true;
   }
}
