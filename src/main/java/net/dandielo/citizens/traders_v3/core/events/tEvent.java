package net.dandielo.citizens.traders_v3.core.events;

import net.dandielo.citizens.traders_v3.TradingEntity;
import net.dandielo.citizens.traders_v3.bukkit.DtlTraders;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public abstract class tEvent extends Event {

   protected final TradingEntity npc;
   protected final Player player;


   protected tEvent(TradingEntity npc, Player player) {
      this.npc = npc;
      this.player = player;
   }

   public Player getPlayer() {
      return this.player;
   }

   public tEvent callEvent() {
      DtlTraders.getInstance().getServer().getPluginManager().callEvent(this);
      return this;
   }
}
