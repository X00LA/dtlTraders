package net.dandielo.citizens.traders_v3.bukkit.commands;

import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.MobType;
import net.dandielo.citizens.traders_v3.TEntityStatus;
import net.dandielo.citizens.traders_v3.tNpcManager;
import net.dandielo.citizens.traders_v3.bukkit.DtlTraders;
import net.dandielo.citizens.traders_v3.core.commands.Command;
import net.dandielo.citizens.traders_v3.core.events.trader.TraderCreateEvent;
import net.dandielo.citizens.traders_v3.core.exceptions.InvalidTraderTypeException;
import net.dandielo.citizens.traders_v3.core.exceptions.TraderTypeNotFoundException;
import net.dandielo.citizens.traders_v3.core.locale.LocaleManager;
import net.dandielo.citizens.traders_v3.traders.Trader;
import net.dandielo.citizens.traders_v3.traders.setting.GlobalSettings;
import net.dandielo.citizens.traders_v3.traders.setting.Settings;
import net.dandielo.citizens.traders_v3.traders.stock.StockItem;
import net.dandielo.citizens.traders_v3.traits.TraderTrait;
import net.dandielo.citizens.traders_v3.traits.WalletTrait;
import net.dandielo.citizens.traders_v3.utils.items.attributes.Price;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class TraderCommands {

   private static LocaleManager locale = LocaleManager.locale;


   @Command(
      name = "trader",
      syntax = "create {args}",
      perm = "dtl.trader.commands.create",
      desc = "creates a new trader with the given arguments | \'e:\', \'t:\'",
      usage = "- /trader create Wool trader e:sheep t:server",
      npc = false
   )
   public void traderCreate(DtlTraders plugin, Player sender, Trader trader, Map args) throws TraderTypeNotFoundException, InvalidTraderTypeException {
      String name = (String)args.get("free");
      EntityType entity = EntityType.valueOf(args.get("e") == null?"PLAYER":((String)args.get("e")).toUpperCase());
      String type = args.get("t") == null?"server":(String)args.get("t");
      if(name == null) {
         locale.sendMessage(sender, "error-argument-missing", new Object[]{"argument", "#name"});
      } else {
         if(entity == null) {
            entity = EntityType.PLAYER;
         }

         NPC npc = CitizensAPI.getNPCRegistry().createNPC(entity, name);
         npc.addTrait(TraderTrait.class);
         npc.addTrait(WalletTrait.class);
         WalletTrait wallet = (WalletTrait)npc.getTrait(WalletTrait.class);
         wallet.setType(GlobalSettings.getDefaultWallet());
         wallet.setMoney(GlobalSettings.getWalletStartBalance());
         npc.addTrait(MobType.class);
         ((MobType)npc.getTrait(MobType.class)).setType(entity);
         npc.spawn(sender.getLocation());
         Trader nTrader = (Trader)tNpcManager.create_tNpc(npc, type, sender, TraderTrait.class);
         nTrader.getSettings().setType(type);
         nTrader.parseStatus(TEntityStatus.MANAGE_SELL);
         nTrader.parseStatus(TEntityStatus.MANAGE_UNLOCKED);
         tNpcManager.instance().registerRelation(sender, nTrader);
         locale.sendMessage(sender, "trader-created", new Object[]{"player", sender.getName(), "trader", name});
         locale.sendMessage(sender, "trader-managermode-enabled", new Object[]{"npc", npc.getName()});
         locale.sendMessage(sender, "trader-managermode-toggled", new Object[]{"mode", "#stock-sell"});
         (new TraderCreateEvent(nTrader, sender)).callEvent();
      }
   }

   @Command(
      name = "trader",
      syntax = "stockname <action> {args}",
      perm = "dtl.trader.commands.stockname",
      desc = "Shows/resets or changes the stock name",
      usage = "- /trader stockname set Santas Stock",
      npc = true
   )
   public void settingStockName(DtlTraders plugin, Player sender, Trader trader, Map args) {
      String action = (String)args.get("action");
      if(action.equals("set")) {
         if(args.get("free") == null) {
            locale.sendMessage(sender, "error-argument-missing", new Object[]{"argument", "{args}"});
            return;
         }

         trader.getSettings().setStockFormat((String)args.get("free"));
         locale.sendMessage(sender, "key-change", new Object[]{"key", "#stock-name", "value", trader.getSettings().getStockFormat()});
      } else if(action.equals("reset")) {
         trader.getSettings().setStockFormat(Settings.getGlobalStockNameFormat());
         locale.sendMessage(sender, "key-change", new Object[]{"key", "#stock-name", "value", trader.getSettings().getStockFormat()});
      } else if(action.equals("show")) {
         locale.sendMessage(sender, "key-value", new Object[]{"key", "#stock-name", "value", trader.getSettings().getStockFormat()});
      } else {
         locale.sendMessage(sender, "error-argument-invalid", new Object[]{"argument", "<action>"});
      }

   }

   @Command(
      name = "trader",
      syntax = "stocksize <action> (size)",
      perm = "dtl.trader.commands.stocksize",
      desc = "Shows/resets or changes the stock size",
      usage = "- /trader stocksize set 5",
      npc = true
   )
   public void settingStockSize(DtlTraders plugin, Player sender, Trader trader, Map args) {
      String action = (String)args.get("action");
      if(action.equals("set")) {
         if(args.get("size") == null) {
            locale.sendMessage(sender, "error-argument-missing", new Object[]{"argument", "(size)"});
            return;
         }

         int size = Integer.parseInt((String)args.get("size"));
         if(size < 0 || size > 6) {
            locale.sendMessage(sender, "error-argument-invalid", new Object[]{"argument", "(size)"});
            return;
         }

         trader.getSettings().setStockSize(size);
         locale.sendMessage(sender, "key-change", new Object[]{"key", "#stock-size", "value", String.valueOf(trader.getSettings().getStockSize())});
      } else if(action.equals("reset")) {
         trader.getSettings().setStockSize(Settings.getGlobalStockSize());
         locale.sendMessage(sender, "key-change", new Object[]{"key", "#stock-size", "value", String.valueOf(trader.getSettings().getStockSize())});
      } else if(action.equals("show")) {
         locale.sendMessage(sender, "key-value", new Object[]{"key", "#stock-size", "value", String.valueOf(trader.getSettings().getStockSize())});
      } else {
         locale.sendMessage(sender, "error-argument-invalid", new Object[]{"argument", "<action>"});
      }

   }

   @Command(
      name = "trader",
      syntax = "startstock <action> (stock)",
      perm = "dtl.trader.commands.startstock",
      desc = "Shows/resets or changes the starting stock (buy|sell)",
      usage = "- /trader startstock set buy",
      npc = true
   )
   public void settingStockStart(DtlTraders plugin, Player sender, Trader trader, Map args) {
      String action = (String)args.get("action");
      if(action.equals("set")) {
         if(args.get("stock") == null) {
            locale.sendMessage(sender, "error-argument-missing", new Object[]{"argument", "(stock)"});
            return;
         }

         trader.getSettings().setStockStart((String)args.get("stock"));
         locale.sendMessage(sender, "key-change", new Object[]{"key", "#stock-start", "value", trader.getSettings().getStockStart()});
      } else if(action.equals("reset")) {
         trader.getSettings().setStockStart(Settings.getGlobalStockStart());
         locale.sendMessage(sender, "key-change", new Object[]{"key", "#stock-start", "value", trader.getSettings().getStockStart()});
      } else if(action.equals("show")) {
         locale.sendMessage(sender, "key-value", new Object[]{"key", "#stock-start", "value", trader.getSettings().getStockStart()});
      } else {
         locale.sendMessage(sender, "error-argument-invalid", new Object[]{"argument", "<action>"});
      }

   }

   @Command(
      name = "trader",
      syntax = "wallet <option> <action> (value)",
      perm = "dtl.trader.commands.wallet",
      desc = "Shows/sets the wallet type or its settings (infinite|private|owner|player)",
      usage = "- /trader wallet set type player",
      npc = true
   )
   public void settingWallet(DtlTraders plugin, Player sender, Trader trader, Map args) {
      String option = (String)args.get("option");
      String action = (String)args.get("action");
      if(action.equals("set")) {
         String wallet = (String)args.get("value");
         if(wallet == null) {
            locale.sendMessage(sender, "error-argument-missing", new Object[]{"argument", "(value)"});
            return;
         }

         WalletTrait wallet1 = (WalletTrait)trader.getNPC().getTrait(WalletTrait.class);
         if(option.equals("type")) {
            wallet1.setType(wallet);
            locale.sendMessage(sender, "key-change", new Object[]{"key", "#wallet-type", "value", wallet1.getType()});
         } else if(option.equals("player")) {
            OfflinePlayer e = null;
            OfflinePlayer[] var10 = Bukkit.getOfflinePlayers();
            int var11 = var10.length;

            for(int var12 = 0; var12 < var11; ++var12) {
               OfflinePlayer pl = var10[var12];
               if(pl.getName().equals(wallet)) {
                  e = pl;
               }
            }

            wallet1.setPlayer(e);
            locale.sendMessage(sender, "key-change", new Object[]{"key", "#wallet-player", "value", wallet1.getPlayer()});
         } else if(option.equals("amount")) {
            try {
               wallet1.setMoney(Double.parseDouble(wallet));
               locale.sendMessage(sender, "key-change", new Object[]{"key", "#wallet-balance", "value", String.valueOf(wallet1.getBalance())});
            } catch (NumberFormatException var14) {
               locale.sendMessage(sender, "error-argument-invalid", new Object[]{"argument", "(value)"});
            }
         } else {
            locale.sendMessage(sender, "error-argument-invalid", new Object[]{"argument", "<option>"});
         }
      } else if(action.equals("show")) {
         WalletTrait var15 = (WalletTrait)trader.getNPC().getTrait(WalletTrait.class);
         if(option.equals("type")) {
            locale.sendMessage(sender, "key-value", new Object[]{"key", "#wallet-type", "value", var15.getType()});
         } else if(option.equals("player")) {
            locale.sendMessage(sender, "key-value", new Object[]{"key", "#wallet-player", "value", var15.getPlayer()});
         } else if(option.equals("amount")) {
            locale.sendMessage(sender, "key-value", new Object[]{"key", "#wallet-balance", "value", String.valueOf(var15.getBalance())});
         } else {
            locale.sendMessage(sender, "error-argument-invalid", new Object[]{"argument", "<option>"});
         }
      } else {
         locale.sendMessage(sender, "error-argument-invalid", new Object[]{"argument", "<action>"});
      }

   }

   @Command(
      name = "trader",
      syntax = "sellprice {args}",
      perm = "dtl.trader.commands.sellprice",
      desc = "Sets price to an item that matches the string",
      usage = "- /trader sellprice 33 p:12.11",
      npc = true
   )
   public void sellprice(DtlTraders plugin, Player sender, Trader trader, Map args) {
      String itemString = (String)args.get("free");
      int items = 0;
      StockItem price = new StockItem(itemString);
      Iterator var8 = trader.getStock().getStock("sell").iterator();

      while(var8.hasNext()) {
         StockItem item = (StockItem)var8.next();
         if(item.priorityMatch(price) >= 0) {
            ++items;
            if(item.hasAttribute(Price.class)) {
               item.getPriceAttr().setPrice(price.getPrice());
            } else {
               item.addAttribute("p", price.getPriceFormated());
            }
         }
      }

      locale.sendMessage(sender, "trader-stock-price-set", new Object[]{"items", String.valueOf(items)});
   }

   @Command(
      name = "trader",
      syntax = "buyprice {args}",
      perm = "dtl.trader.commands.buyprice",
      desc = "Sets price to an item that matches the string",
      usage = "- /trader buyprice 33 p:12.11",
      npc = true
   )
   public void buyprice(DtlTraders plugin, Player sender, Trader trader, Map args) {
      String itemString = (String)args.get("free");
      int items = 0;
      StockItem price = new StockItem(itemString);
      Iterator var8 = trader.getStock().getStock("buy").iterator();

      while(var8.hasNext()) {
         StockItem item = (StockItem)var8.next();
         if(price.priorityMatch(item) >= 0) {
            ++items;
            if(item.hasAttribute(Price.class)) {
               item.getPriceAttr().setPrice(price.getPrice());
            } else {
               item.addAttribute("p", price.getPriceFormated());
            }
         }
      }

      locale.sendMessage(sender, "trader-stock-price-set", new Object[]{"items", String.valueOf(items)});
   }

   @Command(
      name = "trader",
      syntax = "reset <option>",
      perm = "dtl.trader.commands.reset",
      npc = true
   )
   public void resetSettings(DtlTraders plugin, Player sender, Trader trader, Map args) {
      if(((String)args.get("option")).equals("prices")) {
         Iterator var5 = trader.getStock().getStock("sell").iterator();

         while(var5.hasNext()) {
            StockItem item = (StockItem)var5.next();
            item.removeAttribute(Price.class);
         }

         locale.sendMessage(sender, "trader-stock-price-reset", new Object[0]);
      } else {
         locale.sendMessage(sender, "error-argument-invalid", new Object[]{"argument", "<action>"});
      }

   }

   @Command(
      name = "trader",
      syntax = "pattern <action> (pattern)",
      perm = "dtl.trader.commands.pattern",
      desc = "Allows to manage patterns for a trader",
      usage = "- /trader pattern set pattern_na,e",
      npc = true
   )
   public void setPattern(DtlTraders plugin, Player sender, Trader trader, Map args) {
      String result;
      Iterator var6;
      String pat;
      if(((String)args.get("action")).equals("show")) {
         result = "";

         for(var6 = trader.getSettings().getPatterns().iterator(); var6.hasNext(); result = result + ChatColor.DARK_AQUA + pat + ", " + ChatColor.RESET) {
            pat = (String)var6.next();
         }

         locale.sendMessage(sender, "key-value", new Object[]{"key", "#pattern-list", "value", result});
      } else if(((String)args.get("action")).equals("add")) {
         if(!args.containsKey("pattern")) {
            locale.sendMessage(sender, "error-argument-missing", new Object[]{"argument", "(pattern)"});
            return;
         }

         trader.getSettings().getPatterns().add(args.get("pattern"));
         locale.sendMessage(sender, "key-change", new Object[]{"key", "#pattern-add", "value", args.get("pattern")});
      } else if(((String)args.get("action")).equals("remove")) {
         if(!args.containsKey("pattern")) {
            locale.sendMessage(sender, "error-argument-missing", new Object[]{"argument", "(pattern)"});
            return;
         }

         trader.getSettings().getPatterns().remove(args.get("pattern"));
         locale.sendMessage(sender, "key-change", new Object[]{"key", "#pattern-remove", "value", args.get("pattern")});
      } else if(((String)args.get("action")).equals("removeall")) {
         result = "";

         for(var6 = trader.getSettings().getPatterns().iterator(); var6.hasNext(); result = result + ChatColor.DARK_AQUA + pat + ", " + ChatColor.RESET) {
            pat = (String)var6.next();
         }

         trader.getSettings().getPatterns().clear();
         locale.sendMessage(sender, "key-change", new Object[]{"key", "#pattern-remove-all", "value", result});
      } else if(((String)args.get("action")).equals("default")) {
         trader.getSettings().getPatterns().clear();
         trader.getSettings().getPatterns().addAll(Settings.defaultPatterns());
         result = "";

         for(var6 = trader.getSettings().getPatterns().iterator(); var6.hasNext(); result = result + ChatColor.DARK_AQUA + pat + ", " + ChatColor.RESET) {
            pat = (String)var6.next();
         }

         locale.sendMessage(sender, "key-change", new Object[]{"key", "#pattern-default", "value", result});
      } else {
         locale.sendMessage(sender, "error-argument-invalid", new Object[]{"argument", "<action>"});
      }

   }

   @Command(
      name = "trader",
      syntax = "manage <trader>",
      perm = "dtl.trader.commands.manage",
      usage = "",
      desc = "",
      npc = false
   )
   public void traderManage(DtlTraders plugin, Player sender, Trader trader, Map args) throws TraderTypeNotFoundException, InvalidTraderTypeException {
      Iterator it = CitizensAPI.getNPCRegistry().iterator();
      Pattern pat = Pattern.compile("&.");
      NPC result = null;

      while(it.hasNext() && result == null) {
         if(!pat.matcher((result = (NPC)it.next()).getName()).replaceAll("").equals(args.get("trader"))) {
            try {
               if(result.getId() != Integer.parseInt((String)args.get("trader"))) {
                  result = null;
               }
            } catch (Exception var10) {
               result = null;
            }
         }

         if(result != null && !result.hasTrait(TraderTrait.class)) {
            result = null;
         }
      }

      if(result == null) {
         locale.sendMessage(sender, "error-npc-invalid", new Object[0]);
      } else {
         Trader tr;
         if(!tNpcManager.instance().inRelation(sender)) {
            tr = (Trader)tNpcManager.create_tNpc(result, ((TraderTrait)result.getTrait(TraderTrait.class)).getType(), sender, TraderTrait.class);
            tr.parseStatus(TEntityStatus.MANAGE_SELL);
            tNpcManager.instance().registerRelation(sender, tr);
            locale.sendMessage(sender, "trader-managermode-enabled", new Object[]{"npc", result.getName()});
         } else {
            tr = tNpcManager.instance().getTraderRelation(sender);
            tNpcManager.instance().removeRelation(sender);
            sender.closeInventory();
            locale.sendMessage(sender, "trader-managermode-disabled", new Object[]{"npc", tr.getNPC().getName()});
            if(tr.getNPC().getId() != result.getId()) {
               Trader nTrader = (Trader)tNpcManager.create_tNpc(result, ((TraderTrait)result.getTrait(TraderTrait.class)).getType(), sender, TraderTrait.class);
               tNpcManager.instance().registerRelation(sender, nTrader);
               locale.sendMessage(sender, "trader-managermode-enabled", new Object[]{"npc", result.getName()});
            }
         }

      }
   }

   @Command(
      name = "trader",
      syntax = "give {args}",
      perm = "dtl.trader.commands.give",
      usage = "",
      desc = "",
      npc = false
   )
   public void traderGive(DtlTraders plugin, Player sender, Trader trader, Map args) {
      if(args.get("free") != null) {
         try {
            StockItem item = new StockItem((String)args.get("free"));
            sender.getInventory().setItemInMainHand(item.getItem(true));
         } catch (NumberFormatException var6) {
            ;
         }
      }

   }

   @Command(
      name = "trader",
      syntax = "open <trader>",
      perm = "dtl.trader.commands.open",
      usage = "",
      desc = "",
      npc = true
   )
   public void traderOpen(DtlTraders plugin, Player sender, Trader trader, Map args) throws TraderTypeNotFoundException, InvalidTraderTypeException {
      Iterator it = CitizensAPI.getNPCRegistry().iterator();
      Pattern pat = Pattern.compile("&.");
      NPC result = null;

      while(it.hasNext() && result == null) {
         if(!pat.matcher((result = (NPC)it.next()).getName()).replaceAll("").equals(args.get("trader"))) {
            try {
               if(result.getId() != Integer.parseInt((String)args.get("trader"))) {
                  result = null;
               }
            } catch (Exception var10) {
               result = null;
            }
         }

         if(result != null && !result.hasTrait(TraderTrait.class)) {
            result = null;
         }
      }

      if(result == null) {
         locale.sendMessage(sender, "error-npc-invalid", new Object[0]);
      } else {
         if(!tNpcManager.instance().inRelation(sender)) {
            Trader settings = tNpcManager.instance().getTraderRelation(sender);
            tNpcManager.instance().removeRelation(sender);
            sender.closeInventory();
         }

         if(!tNpcManager.instance().inRelation(sender)) {
            TraderTrait settings1 = (TraderTrait)result.getTrait(TraderTrait.class);
            Trader nTrader = (Trader)tNpcManager.create_tNpc(result, settings1.getType(), sender, TraderTrait.class);
            nTrader.parseStatus(TEntityStatus.baseStatus(settings1.getSettings().getStockStart()));
            tNpcManager.instance().registerRelation(sender, nTrader);
         }

      }
   }

}
