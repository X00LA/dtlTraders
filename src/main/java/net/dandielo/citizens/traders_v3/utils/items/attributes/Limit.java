package net.dandielo.citizens.traders_v3.utils.items.attributes;

import java.util.Iterator;
import java.util.List;
import net.dandielo.citizens.traders_v3.TEntityStatus;
import net.dandielo.citizens.traders_v3.core.locale.LocaleManager;
import net.dandielo.citizens.traders_v3.traders.limits.LimitManager;
import net.dandielo.citizens.traders_v3.traders.stock.StockItem;
import net.dandielo.citizens.traders_v3.utils.items.StockItemAttribute;
import net.dandielo.core.items.dItem;
import net.dandielo.core.items.serialize.Attribute;

@Attribute(
   name = "Limit",
   key = "l",
   standalone = true,
   priority = 0
)
public class Limit extends StockItemAttribute {

   private String id;
   private int limit;
   private int plimit;
   private long timeout;
   private long ptimeout;


   public Limit(dItem item, String key) {
      super(item, key);
   }

   public String getID() {
      return this.id;
   }

   public int getLimit() {
      return this.limit;
   }

   public long getTimeout() {
      return this.timeout;
   }

   public void increaseLimit(int l) {
      this.limit += l;
   }

   public void decreaseLimit(int l) {
      this.limit = this.limit - l < 0?0:this.limit - l;
   }

   public void increaseTimeout(long t) {
      this.timeout += t;
   }

   public void decreaseTimeout(long t) {
      this.timeout = this.timeout - t < 0L?0L:this.timeout - t;
   }

   public int getPlayerLimit() {
      return this.limit;
   }

   public long getPlayerTimeout() {
      return this.timeout;
   }

   public void increasePlayerLimit(int l) {
      this.limit += l;
   }

   public void decreasePlayerLimit(int l) {
      this.limit = this.limit - l < 0?0:this.limit - l;
   }

   public void increasePlayerTimeout(long t) {
      this.timeout += t;
   }

   public void decreasePlayerTimeout(long t) {
      this.timeout = this.timeout - t < 0L?0L:this.timeout - t;
   }

   public boolean deserialize(String raw) {
      String[] data = raw.split("/");

      try {
         this.id = data[0];
         this.limit = Integer.parseInt(data[1]);
         if(data.length > 2) {
            this.timeout = LimitManager.parseTimeout(data[2]);
            if(data.length == 5) {
               this.plimit = Integer.parseInt(data[3]);
               this.ptimeout = LimitManager.parseTimeout(data[4]);
            }
         }

         return true;
      } catch (NumberFormatException var4) {
         return false;
      }
   }

   public String serialize() {
      String result = this.id + "/" + this.limit;
      if(this.timeout != 0L) {
         result = result + "/" + LimitManager.timeoutString(this.timeout);
      }

      if(this.plimit != 0) {
         result = result + "/" + this.plimit + "/" + LimitManager.timeoutString(this.ptimeout);
      }

      return result;
   }

   public void getDescription(TEntityStatus status, List lore) {
      if(status.inManagementMode()) {
         Iterator var3 = LocaleManager.locale.getLore("item-rawLimit").iterator();

         while(var3.hasNext()) {
            String pLore = (String)var3.next();
            lore.add(pLore.replace("{limit}", String.valueOf(this.limit)).replace("{timeout}", LimitManager.timeoutString(this.timeout)));
         }

      }
   }

   public static List loreRequest(String player, StockItem item, List lore, TEntityStatus status) {
      LimitManager limits = LimitManager.self;
      if(!item.hasAttribute(Limit.class)) {
         return lore;
      } else {
         Iterator var5 = LocaleManager.locale.getLore("item-" + status.asStock() + "-limit").iterator();

         String pLore;
         while(var5.hasNext()) {
            pLore = (String)var5.next();
            lore.add(pLore.replace("{limit-total}", String.valueOf(limits.getTotalLimit(item))).replace("{limit-used}", String.valueOf(Math.abs(limits.getTotalUsed(item)))).replace("{limit-avail}", String.valueOf(limits.getTotalLimit(item) - (long)Math.abs(limits.getTotalUsed(item)))));
         }

         var5 = LocaleManager.locale.getLore("item-" + status.asStock() + "-plimit").iterator();

         while(var5.hasNext()) {
            pLore = (String)var5.next();
            lore.add(pLore.replace("{limit-total}", String.valueOf(limits.getTotalLimit(item))).replace("{limit-used}", String.valueOf(Math.abs(limits.getTotalUsed(item)))).replace("{limit-avail}", String.valueOf(limits.getTotalLimit(item) - (long)Math.abs(limits.getTotalUsed(item)))));
         }

         return lore;
      }
   }
}
