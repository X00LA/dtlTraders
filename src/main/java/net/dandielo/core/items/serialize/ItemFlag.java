package net.dandielo.core.items.serialize;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.dandielo.citizens.traders_v3.bukkit.DtlTraders;
import net.dandielo.core.items.dItem;
import net.dandielo.core.items.serialize.Attribute;
import net.dandielo.core.items.serialize.flags.Lore;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

public abstract class ItemFlag {

   protected final String key;
   protected dItem item;
   private static final Map flags = new HashMap();


   public ItemFlag(dItem item, String key) {
      this.item = item;
      this.key = key;
   }

   public ItemStack onNativeAssign(ItemStack item, boolean abstrac) {
      this.onAssign(item, abstrac);
      return item;
   }

   public void onAssign(ItemStack item, boolean abstrac) {}

   public boolean onRefactor(ItemStack item) {
      return false;
   }

   public void getDescription(List result) {}

   public boolean equals(ItemFlag that) {
      return this.key.equals(that.key);
   }

   public boolean similar(ItemFlag that) {
      return this.equals(that);
   }

   public Attribute getInfo() {
      return (Attribute)this.getClass().getAnnotation(Attribute.class);
   }

   public final String toString() {
      return this.key;
   }

   public String getKey() {
      return this.key;
   }

   public int hashCode() {
      return this.key.hashCode();
   }

   public final boolean equals(Object o) {
      return o instanceof ItemFlag && this.key.equals(((ItemFlag)o).key);
   }

   public static List getAllFlags(dItem item) {
      ArrayList result = new ArrayList();
      Iterator var2 = flags.entrySet().iterator();

      while(var2.hasNext()) {
         Entry flag = (Entry)var2.next();
         if(!((Class)flag.getValue()).equals(Lore.class)) {
            Attribute attrInfo = (Attribute)flag.getKey();
            if(attrInfo.required() || Arrays.binarySearch(attrInfo.items(), item.getMaterial()) >= 0 || attrInfo.items().length == 0 && !attrInfo.standalone()) {
               try {
                  ItemFlag iFlag = (ItemFlag)((Class)flag.getValue()).getConstructor(new Class[]{dItem.class, String.class}).newInstance(new Object[]{item, ((Attribute)flag.getKey()).key()});
                  result.add(iFlag);
               } catch (Exception var6) {
                  ;
               }
            }
         }
      }

      return result;
   }

   public static ItemFlag init(dItem item, String key) {
      Attribute attr = null;
      Iterator itemflag = flags.keySet().iterator();

      while(itemflag.hasNext()) {
         Attribute attrEntry = (Attribute)itemflag.next();
         if(attrEntry.key().equals(key)) {
            attr = attrEntry;
         }
      }

      try {
         ItemFlag itemflag1 = (ItemFlag)((Class)flags.get(attr)).getConstructor(new Class[]{dItem.class, String.class}).newInstance(new Object[]{item, key});
         return itemflag1;
      } catch (Exception var5) {
         return null;
      }
   }

   public static ItemFlag init(dItem item, Class clazz) {
      Attribute attr = (Attribute)clazz.getAnnotation(Attribute.class);

      try {
         ItemFlag itemflag = (ItemFlag)clazz.getConstructor(new Class[]{dItem.class, String.class}).newInstance(new Object[]{item, attr.key()});
         return itemflag;
      } catch (Exception var4) {
         return null;
      }
   }

   public static void registerCoreFlags() {
      try {
         registerFlag(Lore.class);
      } catch (Exception var1) {
         ;
      }

   }

   protected static String flagsAsString() {
      String result = "";

      Attribute attr;
      for(Iterator var1 = flags.keySet().iterator(); var1.hasNext(); result = result + ", " + ChatColor.YELLOW + attr.name() + ChatColor.RESET) {
         attr = (Attribute)var1.next();
      }

      return ChatColor.WHITE + "[" + result.substring(2) + ChatColor.WHITE + "]";
   }

   public static void registerFlag(Class clazz) {
      if(!clazz.isAnnotationPresent(Attribute.class)) {
         DtlTraders.warning("Couldnt register the following flag class: " + clazz.getSimpleName());
      } else {
         Attribute attr = (Attribute)clazz.getAnnotation(Attribute.class);
         flags.put(attr, clazz);
      }
   }

}
