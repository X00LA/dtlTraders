package net.dandielo.citizens.traders_v3.bukkit;

import net.dandielo.citizens.traders_v3.bukkit.DtlTraders;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Perms {

   public static final Perms perms = new Perms();
   private Permission permission = null;


   private Perms() {
      this.initPerms();
   }

   private void initPerms() {
      RegisteredServiceProvider permissionProvider = DtlTraders.getInstance().getServer().getServicesManager().getRegistration(Permission.class);
      if(permissionProvider != null) {
         this.permission = (Permission)permissionProvider.getProvider();
         DtlTraders.info("Permission plugin: " + ChatColor.YELLOW + this.permission.getName());
      } else {
         DtlTraders.info("Permission plugin not found! Not all functions will be available");
      }

   }

   public boolean has(CommandSender sender, String perm) {
      return this.permission != null?this.permission.has(sender, perm):sender.hasPermission(perm);
   }

   public static boolean hasPerm(CommandSender sender, String perm) {
      return perms.has(sender, perm);
   }

}
