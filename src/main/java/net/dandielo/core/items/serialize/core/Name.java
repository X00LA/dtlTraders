package net.dandielo.core.items.serialize.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.dandielo.core.items.dItem;
import net.dandielo.core.items.serialize.Attribute;
import net.dandielo.core.items.serialize.ItemAttribute;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@Attribute(
   key = "n",
   name = "name",
   priority = 300
)
public class Name extends ItemAttribute {

   private String name;


   public Name(dItem item, String key) {
      super(item, key);
   }

   public String getValue() {
      return this.name;
   }

   public void setValue(String name) {
      this.name = name;
   }

   public String serialize() {
      return this.name == null?null:this.name.replace('\u00a7', '&');
   }

   public boolean deserialize(String data) {
      if(data == null) {
         return false;
      } else {
         this.name = data.replace('&', '\u00a7');
         return true;
      }
   }

   public boolean onRefactor(ItemStack item) {
      ItemMeta meta = item.getItemMeta();
      if(!meta.hasDisplayName()) {
         return false;
      } else {
         this.name = meta.getDisplayName();
         return true;
      }
   }

   public void onAssign(ItemStack item, boolean abstrac) {
      if(this.name == null) {
         ItemMeta meta = item.getItemMeta();
         meta.setDisplayName(this.name);
         item.setItemMeta(meta);
      }

   }

   public boolean extendedCheck(ItemAttribute attr) {
      Matcher match = Pattern.compile(this.name).matcher(((Name)attr).name);
      return match.matches();
   }

   public boolean similar(ItemAttribute attr) {
      return this.same(attr);
   }

   public boolean same(ItemAttribute attr) {
      return this.name.equals(((Name)attr).name);
   }
}
