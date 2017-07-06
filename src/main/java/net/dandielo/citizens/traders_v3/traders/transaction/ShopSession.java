package net.dandielo.citizens.traders_v3.traders.transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.dandielo.citizens.traders_v3.core.locale.LocaleManager;
import net.dandielo.citizens.traders_v3.traders.Trader;
import net.dandielo.citizens.traders_v3.traders.patterns.Pattern;
import net.dandielo.citizens.traders_v3.traders.patterns.PatternManager;
import net.dandielo.citizens.traders_v3.traders.patterns.types.PricePattern;
import net.dandielo.citizens.traders_v3.traders.setting.Settings;
import net.dandielo.citizens.traders_v3.traders.stock.StockItem;
import net.dandielo.citizens.traders_v3.traders.transaction.CurrencyHandler;
import net.dandielo.citizens.traders_v3.traders.transaction.TransactionInfo;
import net.dandielo.citizens.traders_v3.traders.transaction.participants.PlayerParticipant;
import net.dandielo.citizens.traders_v3.traders.transaction.participants.TraderParticipant;
import net.dandielo.core.items.serialize.ItemAttribute;
import org.bukkit.entity.Player;

public class ShopSession {

   private Trader trader;
   private Settings settings;
   private Player player;
   private Map cache = new HashMap();


   public ShopSession(Trader trader, Player player) {
      this.trader = trader;
      this.settings = trader.getSettings();
      this.player = player;
   }

   public ShopSession(Settings settings, Player player) {
      this.trader = null;
      this.settings = settings;
      this.player = player;
   }

   public PricePattern.ItemCurrencies getCurrencies(String stock, StockItem item) {
      if(!this.cache.containsKey(stock)) {
         this.cache.put(stock, new HashMap());
      }

      Map cachedStock = (Map)this.cache.get(stock);
      if(cachedStock.containsKey(item)) {
         return (PricePattern.ItemCurrencies)cachedStock.get(item);
      } else {
         PricePattern.ItemCurrencies currencies = new PricePattern.ItemCurrencies();
         List patterns = PatternManager.getPatterns(this.settings.getPatterns());
         Iterator var6 = patterns.iterator();

         while(var6.hasNext()) {
            Pattern attr = (Pattern)var6.next();
            if(attr instanceof PricePattern) {
               currencies.merge(((PricePattern)attr).getItemCurrency(this.player, stock, item));
            }
         }

         currencies.resetPriorities();
         var6 = item.getAttributes("p").iterator();

         while(var6.hasNext()) {
            ItemAttribute attr1 = (ItemAttribute)var6.next();
            if(attr1 instanceof CurrencyHandler) {
               currencies.merge((CurrencyHandler)attr1, Integer.valueOf(0));
            }
         }

         cachedStock.put(item, currencies);
         return currencies;
      }
   }

   public List getDescription(String stock, StockItem item, int amount) {
      PricePattern.ItemCurrencies currencies = this.getCurrencies(stock, item);
      ArrayList result = new ArrayList();
      Iterator var6 = currencies.getCurrencies().iterator();

      while(var6.hasNext()) {
         CurrencyHandler handler = (CurrencyHandler)var6.next();
         handler.getDescription((new TransactionInfo(stock, item, amount)).setParticipants(new PlayerParticipant(this.player), new TraderParticipant(this.trader)).setMultiplier(currencies.getMultiplier()), result);
      }

      if(result.size() > 0) {
         if(stock.equals("sell")) {
            result.addAll(0, LocaleManager.locale.getLore("item-price-list"));
            result.addAll(LocaleManager.locale.getLore("item-" + stock));
         } else {
            result.addAll(0, LocaleManager.locale.getLore("item-worth-list"));
            result.addAll(LocaleManager.locale.getLore("item-player-" + stock));
         }
      }

      return result;
   }

   public boolean allowTransaction(String stock, StockItem item, int amount) {
      if(this.trader == null) {
         return false;
      } else {
         PricePattern.ItemCurrencies currencies = this.getCurrencies(stock, item);
         boolean result = currencies.getCurrencies().size() > 0;

         CurrencyHandler handler;
         for(Iterator var6 = currencies.getCurrencies().iterator(); var6.hasNext(); result &= handler.allowTransaction((new TransactionInfo(stock, item, amount)).setParticipants(new PlayerParticipant(this.player), new TraderParticipant(this.trader)).setMultiplier(currencies.getMultiplier()))) {
            handler = (CurrencyHandler)var6.next();
         }

         return result;
      }
   }

   public boolean finalizeTransaction(String stock, StockItem item, int amount) {
      if(this.trader == null) {
         return false;
      } else {
         PricePattern.ItemCurrencies currencies = this.getCurrencies(stock, item);
         boolean result = currencies.getCurrencies().size() > 0;

         CurrencyHandler handler;
         for(Iterator var6 = currencies.getCurrencies().iterator(); var6.hasNext(); result &= handler.finalizeTransaction((new TransactionInfo(stock, item, amount)).setParticipants(new PlayerParticipant(this.player), new TraderParticipant(this.trader)).setMultiplier(currencies.getMultiplier()))) {
            handler = (CurrencyHandler)var6.next();
         }

         return result;
      }
   }

   public double getCurrencyValue(String stock, StockItem item, int amount, Class clazz) {
      CurrencyHandler result = null;
      PricePattern.ItemCurrencies currencies = this.getCurrencies(stock, item);
      Iterator it = currencies.getCurrencies().iterator();

      while(it.hasNext() && result == null) {
         result = (CurrencyHandler)it.next();
         if(!clazz.isInstance(result)) {
            result = null;
         }
      }

      return result != null?result.getTotalPrice((new TransactionInfo(stock, item, amount)).setParticipants(new PlayerParticipant(this.player), new TraderParticipant(this.trader)).setMultiplier(currencies.getMultiplier())):0.0D;
   }
}
