package net.dandielo.core.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.dandielo.core.utils.NBTReader;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagList;
import org.bukkit.inventory.ItemStack;

public class NBTItemStack extends NBTReader {

   private NBTReader displayCompound;


   public NBTItemStack(ItemStack item) {
      super(item);
   }

   private NBTReader getDisplayTag() {
      if(!this.hasKey("display") || this.displayCompound == null) {
         this.setTag("display", new NBTTagCompound());
         this.displayCompound = this.getTagReader("display");
      }

      return this.displayCompound;
   }

   public List getLore() {
      ArrayList result = new ArrayList();
      if(this.getDisplayTag().hasKeyOfType("Lore", NBTReader.NBTTagType.LIST)) {
         NBTReader lore = this.getDisplayTag().getListReader("Lore", NBTReader.NBTTagType.STRING);

         for(int i = 0; i < lore.getListSize(); ++i) {
            result.add(lore.getStringAt(i));
         }
      }

      return result;
   }

   public void setLore(List list) {
      if(!this.getDisplayTag().hasKeyOfType("Lore", NBTReader.NBTTagType.LIST)) {
         this.getDisplayTag().setTag("Lore", new NBTTagList());
      }

      NBTReader lore = this.getDisplayTag().getListReader("Lore", NBTReader.NBTTagType.STRING);
      Iterator var3 = list.iterator();

      while(var3.hasNext()) {
         String line = (String)var3.next();
         lore.addString(line);
      }

   }

   public String getName() {
      return this.getDisplayTag().getString("Name");
   }

   public void setName(String name) {
      this.getDisplayTag().setString("Name", name);
   }

   public byte getCount() {
      return this.getByte("Count");
   }

   public short getDamage() {
      return this.getShort("Damage");
   }

   public String getID() {
      return this.getString("id");
   }

   public boolean isUnbreakable() {
      return this.hasKey("Unbreakable")?this.getBoolean("Unbreakable"):false;
   }

   public void setUnbreakable(boolean value) {
      this.setBoolean("Unbreakable", value);
   }
}
