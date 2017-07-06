package net.dandielo.core.items.serialize.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import net.dandielo.core.items.dItem;
import net.dandielo.core.items.serialize.Attribute;
import net.dandielo.core.items.serialize.ItemAttribute;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

@Attribute(
   name = "Firework",
   key = "fw",
   priority = 5,
   items = {Material.FIREWORK}
)
public class Firework extends ItemAttribute {

   private static Builder effectBuilder = FireworkEffect.builder();
   private List effects = new ArrayList();


   public Firework(dItem item, String key) {
      super(item, key);
   }

   public boolean deserialize(String data) {
      String[] values = data.split("/");
      String[] var3 = values;
      int var4 = values.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String effectData = var3[var5];
         List fwe = Arrays.asList(effectData.split("\\."));
         effectBuilder.trail(fwe.contains("trail"));
         effectBuilder.flicker(fwe.contains("flicker"));
         int i = 0;
         Iterator var9 = fwe.iterator();

         while(var9.hasNext()) {
            String line = (String)var9.next();
            if(line.contains("^")) {
               ArrayList colors = new ArrayList();
               String[] var12 = line.split("-");
               int var13 = var12.length;

               for(int var14 = 0; var14 < var13; ++var14) {
                  String colorData = var12[var14];
                  String[] RGBdata = colorData.split("\\^");
                  colors.add(Color.fromRGB(Integer.parseInt(RGBdata[0]), Integer.parseInt(RGBdata[1]), Integer.parseInt(RGBdata[2])));
               }

               if(i++ == 0) {
                  effectBuilder.withColor(colors);
               } else {
                  effectBuilder.withFade(colors);
               }
            } else if(!line.isEmpty() && !line.equals("flicker") && !line.equals("trail")) {
               effectBuilder.with(Type.valueOf(line.toUpperCase()));
            }
         }

         this.effects.add(effectBuilder.build());
      }

      return true;
   }

   public String serialize() {
      String result = "";
      int i = 0;
      Iterator var3 = this.effects.iterator();

      while(var3.hasNext()) {
         FireworkEffect effect = (FireworkEffect)var3.next();
         int j = 0;
         Iterator var6 = effect.serialize().entrySet().iterator();

         while(var6.hasNext()) {
            Entry entry = (Entry)var6.next();
            if(entry.getValue() instanceof List) {
               int z = 0;
               List tempList = (List)entry.getValue();
               Iterator var10 = tempList.iterator();

               while(var10.hasNext()) {
                  Color color = (Color)var10.next();
                  result = result + color.getRed() + "^" + color.getGreen() + "^" + color.getBlue();
                  if(z++ + 1 < tempList.size()) {
                     result = result + "-";
                  }
               }
            } else if(entry.getValue() instanceof Boolean) {
               if(!((Boolean)entry.getValue()).booleanValue()) {
                  continue;
               }

               result = result + (String)entry.getKey();
            } else {
               result = result + entry.getValue();
            }

            if(j++ + 1 < effect.serialize().size()) {
               result = result + ".";
            }
         }

         if(i++ + 1 < this.effects.size()) {
            result = result + "/";
         }
      }

      return result;
   }

   public void onAssign(ItemStack item, boolean unused) {
      if(item.getItemMeta() instanceof FireworkMeta) {
         FireworkMeta firework = (FireworkMeta)item.getItemMeta();
         firework.addEffects(this.effects);
         item.setItemMeta(firework);
      }
   }

   public boolean onRefactor(ItemStack item) {
      if(!(item.getItemMeta() instanceof FireworkMeta)) {
         return false;
      } else {
         FireworkMeta firework = (FireworkMeta)item.getItemMeta();
         if(!firework.hasEffects()) {
            return false;
         } else {
            this.effects.addAll(firework.getEffects());
            return true;
         }
      }
   }

   public boolean same(ItemAttribute that) {
      if(this.effects.size() != ((Firework)that).effects.size()) {
         return false;
      } else {
         boolean equals = true;

         FireworkEffect effect;
         for(Iterator var3 = ((Firework)that).effects.iterator(); var3.hasNext(); equals = equals?this.effects.contains(effect):equals) {
            effect = (FireworkEffect)var3.next();
         }

         return equals;
      }
   }

   public boolean similar(ItemAttribute that) {
      return this.same(that);
   }

}
