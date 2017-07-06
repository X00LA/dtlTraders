package net.dandielo.citizens.traders_v3.traders.transaction.participants;

import java.util.UUID;
import net.dandielo.citizens.traders_v3.traders.Trader;
import net.dandielo.citizens.traders_v3.traders.transaction.Participant;

public class TraderParticipant implements Participant {

   private Trader trader;


   public TraderParticipant(Trader trader) {
      this.trader = trader;
   }

   public Trader getTrader() {
      return this.trader;
   }

   public boolean isPlayer() {
      return false;
   }

   public UUID getUUID() {
      return this.trader.getNPC().getUniqueId();
   }

   public boolean check(double amount) {
      return this.trader.getWallet().check(this.trader, amount);
   }

   public boolean withdraw(double amount) {
      return this.trader.getWallet().withdraw(this.trader, amount);
   }

   public boolean deposit(double amount) {
      return this.trader.getWallet().deposit(this.trader, amount);
   }
}
