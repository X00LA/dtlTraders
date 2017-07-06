package net.dandielo.core.items.serialize.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import net.dandielo.core.items.dItem;
import net.dandielo.core.items.serialize.Attribute;
import net.dandielo.core.items.serialize.ItemAttribute;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

@Attribute(
   name = "StoredEnchants",
   key = "se",
   priority = 5,
   items = {Material.ENCHANTED_BOOK}
)
public class StoredEnchant extends ItemAttribute {

   private java.util.Map enchants = new HashMap();


   public StoredEnchant(dItem item, String key) {
      super(item, key);
   }

   public boolean deserialize(String data) {
      String[] var2 = data.split(",");
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String enchantment = var2[var4];
         String[] enchData = enchantment.split("/");
         Enchantment ench = Enchantment.getByName(enchData[0].toUpperCase());
         if(ench == null) {
            ench = Enchantment.getById(Integer.parseInt(enchData[0]));
         }

         try {
            this.enchants.put(ench, Integer.valueOf(Integer.parseInt(enchData[1])));
         } catch (NumberFormatException var9) {
            return false;
         }
      }

      return true;
   }

   public String serialize() {
      String result = "";

      Entry enchant;
      for(Iterator var2 = this.enchants.entrySet().iterator(); var2.hasNext(); result = result + "," + ((Enchantment)enchant.getKey()).getName().toLowerCase() + "/" + enchant.getValue()) {
         enchant = (Entry)var2.next();
      }

      return result.substring(1);
   }

   public void onAssign(ItemStack item, boolean unused) {
      if(item.getType().equals(Material.ENCHANTED_BOOK)) {
         EnchantmentStorageMeta meta = (EnchantmentStorageMeta)item.getItemMeta();
         Iterator var4 = this.enchants.entrySet().iterator();

         while(var4.hasNext()) {
            Entry enchant = (Entry)var4.next();
            meta.addStoredEnchant((Enchantment)enchant.getKey(), ((Integer)enchant.getValue()).intValue(), true);
         }

         item.setItemMeta(meta);
      }
   }

   public boolean onRefactor(ItemStack item) {
      if(!item.getType().equals(Material.ENCHANTED_BOOK)) {
         return false;
      } else if(!((EnchantmentStorageMeta)item.getItemMeta()).hasStoredEnchants()) {
         return false;
      } else {
         Iterator var2 = ((EnchantmentStorageMeta)item.getItemMeta()).getStoredEnchants().entrySet().iterator();

         while(var2.hasNext()) {
            Entry enchant = (Entry)var2.next();
            this.enchants.put(enchant.getKey(), enchant.getValue());
         }

         return true;
      }
   }

   public boolean same(ItemAttribute data) {
      if(((StoredEnchant)data).enchants.size() != this.enchants.size()) {
         return false;
      } else {
         boolean equals = true;
         Iterator var3 = ((StoredEnchant)data).enchants.entrySet().iterator();

         while(var3.hasNext()) {
            Entry enchant = (Entry)var3.next();
            if(equals && this.enchants.get(enchant.getKey()) != null) {
               equals = this.enchants.get(enchant.getKey()) == enchant.getValue();
            } else {
               equals = false;
            }
         }

         return equals;
      }
   }

   public boolean similar(ItemAttribute data) {
      return this.same(data);
   }
}
