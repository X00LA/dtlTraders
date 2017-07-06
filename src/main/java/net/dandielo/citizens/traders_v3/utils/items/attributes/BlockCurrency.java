package net.dandielo.citizens.traders_v3.utils.items.attributes;

import java.util.Iterator;
import java.util.List;
import net.dandielo.citizens.traders_v3.core.locale.LocaleManager;
import net.dandielo.citizens.traders_v3.traders.stock.StockItem;
import net.dandielo.citizens.traders_v3.traders.transaction.CurrencyHandler;
import net.dandielo.citizens.traders_v3.traders.transaction.TransactionInfo;
import net.dandielo.citizens.traders_v3.utils.items.StockItemAttribute;
import net.dandielo.core.items.serialize.Attribute;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Attribute(
   key = "p",
   sub = {"b"},
   name = "Block Price",
   standalone = true,
   priority = 0
)
public class BlockCurrency extends StockItemAttribute implements CurrencyHandler {

   private ItemStack is;
   private int amount;


   public BlockCurrency(StockItem item, String key, String sub) {
      super(item, key, sub);
   }

   public boolean finalizeTransaction(TransactionInfo info) {
      String stock = info.getStock().name().toLowerCase();
      Player player = info.getPlayerParticipant();
      int amount = info.getAmount();
      boolean result = false;
      int endAmount = amount * this.amount;
      ItemStack clone = this.is.clone();
      clone.setAmount(endAmount);
      if(stock == "sell") {
         ItemStack[] contents = player.getInventory().getContents();

         for(int i = 0; i < contents.length && endAmount > 0; ++i) {
            ItemStack nItem = contents[i];
            if(nItem != null && nItem.isSimilar(clone)) {
               int diff = endAmount - nItem.getAmount();
               if(diff < 0) {
                  nItem.setAmount(-diff);
               } else {
                  player.getInventory().setItem(i, (ItemStack)null);
               }

               endAmount = diff;
            }
         }

         result = true;
      } else if(stock == "buy") {
         player.getInventory().addItem(new ItemStack[]{clone});
         result = true;
      }

      return result;
   }

   public boolean allowTransaction(TransactionInfo info) {
      String stock = info.getStock().name().toLowerCase();
      Player player = info.getPlayerParticipant();
      int amount = info.getAmount();
      boolean result = false;
      int endAmount = amount * this.amount;
      if(stock == "sell") {
         result = player.getInventory().containsAtLeast(this.is, endAmount);
      } else if(stock == "buy") {
         ItemStack[] contents = player.getInventory().getContents();

         for(int i = 0; i < contents.length && endAmount > 0; ++i) {
            if(contents[i] == null) {
               endAmount -= 64;
            } else if(contents[i].isSimilar(this.is)) {
               endAmount -= contents[i].getAmount();
            }
         }
      }

      return result;
   }

   public void getDescription(TransactionInfo info, List lore) {
      int amount = info.getAmount();
      ChatColor mReqColor = this.allowTransaction(info)?ChatColor.GREEN:ChatColor.RED;
      Iterator var5 = LocaleManager.locale.getLore("item-currency-price").iterator();

      while(var5.hasNext()) {
         String pLore = (String)var5.next();
         lore.add(pLore.replace("{amount}", String.valueOf(amount * amount)).replace("{text}", " block of ").replace("{currency}", mReqColor + this.is.getType().name().toLowerCase()));
      }

   }

   public String getName() {
      return "Item exchange currency";
   }

   public boolean deserialize(String data) {
      String[] info = data.split("-");
      this.is = new ItemStack(Material.getMaterial(info[0].toUpperCase()));
      this.amount = Integer.parseInt(info[1]);
      return true;
   }

   public String serialize() {
      return this.is.getType().name().toLowerCase() + "-" + this.amount;
   }

   public double getTotalPrice(TransactionInfo info) {
      return 0.0D;
   }
}
