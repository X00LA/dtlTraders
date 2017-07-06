package net.dandielo.citizens.traders_v3.core.events.trader;

import net.dandielo.citizens.traders_v3.core.events.TraderEvent;
import net.dandielo.citizens.traders_v3.traders.Trader;
import net.dandielo.citizens.traders_v3.traders.setting.Settings;
import net.dandielo.citizens.traders_v3.traders.stock.Stock;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class TraderPrepareEvent extends TraderEvent implements Cancellable {

   private static final HandlerList handlers = new HandlerList();
   boolean cancelled = false;


   public TraderPrepareEvent(Trader npc, Player player) {
      super(npc, player);
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }

   public Stock getStock() {
      return this.getTrader().getStock();
   }

   public Settings getSettings() {
      return this.getTrader().getSettings();
   }

   public boolean isCancelled() {
      return this.cancelled;
   }

   public void setCancelled(boolean val) {
      this.cancelled = val;
   }

}
