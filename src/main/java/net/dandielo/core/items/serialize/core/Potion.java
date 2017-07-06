package net.dandielo.core.items.serialize.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.dandielo.core.items.dItem;
import net.dandielo.core.items.serialize.Attribute;
import net.dandielo.core.items.serialize.ItemAttribute;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

@Attribute(
   name = "Potion",
   key = "pt",
   priority = 5,
   items = {Material.POTION, Material.SPLASH_POTION, Material.TIPPED_ARROW, Material.LINGERING_POTION}
)
public class Potion extends ItemAttribute {

   private List effects = new ArrayList();
   private boolean extended;
   private boolean upgraded;
   private PotionType type;


   public Potion(dItem item, String key) {
      super(item, key);
   }

   public boolean deserialize(String data) {
      String[] potionData = data.split("@");
      String[] baseData = potionData[0].split("\\.");
      this.type = PotionType.valueOf(baseData[0]);

      for(int savedEffects = 1; savedEffects < baseData.length; ++savedEffects) {
         if(baseData[savedEffects].equals("ext")) {
            this.extended = true;
         } else if(baseData[savedEffects].equals("upg")) {
            this.upgraded = true;
         }
      }

      if(potionData.length > 1 && potionData[1] != null && potionData[1].length() > 0) {
         String[] var11 = potionData[1].split(",");
         String[] var5 = var11;
         int var6 = var11.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            String savedEffect = var5[var7];
            String[] effectData = savedEffect.split("/");
            PotionEffect effect = new PotionEffect(PotionEffectType.getByName(effectData[0]), Integer.parseInt(effectData[1]), Integer.parseInt(effectData[2]), Boolean.parseBoolean(effectData[3]));
            this.effects.add(effect);
         }
      }

      return true;
   }

   public String serialize() {
      String result = this.type.toString();
      if(this.extended) {
         result = result + ".ext";
      }

      if(this.upgraded) {
         result = result + ".upg";
      }

      result = result + "@";

      PotionEffect e;
      for(Iterator var2 = this.effects.iterator(); var2.hasNext(); result = result + e.getType().getName() + "/" + e.getDuration() + "/" + e.getAmplifier() + "/" + e.isAmbient()) {
         e = (PotionEffect)var2.next();
      }

      return result;
   }

   public void onAssign(ItemStack item, boolean unused) {
      if(item.getType().equals(Material.POTION) || item.getType().equals(Material.SPLASH_POTION) || item.getType().equals(Material.LINGERING_POTION) || item.getType().equals(Material.TIPPED_ARROW)) {
         PotionMeta meta = (PotionMeta)item.getItemMeta();
         if(!this.effects.isEmpty()) {
            Iterator var4 = this.effects.iterator();

            while(var4.hasNext()) {
               PotionEffect effect = (PotionEffect)var4.next();
               meta.addCustomEffect(effect, false);
            }
         }

         meta.setBasePotionData(new PotionData(this.type, this.extended, this.upgraded));
         item.setItemMeta(meta);
      }
   }

   public boolean onRefactor(ItemStack item) {
      if(!item.getType().equals(Material.POTION) && !item.getType().equals(Material.SPLASH_POTION) && !item.getType().equals(Material.LINGERING_POTION) && !item.getType().equals(Material.TIPPED_ARROW)) {
         return false;
      } else {
         PotionMeta meta = (PotionMeta)item.getItemMeta();
         this.effects = meta.getCustomEffects();
         PotionData data = meta.getBasePotionData();
         this.extended = data.isExtended();
         this.upgraded = data.isUpgraded();
         this.type = data.getType();
         return true;
      }
   }

   public boolean same(ItemAttribute attr) {
      if(!this.type.equals(((Potion)attr).type)) {
         return false;
      } else if(this.extended != ((Potion)attr).extended) {
         return false;
      } else if(this.upgraded != ((Potion)attr).upgraded) {
         return false;
      } else if(((Potion)attr).effects.size() != this.effects.size()) {
         return false;
      } else {
         boolean equals = true;

         PotionEffect effect;
         for(Iterator var3 = ((Potion)attr).effects.iterator(); var3.hasNext(); equals = equals?this.effects.contains(effect):equals) {
            effect = (PotionEffect)var3.next();
         }

         return equals;
      }
   }

   public boolean similar(ItemAttribute attr) {
      return this.same(attr);
   }
}
