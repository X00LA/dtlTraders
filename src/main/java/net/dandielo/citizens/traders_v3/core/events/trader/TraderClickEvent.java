package net.dandielo.citizens.traders_v3.core.events.trader;

import net.dandielo.citizens.traders_v3.core.events.TraderEvent;
import net.dandielo.citizens.traders_v3.traders.Trader;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class TraderClickEvent extends TraderEvent {

   private static final HandlerList handlers = new HandlerList();
   protected boolean mmToggling;
   protected boolean leftClick;


   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }

   public TraderClickEvent(Trader npc, Player player, boolean mmToggling, boolean leftClick) {
      super(npc, player);
      this.mmToggling = mmToggling;
      this.leftClick = leftClick;
   }

   public boolean isLeftClick() {
      return this.leftClick;
   }

   public boolean isRightClick() {
      return !this.leftClick;
   }

   public boolean isManagerToggling() {
      return this.mmToggling;
   }

   public void setManagerToggling(boolean toggle) {
      this.mmToggling = toggle;
   }

}
