package net.dandielo.citizens.traders_v3.bukkit.commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.dandielo.citizens.traders_v3.bukkit.DtlTraders;
import net.dandielo.citizens.traders_v3.core.PluginSettings;
import net.dandielo.citizens.traders_v3.core.dB;
import net.dandielo.citizens.traders_v3.core.commands.Command;
import net.dandielo.citizens.traders_v3.core.locale.LocaleManager;
import net.dandielo.citizens.traders_v3.traders.Trader;
import net.dandielo.citizens.traders_v3.traders.patterns.PatternManager;
import net.dandielo.citizens.traders_v3.traders.setting.GlobalSettings;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class GeneralCommands {

   private static LocaleManager locale = LocaleManager.locale;
   private static List commands = new ArrayList();


   @Command(
      name = "trader",
      syntax = "",
      perm = "dtl.trader.commands",
      desc = "shows plugin version, and trader statistics if any is selected",
      npc = false
   )
   public void trader(DtlTraders plugin, CommandSender sender, Trader npc, Map args) {
      locale.sendMessage(sender, "plugin-command-message", new Object[]{"version", plugin.getDescription().getVersion(), "name", plugin.getName()});
      if(npc != null) {
         locale.sendMessage(sender, "key-value", new Object[]{"key", "#type", "value", "#trader-" + npc.getSettings().getType()});
         locale.sendMessage(sender, "key-value", new Object[]{"key", "#owner", "value", npc.getSettings().getOwner() == null?"":npc.getSettings().getOwner()});
         locale.sendMessage(sender, "key-value", new Object[]{"key", "#stock-name", "value", npc.getSettings().getStockName()});
         locale.sendMessage(sender, "key-value", new Object[]{"key", "#stock-start", "value", npc.getSettings().getStockStart()});
         locale.sendMessage(sender, "key-value", new Object[]{"key", "#stock-size", "value", String.valueOf(npc.getSettings().getStockSize())});
      }

   }

   @Command(
      name = "trader",
      syntax = "reload",
      perm = "dtl.trader.commands.reload",
      desc = "reloads the locale and all global settings",
      npc = false
   )
   public void traderReload(DtlTraders plugin, CommandSender sender, Trader npc, Map args) {
      plugin.reloadConfig();
      PluginSettings.initPluginSettings();
      GlobalSettings.initGlobalSettings();
      PatternManager.instance.reload();
      locale.load();
      locale.sendMessage(sender, "plugin-reload", new Object[0]);
   }

   public static void registerCommandInfo(Command command) {
      Object list = commands;
      if(list == null) {
         list = new ArrayList();
      }

      ((List)list).add(command);
   }

   @Command(
      name = "trader",
      syntax = "help",
      desc = "allows to get information about all trader commands",
      perm = "dtl.trader.commands.help",
      npc = false
   )
   public void traderHelpBasic(DtlTraders plugin, CommandSender sender, Trader npc, Map args) {
      List cmds = commands;
      if(cmds == null) {
         dB.high(new Object[]{"Command informations are not loaded"});
      }

      sender.sendMessage(ChatColor.GOLD + "== " + ChatColor.YELLOW + "Trader commands" + ChatColor.GOLD + " ==");
      sender.sendMessage("");
      Iterator var6 = cmds.iterator();

      while(var6.hasNext()) {
         Command cmd = (Command)var6.next();
         sender.sendMessage(nameAndSyntax(cmd));
      }

   }

   private static String nameAndSyntax(Command cmd) {
      return ChatColor.GOLD + "Command: " + ChatColor.YELLOW + cmd.name() + " " + cmd.syntax();
   }

}
