package net.dandielo.citizens.traders_v3.core.events.trader;

import net.dandielo.citizens.traders_v3.core.events.TraderEvent;
import net.dandielo.citizens.traders_v3.traders.Trader;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class TraderCreateEvent extends TraderEvent {

   private static final HandlerList handlers = new HandlerList();


   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }

   public TraderCreateEvent(Trader npc, Player player) {
      super(npc, player);
   }

}
