package net.dandielo.citizens.traders_v3.core.commands;

import net.citizensnpcs.Citizens;
import net.citizensnpcs.api.CitizensAPI;
import net.dandielo.citizens.traders_v3.TradingEntity;
import net.dandielo.citizens.traders_v3.tNpcManager;
import net.dandielo.citizens.traders_v3.core.commands.CommandManager;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DtlCommandExecutor implements CommandExecutor {

   public static CommandManager cManager;
   public static Citizens citizens;
   private static tNpcManager manager = tNpcManager.instance();


   public DtlCommandExecutor(CommandManager manager) {
      cManager = manager;
      citizens = (Citizens)CitizensAPI.getPlugin();
   }

   public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String name, String[] args) {
      if(sender instanceof Player) {
         TradingEntity npc = manager.getRelation(sender.getName(), TradingEntity.class);
         return cManager.execute(name, args, sender, npc);
      } else {
         return true;
      }
   }

}
