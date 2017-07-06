package net.dandielo.citizens.traders_v3.traders.patterns.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.dandielo.citizens.traders_v3.bukkit.Perms;
import net.dandielo.citizens.traders_v3.core.dB;
import net.dandielo.citizens.traders_v3.traders.patterns.Pattern;
import net.dandielo.citizens.traders_v3.traders.stock.StockItem;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class ItemPattern extends Pattern {

   private Map items;
   private Map inherits;
   private Map tiers;


   public ItemPattern(String name) {
      super(name, Pattern.Type.ITEM);
      this.items = new HashMap();
      this.inherits = new HashMap();
      this.tiers = new HashMap();
   }

   public ItemPattern(String name, boolean tier) {
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
         dB.high(new Object[]{key});
         Iterator pattern;
         Object pat;
         StockItem stockItem;
         Iterator var9;
         Entry entry;
         if(key.equals("all")) {
            pattern = ((List)data.get("all")).iterator();

            while(pattern.hasNext()) {
               pat = pattern.next();
               if(pat instanceof String) {
                  stockItem = new StockItem((String)pat);
                  stockItem.addAttribute("pat", String.valueOf(this.priority));
                  if(this.tier) {
                     stockItem.addAttribute("t", this.getName());
                  }

                  sell.add(stockItem);
                  buy.add(stockItem);
               } else {
                  stockItem = null;

                  for(var9 = ((Map)pat).entrySet().iterator(); var9.hasNext(); stockItem = new StockItem((String)entry.getKey(), (List)entry.getValue())) {
                     entry = (Entry)var9.next();
                  }

                  stockItem.addAttribute("pat", String.valueOf(this.priority));
                  if(this.tier) {
                     stockItem.addAttribute("t", this.getName());
                  }

                  sell.add(stockItem);
                  buy.add(stockItem);
               }
            }
         } else if(key.equals("sell")) {
            pattern = ((List)data.get("sell")).iterator();

            while(pattern.hasNext()) {
               pat = pattern.next();
               if(pat instanceof String) {
                  stockItem = new StockItem((String)pat);
                  stockItem.addAttribute("pat", String.valueOf(this.priority));
                  if(this.tier) {
                     stockItem.addAttribute("t", this.getName());
                  }

                  sell.add(stockItem);
               } else {
                  stockItem = null;

                  for(var9 = ((Map)pat).entrySet().iterator(); var9.hasNext(); stockItem = new StockItem((String)entry.getKey(), (List)entry.getValue())) {
                     entry = (Entry)var9.next();
                  }

                  stockItem.addAttribute("pat", String.valueOf(this.priority));
                  if(this.tier) {
                     stockItem.addAttribute("t", this.getName());
                  }

                  sell.add(stockItem);
               }
            }
         } else if(!key.equals("buy")) {
            if(!this.tier && key.equals("inherit")) {
               pattern = data.getStringList(key).iterator();

               while(pattern.hasNext()) {
                  String pat1 = (String)pattern.next();
                  this.inherits.put(pat1, (Object)null);
               }
            } else if(!key.equals("type") && !key.equals("priority")) {
               ItemPattern pattern1 = new ItemPattern(key, true);
               pattern1.loadItems(data.getConfigurationSection(key));
               this.tiers.put(key, pattern1);
            }
         } else {
            pattern = ((List)data.get("buy")).iterator();

            while(pattern.hasNext()) {
               pat = pattern.next();
               dB.normal(new Object[]{pat});
               if(pat instanceof String) {
                  stockItem = new StockItem((String)pat);
                  stockItem.addAttribute("pat", String.valueOf(this.priority));
                  if(this.tier) {
                     stockItem.addAttribute("t", this.getName());
                  }

                  buy.add(stockItem);
               } else {
                  stockItem = null;

                  for(var9 = ((Map)pat).entrySet().iterator(); var9.hasNext(); stockItem = new StockItem((String)entry.getKey(), (List)entry.getValue())) {
                     entry = (Entry)var9.next();
                     dB.normal(new Object[]{entry.getKey()});
                     dB.normal(new Object[]{Integer.valueOf(((List)entry.getValue()).size())});
                  }

                  stockItem.addAttribute("pat", String.valueOf(this.priority));
                  if(this.tier) {
                     stockItem.addAttribute("t", this.getName());
                  }

                  buy.add(stockItem);
               }
            }
         }
      }

      this.items.put("sell", sell);
      this.items.put("buy", buy);
   }

   public List updateStock(List stock, String key, Player player) {
      Iterator var4 = ((List)this.items.get(key)).iterator();

      while(var4.hasNext()) {
         StockItem e = (StockItem)var4.next();
         if(!stock.contains(e)) {
            stock.add(e);
         }
      }

      var4 = this.tiers.entrySet().iterator();

      while(var4.hasNext()) {
         Entry e1 = (Entry)var4.next();
         if(Perms.hasPerm(player, "dtl.trader.tiers." + (String)e1.getKey())) {
            ((ItemPattern)e1.getValue()).updateStock(stock, key, player);
         }
      }

      return stock;
   }
}
