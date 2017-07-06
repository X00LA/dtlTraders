package net.dandielo.citizens.traders_v3.traits;

import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.util.DataKey;
import net.dandielo.citizens.traders_v3.core.dB;
import net.dandielo.citizens.traders_v3.traders.setting.Settings;
import net.dandielo.citizens.traders_v3.traders.stock.Stock;
import net.dandielo.citizens.traders_v3.traders.stock.StockTrader;

public class TraderTrait extends Trait {

   private Settings settings;
   private Stock stock;


   public TraderTrait() {
      super("trader");
      this.settings = new Settings(this.npc);
   }

   public Settings getSettings() {
      return this.settings;
   }

   public String getType() {
      return this.settings.getType();
   }

   public Stock getStock() {
      return this.stock;
   }

   public void onRemove() {}

   public void onAttach() {
      dB.info(new Object[]{"Trader trait attached to:", this.npc.getName()});
      this.settings = new Settings(this.npc);
      this.stock = new StockTrader(this.settings);
   }

   public void load(DataKey data) {
      this.settings.load(data);
      this.stock.load(data);
   }

   public void save(DataKey data) {
      this.settings.save(data);
      this.stock.save(data);
   }
}
