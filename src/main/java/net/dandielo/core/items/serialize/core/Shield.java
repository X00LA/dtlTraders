package net.dandielo.core.items.serialize.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.dandielo.core.items.dItem;
import net.dandielo.core.items.serialize.Attribute;
import net.dandielo.core.items.serialize.ItemAttribute;
import net.dandielo.core.utils.NBTItemStack;
import net.dandielo.core.utils.NBTReader;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagList;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;

@Attribute(
   name = "Shield",
   key = "sh",
   priority = 5,
   items = {Material.SHIELD}
)
public class Shield extends ItemAttribute {

   private int base;
   private List patterns = new ArrayList();


   public Shield(dItem item, String key) {
      super(item, key);
   }

   public boolean onRefactor(ItemStack item) {
      NBTItemStack nItem = new NBTItemStack(item);
      if(!nItem.hasKey("BlockEntityTag")) {
         return false;
      } else {
         NBTReader beTag = nItem.getTagReader("BlockEntityTag");
         this.base = beTag.getInt("Base");
         NBTReader patterns = beTag.getListReader("Patterns", NBTReader.NBTTagType.COMPOUND);

         for(int i = 0; i < patterns.getListSize(); ++i) {
            NBTReader pattern = patterns.getReaderAt(i);
            this.patterns.add(new Pattern(DyeColor.getByColor(Color.fromRGB(pattern.getInt("Color"))), PatternType.getByIdentifier(pattern.getString("Pattern"))));
         }

         return true;
      }
   }

   public ItemStack onNativeAssign(ItemStack item, boolean abstrac) {
      NBTItemStack nItem = new NBTItemStack(item);
      if(this.base != 0 || !this.patterns.isEmpty()) {
         nItem.setTag("BlockEntityTag", new NBTTagCompound());
         NBTReader beTag = nItem.getTagReader("BlockEntityTag");
         beTag.setInt("Base", this.base);
         beTag.setTag("Patterns", new NBTTagList());
         NBTReader patterns = beTag.getListReader("Patterns", NBTReader.NBTTagType.COMPOUND);
         Iterator var6 = this.patterns.iterator();

         while(var6.hasNext()) {
            Pattern pat = (Pattern)var6.next();
            NBTReader pattern = new NBTReader(new NBTTagCompound());
            pattern.setString("Pattern", pat.getPattern().getIdentifier());
            pattern.setInt("Color", pat.getColor() == null?0:pat.getColor().getColor().asRGB());
            patterns.addTag(pattern.asCompoundTag());
         }
      }

      return nItem.getItemStack();
   }

   public String serialize() {
      String result = Integer.toString(this.base);

      Pattern pattern;
      String colorString;
      for(Iterator var2 = this.patterns.iterator(); var2.hasNext(); result = result + "," + pattern.getPattern().getIdentifier() + "@" + colorString) {
         pattern = (Pattern)var2.next();
         Color rgb = pattern.getColor() == null?Color.fromRGB(0):pattern.getColor().getColor();
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
         if(strPattern.contains(".")) {
            String[] patternData = strPattern.split("@");
            String[] colorData = patternData[1].split("\\.");
            Color color = Color.fromRGB(Integer.parseInt(colorData[0]), Integer.parseInt(colorData[1]), Integer.parseInt(colorData[2]));
            this.patterns.add(new Pattern(DyeColor.getByColor(color), PatternType.getByIdentifier(patternData[0].toLowerCase())));
         } else {
            this.base = Integer.valueOf(strPattern).intValue();
         }
      }

      return true;
   }
}
