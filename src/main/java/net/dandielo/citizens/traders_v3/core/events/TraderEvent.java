package net.dandielo.citizens.traders_v3.core.events;

import net.dandielo.citizens.traders_v3.core.events.tEvent;
import net.dandielo.citizens.traders_v3.traders.Trader;
import org.bukkit.entity.Player;

public abstract class TraderEvent extends tEvent {

   protected TraderEvent(Trader npc, Player player) {
      super(npc, player);
   }

   public Trader getTrader() {
      return (Trader)this.npc;
   }
}
