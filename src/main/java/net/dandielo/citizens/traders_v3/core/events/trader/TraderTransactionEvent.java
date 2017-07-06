package net.dandielo.citizens.traders_v3.core.events.trader;

import net.dandielo.citizens.traders_v3.core.events.TraderEvent;
import net.dandielo.citizens.traders_v3.traders.Trader;
import net.dandielo.citizens.traders_v3.traders.stock.StockItem;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class TraderTransactionEvent extends TraderEvent {

   private static final HandlerList handlers = new HandlerList();
   private StockItem item;
   private TraderTransactionEvent.TransactionResult result;
   private boolean saveToInv = true;


   public TraderTransactionEvent(Trader npc, Player player, StockItem item, TraderTransactionEvent.TransactionResult result) {
      super(npc, player);
      this.item = item;
      this.result = result;
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }

   public boolean isSaveToInv() {
      return this.saveToInv;
   }

   public void setSaveToInv(boolean saveToInv) {
      this.saveToInv = saveToInv;
   }

   public Player getCustomer() {
      return this.player;
   }

   public StockItem getItem() {
      return this.item;
   }

   public TraderTransactionEvent.TransactionResult getResult() {
      return this.result;
   }


   public static enum TransactionResult {

      SUCCESS_PLAYER_BUY("SUCCESS_PLAYER_BUY", 0),
      SUCCESS_PLAYER_SELL("SUCCESS_PLAYER_SELL", 1),
      LIMIT_REACHED("LIMIT_REACHED", 2),
      INVENTORY_FULL("INVENTORY_FULL", 3),
      PLAYER_LACKS_MONEY("PLAYER_LACKS_MONEY", 4),
      TRADER_LACKS_MONEY("TRADER_LACKS_MONEY", 5);
      // $FF: synthetic field
      private static final TraderTransactionEvent.TransactionResult[] $VALUES = new TraderTransactionEvent.TransactionResult[]{SUCCESS_PLAYER_BUY, SUCCESS_PLAYER_SELL, LIMIT_REACHED, INVENTORY_FULL, PLAYER_LACKS_MONEY, TRADER_LACKS_MONEY};


      private TransactionResult(String var1, int var2) {}

      public boolean success() {
         return this.equals(SUCCESS_PLAYER_BUY);
      }

      public boolean falied() {
         return !this.equals(SUCCESS_PLAYER_BUY);
      }

   }
}
