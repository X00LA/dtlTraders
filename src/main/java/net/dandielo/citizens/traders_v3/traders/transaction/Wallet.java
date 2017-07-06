package net.dandielo.citizens.traders_v3.traders.transaction;

import net.dandielo.citizens.traders_v3.bukkit.Econ;
import net.dandielo.citizens.traders_v3.core.dB;
import net.dandielo.citizens.traders_v3.traders.Trader;
import org.bukkit.OfflinePlayer;

public class Wallet {

   private OfflinePlayer player = null;
   private double money;
   private Wallet.Type type;


   public Wallet(String type, double money) {
      this.money = money;
      this.type = Wallet.Type.fromString(type);
   }

   public void setType(String type) {
      this.type = Wallet.Type.fromString(type);
   }

   public void setMoney(double money) {
      this.money = money;
   }

   public double getMoney() {
      return this.money;
   }

   public void setPlayer(OfflinePlayer player) {
      this.player = player;
   }

   public OfflinePlayer getPlayer() {
      return this.player;
   }

   public boolean check(Trader trader, double amount) {
      dB.info(new Object[]{"Withdraw money, from: trader, name: ", trader.getSettings().getNPC().getName()});
      dB.info(new Object[]{"Amount: ", Double.valueOf(amount), ", wallet: ", this.type.name().toLowerCase(), ", balance: ", Double.valueOf(this.money)});
      return this.type.equals(Wallet.Type.INFINITE)?true:(this.type.equals(Wallet.Type.PRIVATE)?(this.money - amount >= 0.0D?(this.money -= amount) >= 0.0D:false):(this.type.equals(Wallet.Type.OWNER)?Econ.econ.withdraw(trader.getSettings().getOwner().getUniqueId(), amount):(this.player != null?Econ.econ.withdraw(this.player.getUniqueId(), amount):false)));
   }

   public boolean deposit(Trader trader, double amount) {
      dB.info(new Object[]{"Deposit money, to: trader, name: ", trader.getSettings().getNPC().getName()});
      dB.info(new Object[]{"Amount: ", Double.valueOf(amount), ", wallet: ", this.type.name().toLowerCase()});
      if(this.type.equals(Wallet.Type.PRIVATE)) {
         this.money += amount;
      } else {
         if(this.type.equals(Wallet.Type.OWNER)) {
            return Econ.econ.deposit(trader.getSettings().getOwner().getUniqueId(), amount);
         }

         if(this.type.equals(Wallet.Type.PLAYER)) {
            if(this.player != null) {
               return Econ.econ.deposit(this.player.getUniqueId(), amount);
            }

            return false;
         }
      }

      return true;
   }

   public boolean withdraw(Trader trader, double amount) {
      dB.info(new Object[]{"Withdraw money, from: trader, name: ", trader.getSettings().getNPC().getName()});
      dB.info(new Object[]{"Amount: ", Double.valueOf(amount), ", wallet: ", this.type.name().toLowerCase(), ", balance: ", Double.valueOf(this.money)});
      return this.type.equals(Wallet.Type.INFINITE)?true:(this.type.equals(Wallet.Type.PRIVATE)?(this.money - amount >= 0.0D?(this.money -= amount) >= 0.0D:false):(this.type.equals(Wallet.Type.OWNER)?Econ.econ.withdraw(trader.getSettings().getOwner().getUniqueId(), amount):(this.player != null?Econ.econ.withdraw(this.player.getUniqueId(), amount):false)));
   }

   static enum Type {

      INFINITE("INFINITE", 0),
      OWNER("OWNER", 1),
      PRIVATE("PRIVATE", 2),
      PLAYER("PLAYER", 3);
      // $FF: synthetic field
      private static final Wallet.Type[] $VALUES = new Wallet.Type[]{INFINITE, OWNER, PRIVATE, PLAYER};


      private Type(String var1, int var2) {}

      public String toString() {
         return this.name().toLowerCase();
      }

      public static Wallet.Type fromString(String type) {
         return valueOf(type.toUpperCase());
      }

   }
}
