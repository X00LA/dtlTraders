package net.dandielo.citizens.traders_v3.traders.transaction.participants;

import java.util.UUID;
import net.dandielo.citizens.traders_v3.bukkit.Econ;
import net.dandielo.citizens.traders_v3.traders.transaction.Participant;
import org.bukkit.entity.Player;

public class PlayerParticipant implements Participant {

   private static Econ econ = Econ.econ;
   private Player player;


   public PlayerParticipant(Player player) {
      this.player = player;
   }

   public Player getPlayer() {
      return this.player;
   }

   public boolean isPlayer() {
      return true;
   }

   public UUID getUUID() {
      return this.player.getUniqueId();
   }

   public boolean check(double amount) {
      return econ.check(this.getUUID(), amount);
   }

   public boolean withdraw(double amount) {
      return econ.withdraw(this.getUUID(), amount);
   }

   public boolean deposit(double amount) {
      return econ.deposit(this.getUUID(), amount);
   }

}
