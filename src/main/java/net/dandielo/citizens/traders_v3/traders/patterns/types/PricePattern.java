package net.dandielo.citizens.traders_v3.traders.patterns.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.dandielo.citizens.traders_v3.bukkit.Perms;
import net.dandielo.citizens.traders_v3.core.dB;
import net.dandielo.citizens.traders_v3.traders.patterns.Pattern;
import net.dandielo.citizens.traders_v3.traders.stock.StockItem;
import net.dandielo.citizens.traders_v3.traders.transaction.CurrencyHandler;
import net.dandielo.citizens.traders_v3.utils.items.attributes.Multiplier;
import net.dandielo.core.items.serialize.ItemAttribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class PricePattern extends Pattern {

   private Map items;
   private Map inherits;
   private Map tiers;


   public PricePattern(String name) {
      super(name, Pattern.Type.PRICE);
      this.items = new HashMap();
      this.inherits = new HashMap();
      this.tiers = new HashMap();
   }

   public PricePattern(String name, boolean tier) {
      this(name);
      this.tier = tier;
   }

   public void loadItems(ConfigurationSection data) {
      ArrayList sell = new ArrayList();
      ArrayList buy = new ArrayList();
      this.priority = data.getInt("priority", 0);
      Iterator var4 = data.getKeys(false).iterator();

      while(var4.hasNext()) {
         String key = (String)var4.next();
         String pat;
         StockItem stockItem;
         Iterator pattern1;
         if(key.equals("all")) {
            pattern1 = data.getStringList(key).iterator();

            while(pattern1.hasNext()) {
               pat = (String)pattern1.next();
               stockItem = new StockItem(pat);
               if(this.tier) {
                  stockItem.addAttribute("t", this.getName());
               }

               sell.add(stockItem);
               buy.add(stockItem);
            }
         } else if(key.equals("sell")) {
            pattern1 = data.getStringList(key).iterator();

            while(pattern1.hasNext()) {
               pat = (String)pattern1.next();
               stockItem = new StockItem(pat);
               if(this.tier) {
                  stockItem.addAttribute("t", this.getName());
               }

               dB.spec(dB.DebugLevel.S2_MAGIC_POWA, new Object[]{"Added \"", stockItem, "\" item to the sell category"});
               sell.add(stockItem);
            }
         } else if(key.equals("buy")) {
            for(pattern1 = data.getStringList(key).iterator(); pattern1.hasNext(); buy.add(stockItem)) {
               pat = (String)pattern1.next();
               stockItem = new StockItem(pat);
               if(this.tier) {
                  stockItem.addAttribute("t", this.getName());
               }
            }
         } else if(!this.tier && key.equals("inherit")) {
            pattern1 = data.getStringList(key).iterator();

            while(pattern1.hasNext()) {
               pat = (String)pattern1.next();
               this.inherits.put(pat, (Object)null);
            }
         } else if(!key.equals("type") && !key.equals("priority")) {
            PricePattern pattern = new PricePattern(key, true);
            pattern.loadItems(data.getConfigurationSection(key));
            this.tiers.put(key, pattern);
         }
      }

      dB.spec(dB.DebugLevel.S2_MAGIC_POWA, new Object[]{"Added ", Integer.valueOf(sell.size()), " items to the sell category"});
      dB.spec(dB.DebugLevel.S2_MAGIC_POWA, new Object[]{"Added ", Integer.valueOf(buy.size()), " items to the buy category"});
      this.items.put("sell", sell);
      this.items.put("buy", buy);
   }

   public PricePattern.ItemCurrencies getItemCurrency(Player player, String stock, StockItem item) {
      PricePattern.ItemCurrencies result = new PricePattern.ItemCurrencies();
      Iterator var5 = this.inherits.entrySet().iterator();

      Entry e;
      while(var5.hasNext()) {
         e = (Entry)var5.next();
         if(e.getValue() != null && Perms.hasPerm(player, "dtl.trader.patterns." + (String)e.getKey())) {
            result.merge(((PricePattern)e.getValue()).getItemCurrency(player, stock, item));
         }
      }

      result.resetPriorities();
      var5 = ((List)this.items.get(stock)).iterator();

      while(var5.hasNext()) {
         StockItem e1 = (StockItem)var5.next();
         int tempPriority = e1.priorityMatch(item);
         if(e1.hasMultiplier()) {
            result.multiplier((Multiplier)e1.getAttribute(Multiplier.class, false), Integer.valueOf(tempPriority + 1000 * this.priority));
         }

         Iterator var8 = e1.getAttributes("p").iterator();

         while(var8.hasNext()) {
            ItemAttribute patternAttrib = (ItemAttribute)var8.next();
            if(patternAttrib instanceof CurrencyHandler) {
               result.merge((CurrencyHandler)patternAttrib, Integer.valueOf(tempPriority + 1000 * this.priority));
            }
         }
      }

      var5 = this.tiers.entrySet().iterator();

      while(var5.hasNext()) {
         e = (Entry)var5.next();
         if(Perms.hasPerm(player, "dtl.trader.tiers." + (String)e.getKey())) {
            result.merge(((PricePattern)e.getValue()).getItemCurrency(player, stock, item));
         }
      }

      return result;
   }

   private static class Pair {

      private final Object key;
      private Object value;


      public static PricePattern.Pair createPair(Object key, Object value) {
         return new PricePattern.Pair(key, value);
      }

      public Pair(Object key, Object value) {
         this.key = key;
         this.value = value;
      }
   }

   public static class ItemCurrencies {

      private Map currencies = new HashMap();
      private PricePattern.Pair multiplier = PricePattern.Pair.createPair((Object)null, Integer.valueOf(-1));


      public void merge(PricePattern.ItemCurrencies that) {
         if(((Integer)this.multiplier.value).intValue() <= ((Integer)that.multiplier.value).intValue()) {
            this.multiplier = that.multiplier;
         }

         Iterator var2 = that.currencies.entrySet().iterator();

         while(var2.hasNext()) {
            Entry currency = (Entry)var2.next();
            PricePattern.Pair tempCurrency = (PricePattern.Pair)currency.getValue();
            PricePattern.Pair mappedCurrency = (PricePattern.Pair)this.currencies.get(currency.getKey());
            if(mappedCurrency != null) {
               if(((Integer)mappedCurrency.value).intValue() <= ((Integer)tempCurrency.value).intValue()) {
                  this.currencies.put(currency.getKey(), tempCurrency);
               }
            } else {
               this.currencies.put(currency.getKey(), tempCurrency);
            }
         }

      }

      public void merge(CurrencyHandler handler, Integer priority) {
         PricePattern.Pair mappedCurrency = (PricePattern.Pair)this.currencies.get(handler.getName());
         if(mappedCurrency != null) {
            if(((Integer)mappedCurrency.value).intValue() <= priority.intValue()) {
               this.currencies.put(handler.getName(), PricePattern.Pair.createPair(handler, priority));
            }
         } else if(priority.intValue() >= 0) {
            this.currencies.put(handler.getName(), PricePattern.Pair.createPair(handler, priority));
         }

      }

      public void multiplier(Multiplier attr, Integer priority) {
         if(((Integer)this.multiplier.value).intValue() <= priority.intValue()) {
            this.multiplier = PricePattern.Pair.createPair(attr, priority);
         }

      }

      public void resetPriorities() {
         this.multiplier.value = Integer.valueOf(-1);
         Iterator var1 = this.currencies.entrySet().iterator();

         while(var1.hasNext()) {
            Entry currency = (Entry)var1.next();
            ((PricePattern.Pair)currency.getValue()).value = Integer.valueOf(-1);
         }

      }

      public Set getCurrencies() {
         HashSet result = new HashSet();
         Iterator var2 = this.currencies.values().iterator();

         while(var2.hasNext()) {
            PricePattern.Pair entry = (PricePattern.Pair)var2.next();
            result.add(entry.key);
         }

         return result;
      }

      public double getMultiplier() {
         return this.multiplier.key == null?1.0D:((Multiplier)this.multiplier.key).getMultiplier();
      }
   }
}
