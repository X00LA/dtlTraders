package net.dandielo.citizens.traders_v3.traits;

import java.util.UUID;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.util.DataKey;
import net.dandielo.citizens.traders_v3.core.dB;
import net.dandielo.citizens.traders_v3.traders.transaction.Wallet;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class WalletTrait extends Trait {

   @Persist
   String type = "infinite";
   @Persist
   double money = 0.0D;
   private String playerUUID;
   private OfflinePlayer player;
   private Wallet wallet;


   public WalletTrait() {
      super("wallet");
   }

   public void setPlayer(OfflinePlayer player2) {
      this.player = player2;
      this.wallet.setPlayer(player2);
   }

   public void setType(String type) {
      this.type = type;
      this.wallet.setType(type);
   }

   public void setMoney(double money) {
      this.money = money;
      this.wallet.setMoney(money);
   }

   public double getBalance() {
      return this.wallet.getMoney();
   }

   public OfflinePlayer getPlayer() {
      return this.player;
   }

   public String getType() {
      return this.type;
   }

   public Wallet getWallet() {
      return this.wallet;
   }

   public void onAttach() {
      dB.info(new Object[]{"Wallet trait attached to: ", this.npc.getName()});
      this.wallet = new Wallet(this.type, this.money);
      this.wallet.setPlayer(this.player);
   }

   public void load(DataKey data) {
      this.playerUUID = data.getString("player-uuid", "none");
      this.player = this.playerUUID.equals("none")?null:Bukkit.getOfflinePlayer(UUID.fromString(this.playerUUID));
      this.wallet = new Wallet(this.type, this.money);
      this.wallet.setPlayer(this.player);
   }

   public void save(DataKey data) {
      this.playerUUID = this.player == null?"none":this.player.getUniqueId().toString();
      data.setString("player-uuid", this.playerUUID);
      this.money = this.wallet.getMoney();
   }
}
