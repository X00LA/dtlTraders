package net.dandielo.citizens.traders_v3.utils.items.attributes;

import java.util.Iterator;
import java.util.List;
import net.dandielo.citizens.traders_v3.TEntityStatus;
import net.dandielo.citizens.traders_v3.core.dB;
import net.dandielo.citizens.traders_v3.core.locale.LocaleManager;
import net.dandielo.citizens.traders_v3.traders.transaction.CurrencyHandler;
import net.dandielo.citizens.traders_v3.traders.transaction.TransactionInfo;
import net.dandielo.citizens.traders_v3.utils.items.StockItemAttribute;
import net.dandielo.core.items.dItem;
import net.dandielo.core.items.serialize.Attribute;
import org.bukkit.ChatColor;

@Attribute(
   name = "Price",
   key = "p",
   standalone = true,
   priority = 0
)
public class Price extends StockItemAttribute implements CurrencyHandler {

   private double price = 0.0D;


   public Price(dItem item, String key) {
      super(item, key);
   }

   public double getPrice() {
      return this.price;
   }

   public void increase(double value) {
      this.price += value;
   }

   public void decrease(double value) {
      this.price = (this.price -= value) < 0.0D?0.0D:this.price;
   }

   public void setPrice(double value) {
      this.price = value < 0.0D?0.0D:value;
   }

   public boolean deserialize(String data) {
      try {
         this.price = Double.parseDouble(data);
         return true;
      } catch (NumberFormatException var3) {
         dB.spec(dB.DebugLevel.S2_MAGIC_POWA, new Object[]{"A exception occured when parsing the price"});
         return false;
      }
   }

   public String serialize() {
      return String.format("%.2f", new Object[]{Double.valueOf(this.price)}).replace(',', '.');
   }

   public void getDescription(TEntityStatus status, List lore) {
      if(status.inManagementMode()) {
         double totalPrice = this.item.hasFlag(".sp")?this.price:this.price * (double)this.item.getAmount();
         Iterator var5 = LocaleManager.locale.getLore("item-price-summary").iterator();

         while(var5.hasNext()) {
            String pLore = (String)var5.next();
            lore.add(pLore.replace("{unit}", String.format("%.2f", new Object[]{Double.valueOf(this.price)})).replace(',', '.').replace("{total}", String.format("%.2f", new Object[]{Double.valueOf(totalPrice)})).replace(',', '.'));
         }

      }
   }

   public double getTotalPrice(TransactionInfo info) {
      return info.getTotalScaling() * this.price;
   }

   public boolean finalizeTransaction(TransactionInfo info) {
      info.getSeller().deposit(info.getTotalScaling() * this.price);
      return info.getBuyer().withdraw(info.getTotalScaling() * this.price);
   }

   public boolean allowTransaction(TransactionInfo info) {
      return this.price >= 0.0D && info.getBuyer() != null?info.getBuyer().check(info.getTotalScaling() * this.price):false;
   }

   public void getDescription(TransactionInfo info, List lore) {
      ChatColor mReqColor = this.allowTransaction(info)?ChatColor.GREEN:ChatColor.RED;
      Iterator var4 = LocaleManager.locale.getLore("item-price").iterator();

      while(var4.hasNext()) {
         String pLore = (String)var4.next();
         lore.add(pLore.replace("{price}", mReqColor + String.format("%.2f", new Object[]{Double.valueOf(info.getTotalScaling() * this.price)})).replace(',', '.'));
      }

   }

   public String getName() {
      return "Virtual money";
   }
}
