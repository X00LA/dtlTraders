package net.dandielo.citizens.traders_v3.traders.setting;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Owner;
import net.citizensnpcs.api.util.DataKey;
import net.dandielo.citizens.traders_v3.core.dB;
import net.dandielo.citizens.traders_v3.traders.setting.GlobalSettings;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class Settings extends GlobalSettings {

   private final NPC npc;
   private OfflinePlayer owner = null;
   private String type = "server";
   private int stockSize;
   private String stockNameFormat;
   private String stockStart;
   private List patterns;


   public Settings(NPC npc) {
      this.stockSize = GlobalSettings.stockSize;
      this.stockNameFormat = GlobalSettings.stockNameFormat;
      this.stockStart = GlobalSettings.stockStart;
      this.patterns = new ArrayList();
      this.npc = npc;
   }

   public NPC getNPC() {
      return this.npc;
   }

   public String getType() {
      return this.type;
   }

   public void setType(String type) {
      this.type = type;
   }

   public OfflinePlayer getOwner() {
      return this.owner;
   }

   public void setOwner(OfflinePlayer owner) {
      this.owner = owner;
   }

   public String getNpcOwner() {
      return ((Owner)this.npc.getTrait(Owner.class)).getOwner();
   }

   public int getStockSize() {
      return this.stockSize;
   }

   public void setStockSize(int size) {
      this.stockSize = size;
   }

   public String getStockName() {
      return this.stockNameFormat.replace("{npc}", this.npc.getName()).replace('&', '\u00a7').replace('^', '\u00a7');
   }

   public String getStockFormat() {
      return this.stockNameFormat;
   }

   public void setStockFormat(String format) {
      this.stockNameFormat = format;
   }

   public String getStockStart() {
      return this.stockStart;
   }

   public void setStockStart(String stock) {
      this.stockStart = stock;
   }

   public String getManagerStockStart() {
      return mmStockStart;
   }

   public void load(DataKey data) {
      dB.info(new Object[]{"Loading trader settings for: ", this.npc.getName()});
      this.type = data.getString("type");
      String ownerUUID = data.getString("owner-uuid", "none");
      this.owner = ownerUUID.equals("none")?null:Bukkit.getOfflinePlayer(UUID.fromString(ownerUUID));
      if(this.type.equals("trader")) {
         this.type = data.getString("trader");
      }

      this.stockSize = data.getInt("stock.size", GlobalSettings.stockSize);
      this.stockNameFormat = data.getString("stock.format", GlobalSettings.stockNameFormat);
      this.stockStart = data.getString("stock.default", GlobalSettings.stockStart);
      if(data.getRaw("patterns") != null) {
         this.patterns.addAll((List)data.getRaw("patterns"));
      }

   }

   public void save(DataKey data) {
      dB.info(new Object[]{"Saving trader settings for:", this.npc.getName()});
      data.setString("type", this.type);
      data.setString("owner-uuid", this.owner == null?"none":this.owner.getUniqueId().toString());
      data.setRaw("stock", (Object)null);
      data.setRaw("patterns", this.patterns);
      if(this.stockSize != GlobalSettings.stockSize) {
         data.setInt("stock.size", this.stockSize);
      }

      if(!this.stockNameFormat.equals(GlobalSettings.stockNameFormat)) {
         data.setString("stock.format", this.stockNameFormat);
      }

      if(!this.stockStart.equals(GlobalSettings.stockStart)) {
         data.setString("stock.default", this.stockStart);
      }

   }

   public List getPatterns() {
      return this.patterns;
   }
}
