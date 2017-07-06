package net.dandielo.core.items.serialize.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.dandielo.core.items.dItem;
import net.dandielo.core.items.serialize.Attribute;
import net.dandielo.core.items.serialize.ItemAttribute;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

@Attribute(
   key = "bn",
   name = "Banner",
   priority = 5,
   items = {Material.BANNER}
)
public class Banner extends ItemAttribute {

   private List patterns = new ArrayList();


   public Banner(dItem item, String key) {
      super(item, key);
   }

   public String serialize() {
      String result = "unused";

      Pattern pattern;
      String colorString;
      for(Iterator var2 = this.patterns.iterator(); var2.hasNext(); result = result + "," + pattern.getPattern().getIdentifier() + "@" + colorString) {
         pattern = (Pattern)var2.next();
         Color rgb = pattern.getColor().getColor();
         colorString = rgb.getRed() + "." + rgb.getGreen() + "." + rgb.getBlue();
      }

      return result;
   }

   public boolean deserialize(String data) {
      String[] arrayData = data.split(",");
      String[] var3 = arrayData;
      int var4 = arrayData.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String strPattern = var3[var5];
         if(!strPattern.equals("unused")) {
            String[] patternData = strPattern.split("@");
            String[] colorData = patternData[1].split("\\.");
            Color color = Color.fromRGB(Integer.parseInt(colorData[0]), Integer.parseInt(colorData[1]), Integer.parseInt(colorData[2]));
            this.patterns.add(new Pattern(DyeColor.getByColor(color), PatternType.getByIdentifier(patternData[0].toLowerCase())));
         }
      }

      return true;
   }

   public boolean onRefactor(ItemStack item) {
      if(!(item.getItemMeta() instanceof BannerMeta)) {
         return false;
      } else {
         BannerMeta meta = (BannerMeta)item.getItemMeta();
         this.patterns.addAll(meta.getPatterns());
         return true;
      }
   }

   public void onAssign(ItemStack item, boolean unused) {
      if(item.getItemMeta() instanceof BannerMeta) {
         BannerMeta meta = (BannerMeta)item.getItemMeta();
         Iterator var4 = this.patterns.iterator();

         while(var4.hasNext()) {
            Pattern pattern = (Pattern)var4.next();
            meta.addPattern(pattern);
         }

         item.setItemMeta(meta);
      }

   }
}
