package net.dandielo.core.items.serialize.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.dandielo.core.bukkit.NBTUtils;
import net.dandielo.core.items.dItem;
import net.dandielo.core.items.serialize.Attribute;
import net.dandielo.core.items.serialize.ItemAttribute;
import org.bukkit.inventory.ItemStack;

@Attribute(
   name = "GenericDamage",
   key = "g",
   sub = {"dmg"},
   priority = 5
)
public class GenericDamage extends ItemAttribute {

   private static String ATTRIBUTE = "generic.attackDamage";
   private List modifiers = new ArrayList();


   public GenericDamage(dItem item, String key, String sub) {
      super(item, key, sub);
   }

   public boolean deserialize(String data) {
      String[] mods = data.split(";");
      String[] var3 = mods;
      int var4 = mods.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String mod = var3[var5];
         this.modifiers.add(new NBTUtils.Modifier(mod.split("/")));
      }

      return true;
   }

   public String serialize() {
      String result = "";

      NBTUtils.Modifier mod;
      for(Iterator var2 = this.modifiers.iterator(); var2.hasNext(); result = result + ";" + mod.toString()) {
         mod = (NBTUtils.Modifier)var2.next();
      }

      return result.substring(1);
   }

   public boolean onRefactor(ItemStack item) {
      List mods = NBTUtils.getModifiers(item, ATTRIBUTE);
      if(mods != null && !mods.isEmpty()) {
         this.modifiers.addAll(mods);
         return true;
      } else {
         return false;
      }
   }

   public ItemStack onNativeAssign(ItemStack item, boolean endItem) {
      NBTUtils.Modifier mod;
      for(Iterator var3 = this.modifiers.iterator(); var3.hasNext(); item = NBTUtils.setModifier(item, mod.getName(), ATTRIBUTE, mod.getValue(), mod.getOperation())) {
         mod = (NBTUtils.Modifier)var3.next();
      }

      return item;
   }

}
