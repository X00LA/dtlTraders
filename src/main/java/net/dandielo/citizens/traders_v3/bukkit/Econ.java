package net.dandielo.citizens.traders_v3.bukkit;

import java.util.UUID;
import net.dandielo.citizens.traders_v3.bukkit.DtlTraders;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Econ {

   public static final Econ econ = new Econ();
   private Economy economy;
   private boolean enabled = false;


   private Econ() {
      this.init();
   }

   private void init() {
      RegisteredServiceProvider rspEcon = DtlTraders.getInstance().getServer().getServicesManager().getRegistration(Economy.class);
      if(rspEcon != null) {
         this.economy = (Economy)rspEcon.getProvider();
         DtlTraders.info("Economy plugin: " + ChatColor.YELLOW + this.economy.getName());
         this.enabled = true;
      } else {
         DtlTraders.info("Economy plugin not found! Disabling plugin");
      }

   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public boolean check(UUID uid, double amount) {
      return this.economy.getBalance(Bukkit.getOfflinePlayer(uid)) >= amount;
   }

   public boolean deposit(UUID uid, double amount) {
      return this.economy.depositPlayer(Bukkit.getOfflinePlayer(uid), amount).transactionSuccess();
   }

   public boolean withdraw(UUID uid, double amount) {
      return this.economy.withdrawPlayer(Bukkit.getOfflinePlayer(uid), amount).transactionSuccess();
   }

}
