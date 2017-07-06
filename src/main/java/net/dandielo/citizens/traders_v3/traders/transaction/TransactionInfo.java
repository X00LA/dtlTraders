package net.dandielo.citizens.traders_v3.traders.transaction;

import net.dandielo.citizens.traders_v3.traders.stock.StockItem;
import net.dandielo.citizens.traders_v3.traders.transaction.Participant;
import net.dandielo.citizens.traders_v3.traders.transaction.participants.PlayerParticipant;
import net.dandielo.citizens.traders_v3.utils.items.flags.StackPrice;
import org.bukkit.entity.Player;

public class TransactionInfo {

   private Participant seller;
   private Participant buyer;
   private StockItem item;
   TransactionInfo.Stock stock;
   private int amount;
   private double multiplier;


   public TransactionInfo(String stock, StockItem item, int amount) {
      this.stock = TransactionInfo.Stock.valueOf(stock.toUpperCase());
      this.item = item;
      this.amount = amount;
   }

   private void setSeller(Participant seller) {
      this.seller = seller;
   }

   private void setBuyer(Participant buyer) {
      this.buyer = buyer;
   }

   TransactionInfo setMultiplier(double multiplier) {
      this.multiplier = multiplier;
      return this;
   }

   TransactionInfo setParticipants(Participant player, Participant trader) {
      if(this.stock.equals(TransactionInfo.Stock.SELL)) {
         this.setBuyer(player);
         this.setSeller(trader);
      } else {
         this.setSeller(player);
         this.setBuyer(trader);
      }

      return this;
   }

   public boolean isStackprice() {
      return this.item.hasFlag(StackPrice.class);
   }

   public int getAmount() {
      return this.isStackprice()?this.amount / this.item.getAmount():this.amount;
   }

   public double getMultiplier() {
      return this.multiplier;
   }

   public TransactionInfo.Stock getStock() {
      return this.stock;
   }

   public Participant getBuyer() {
      return this.buyer;
   }

   public Participant getSeller() {
      return this.seller;
   }

   public double getTotalScaling() {
      return this.multiplier * (double)this.getAmount();
   }

   public Player getPlayerParticipant() {
      return this.buyer instanceof PlayerParticipant?((PlayerParticipant)this.buyer).getPlayer():((PlayerParticipant)this.seller).getPlayer();
   }

   public static enum Stock {

      SELL("SELL", 0),
      BUY("BUY", 1);
      // $FF: synthetic field
      private static final TransactionInfo.Stock[] $VALUES = new TransactionInfo.Stock[]{SELL, BUY};


      private Stock(String var1, int var2) {}

   }
}
