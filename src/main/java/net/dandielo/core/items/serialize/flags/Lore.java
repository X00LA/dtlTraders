package net.dandielo.core.items.serialize.flags;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.dandielo.core.bukkit.NBTUtils;
import net.dandielo.core.items.dItem;
import net.dandielo.core.items.serialize.Attribute;
import net.dandielo.core.items.serialize.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@Attribute(
   name = "Lore",
   key = ".lore"
)
public class Lore extends ItemFlag {

   public static final String dCoreLorePrefix = "§3§d§d§f";
   private List lore = new ArrayList();


   public Lore(dItem item, String key) {
      super(item, key);
   }

   public void setLore(List lore) {
      this.lore = new ArrayList(lore.size());
      Iterator var2 = lore.iterator();

      while(var2.hasNext()) {
         String unescaped = (String)var2.next();
         this.lore.add(escape(unescaped));
      }

   }

   public List getRawLore() {
      return this.lore;
   }

   public static String escape(String lore) {
      return lore.replace('\u00a7', '^');
   }

   public static String unescape(String lore) {
      return lore.replace('^', '\u00a7').replace('&', '\u00a7');
   }

   public void onAssign(ItemStack item, boolean unused) {
      Object itemLore = item.getItemMeta().getLore();
      if(itemLore == null) {
         itemLore = new ArrayList();
      }

      Iterator meta = this.lore.iterator();

      while(meta.hasNext()) {
         String lore = (String)meta.next();
         ((List)itemLore).add(unescape(lore));
      }

      ItemMeta meta1 = item.getItemMeta();
      meta1.setLore((List)itemLore);
      item.setItemMeta(meta1);
   }

   public boolean onRefactor(ItemStack item) {
      if(!item.getItemMeta().hasLore()) {
         return false;
      } else {
         List cleanedLore = cleanLore(item.getItemMeta().getLore());
         if(cleanedLore.isEmpty()) {
            return false;
         } else {
            this.setLore(cleanedLore);
            return true;
         }
      }
   }

   public List getLore() {
      ArrayList itemLore = new ArrayList();
      Iterator var2 = this.lore.iterator();

      while(var2.hasNext()) {
         String lore = (String)var2.next();
         itemLore.add(unescape(lore));
      }

      return itemLore;
   }

   public int hashCode() {
      return 0;
   }

   public boolean equals(ItemFlag o) {
      Lore itemLore = (Lore)o;
      if((itemLore.lore != null || this.lore != null) && (itemLore.lore == null || this.lore == null)) {
         return false;
      } else if(itemLore.lore.size() != this.lore.size()) {
         return false;
      } else {
         boolean equals = true;

         for(int i = 0; i < itemLore.lore.size() && equals; ++i) {
            equals = ((String)itemLore.lore.get(i)).equals(this.lore.get(i));
         }

         return equals;
      }
   }

   public boolean similar(ItemFlag flag) {
      return this.equals(flag);
   }

   public static List cleanLore(List lore) {
      ArrayList cleaned = new ArrayList();
      Iterator var2 = lore.iterator();

      while(var2.hasNext()) {
         String entry = (String)var2.next();
         if(!entry.startsWith("§3§d§d§f")) {
            cleaned.add(entry);
         }
      }

      return cleaned;
   }

   public static ItemStack addLore(ItemStack item, List lore) {
      ItemStack newItem = NBTUtils.addLore(item, lore);
      if(newItem != null) {
         return newItem;
      } else {
         ItemMeta meta = item.getItemMeta();
         Object newLore = meta.getLore();
         if(newLore == null) {
            newLore = new ArrayList();
         }

         ((List)newLore).addAll(lore);
         meta.setLore((List)newLore);
         newItem = item.clone();
         newItem.setItemMeta(meta);
         return newItem;
      }
   }

   public static boolean hasTraderLore(ItemStack item) {
      if(item.hasItemMeta() && item.getItemMeta().hasLore()) {
         boolean has = false;
         Iterator var2 = item.getItemMeta().getLore().iterator();

         while(var2.hasNext()) {
            String entry = (String)var2.next();
            if(!has && entry.startsWith("§3§d§d§f")) {
               has = true;
            }
         }

         return has;
      } else {
         return false;
      }
   }
}
