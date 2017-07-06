package net.dandielo.core.items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.dandielo.core.items.serialize.ItemAttribute;
import net.dandielo.core.items.serialize.ItemFlag;
import net.dandielo.core.items.serialize.core.Amount;
import net.dandielo.core.items.serialize.core.Banner;
import net.dandielo.core.items.serialize.core.Book;
import net.dandielo.core.items.serialize.core.Durability;
import net.dandielo.core.items.serialize.core.LeatherColor;
import net.dandielo.core.items.serialize.core.Name;
import net.dandielo.core.items.serialize.core.Skull;
import net.dandielo.core.items.serialize.core.SpawnEgg;
import net.dandielo.core.items.serialize.core.StoredEnchant;
import net.dandielo.core.items.serialize.flags.Lore;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

public class dItem {

   private Material material;
   private MaterialData materialData;
   protected Set flags = new HashSet();
   protected Set attributes = new HashSet();
   private String name;
   private HashMap enchantments = new HashMap();


   public dItem() {}

   public dItem(ItemStack item) {
      this.material = item.getType();
      this.materialData = item.getData();
      this.refactor(item);
   }

   public dItem(String data) {
      this.deserialize(data);
   }

   public dItem(String data, List lore) {
      this.deserialize(data);
      if(this.hasFlag(Lore.class)) {
         ((Lore)this.getFlag(Lore.class, false)).setLore(lore);
      }

   }

   public String serialize() {
      String result = this.material.name().toLowerCase();
      if(this.material.getMaxDurability() == 0 && this.materialData.getData() != 0) {
         result = result + ":" + this.materialData.getData();
      }

      Iterator var2;
      ItemAttribute flag;
      for(var2 = this.attributes.iterator(); var2.hasNext(); result = result + " " + flag.toString()) {
         flag = (ItemAttribute)var2.next();
      }

      var2 = this.flags.iterator();

      while(var2.hasNext()) {
         ItemFlag flag1 = (ItemFlag)var2.next();
         if(flag1 != null && flag1.getKey() != null) {
            result = result + " " + flag1.getKey();
         }
      }

      return result;
   }

   public void deserialize(String data) {
      String[] itemData = data.split(" ", 2);
      String[] itemMaterial = itemData[0].split(":");
      this.clearItem();
      this.material = Material.getMaterial(itemMaterial[0].toUpperCase());
      if(itemMaterial.length > 1) {
         this.materialData = new MaterialData(this.material, Byte.parseByte(itemMaterial[1]));
      } else {
         this.materialData = new MaterialData(this.material);
      }

      if(itemData.length != 1) {
         String ITEM_PATTERN = "(([^ :]+):([^ :]+))|([^ :]*)";
         Matcher matcher = Pattern.compile("(([^ :]+):([^ :]+))|([^ :]*)").matcher(itemData[1]);
         String key = "";
         String value = "";

         while(matcher.find()) {
            if(matcher.group(2) != null) {
               if(key.startsWith(".")) {
                  this.addFlag(key);
               } else if(!key.isEmpty() && value != null) {
                  this.addAttribute(key, value.trim());
               }

               key = matcher.group(2);
               value = matcher.group(3);
            } else if(matcher.group(4) != null) {
               if(matcher.group(4).startsWith(".")) {
                  if(key.startsWith(".")) {
                     this.addFlag(key);
                  } else if(!key.isEmpty() && value != null) {
                     this.addAttribute(key, value.trim());
                  }

                  key = matcher.group(4);
                  value = "";
               } else if(!matcher.group(4).isEmpty()) {
                  value = value + " " + matcher.group(4);
               }
            }
         }

         if(key.startsWith(".")) {
            this.addFlag(key);
         } else if(!key.isEmpty() && value != null) {
            this.addAttribute(key, value.trim());
         }

      }
   }

   public void refactor(ItemStack item) {
      if(item.getType().equals(Material.MONSTER_EGG)) {
         SpawnEgg lore = new SpawnEgg(this, "egg");
         lore.onRefactor(item);
         this.attributes.add(lore);
      }

      if(item.getType().equals(Material.LEATHER_BOOTS) || item.getType().equals(Material.LEATHER_HELMET)) {
         LeatherColor lore1 = new LeatherColor(this, "lc");
         lore1.onRefactor(item);
         this.attributes.add(lore1);
      }

      Iterator lore2 = ItemAttribute.initAllAttributes(this).iterator();

      while(lore2.hasNext()) {
         ItemAttribute iFlag = (ItemAttribute)lore2.next();
         if(iFlag.onRefactor(item)) {
            this.attributes.add(iFlag);
         }
      }

      lore2 = ItemFlag.getAllFlags(this).iterator();

      while(lore2.hasNext()) {
         ItemFlag iFlag1 = (ItemFlag)lore2.next();
         if(iFlag1.onRefactor(item)) {
            this.flags.add(iFlag1);
         }
      }

      Lore lore3 = (Lore)ItemFlag.init(this, ".lore");
      if(lore3.onRefactor(item)) {
         this.flags.add(lore3);
      }

   }

   public ItemStack getItem() {
      return this.getItem(false);
   }

   public ItemStack getItem(boolean abstrac) {
      return this.getItem(abstrac, (List)null);
   }

   public ItemStack getItem(boolean abstrac, List lore) {
      ItemStack resultItem = this.materialData.toItemStack();
      System.out.print("**");
      System.out.print(resultItem);
      if(lore != null) {
         ItemMeta firstPass = resultItem.getItemMeta();
         firstPass.setLore(lore);
         resultItem.setItemMeta(firstPass);
         System.out.print("**");
         System.out.print(resultItem);
      } else if(this.hasFlag(Lore.class)) {
         ((Lore)this.getFlag(Lore.class, false)).onAssign(resultItem, abstrac);
         System.out.print("%%");
         System.out.print(resultItem);
      }

      System.out.print("**");
      System.out.print(resultItem);
      ArrayList firstPass1 = new ArrayList();
      ArrayList secondPass = new ArrayList();
      Iterator var6 = this.attributes.iterator();

      ItemAttribute flag;
      while(var6.hasNext()) {
         flag = (ItemAttribute)var6.next();
         if(!(flag instanceof Name) && !(flag instanceof Skull) && !(flag instanceof StoredEnchant) && !(flag instanceof Book) && !(flag instanceof Banner)) {
            secondPass.add(flag);
         } else {
            firstPass1.add(flag);
         }
      }

      System.out.print("**");
      System.out.print(resultItem);

      for(var6 = firstPass1.iterator(); var6.hasNext(); resultItem = flag.onNativeAssign(resultItem, abstrac)) {
         flag = (ItemAttribute)var6.next();
      }

      for(var6 = secondPass.iterator(); var6.hasNext(); resultItem = flag.onNativeAssign(resultItem, abstrac)) {
         flag = (ItemAttribute)var6.next();
      }

      System.out.print("**");
      System.out.print(resultItem);
      var6 = this.flags.iterator();

      while(var6.hasNext()) {
         ItemFlag flag1 = (ItemFlag)var6.next();
         if(flag1 != null && !flag1.getKey().equals(".lore")) {
            resultItem = flag1.onNativeAssign(resultItem, abstrac);
         }
      }

      System.out.print("**");
      System.out.print(resultItem);
      return resultItem;
   }

   public int getAmount() {
      return ((Amount)this.getAttribute(Amount.class, true)).getAmount();
   }

   public void setAmount(int amount) {
      ((Amount)this.getAttribute(Amount.class, true)).setAmount(amount);
   }

   public Material getMaterial() {
      return this.material;
   }

   public MaterialData getMaterialData() {
      return this.materialData;
   }

   public void setMaterial(Material material) {
      this.material = material;
      this.materialData = new MaterialData(material);
   }

   public void setMaterialData(MaterialData materialData) {
      this.material = materialData.getItemType();
      this.materialData = materialData;
   }

   public int getTypeId() {
      return this.material.getId();
   }

   public int getTypeData() {
      return this.materialData.getData();
   }

   public String getName() {
      return this.hasAttribute(Name.class)?((Name)this.getAttribute(Name.class, false)).getValue():this.material.name().toLowerCase();
   }

   public void setName(String name) {
      ((Name)this.getAttribute(Name.class, true)).setValue(name);
   }

   public List getLore() {
      return (List)(this.hasFlag(Lore.class)?((Lore)this.getFlag(Lore.class, false)).getLore():new ArrayList());
   }

   public List getDescription() {
      ArrayList result = new ArrayList();
      Iterator var2 = this.attributes.iterator();

      while(var2.hasNext()) {
         ItemAttribute flag = (ItemAttribute)var2.next();
         flag.getDescription(result);
      }

      var2 = this.flags.iterator();

      while(var2.hasNext()) {
         ItemFlag flag1 = (ItemFlag)var2.next();
         if(!(flag1 instanceof Lore)) {
            flag1.getDescription(result);
         }
      }

      result.addAll(this.getLore());
      return result;
   }

   public int getDurability() {
      return this.hasAttribute(Durability.class)?((Durability)this.getAttribute(Durability.class, false)).getValue():0;
   }

   public ItemAttribute addAttribute(Class clazz) {
      ItemAttribute attribute = ItemAttribute.init(this, clazz);
      this.attributes.remove(attribute);
      this.attributes.add(attribute);
      return attribute;
   }

   public void addAttribute(String key, String value) {
      ItemAttribute attribute = ItemAttribute.init(this, key, value);
      if(attribute != null) {
         this.attributes.remove(attribute);
         this.attributes.add(attribute);
      }

   }

   public ItemAttribute getAttribute(Class clazz, boolean create) {
      ItemAttribute result = null;
      Iterator it = this.attributes.iterator();

      while(it.hasNext() && result == null) {
         result = (ItemAttribute)it.next();
         if(result.getSubkey() != null) {
            result = null;
         } else if(!clazz.isInstance(result)) {
            result = null;
         }
      }

      if(create && result == null) {
         result = ItemAttribute.init(this, clazz);
         this.attributes.add(result);
      }

      return result;
   }

   public ItemAttribute getAttribute(String key) {
      ItemAttribute result = null;
      Iterator it = this.attributes.iterator();

      while(it.hasNext() && result == null) {
         result = (ItemAttribute)it.next();
         String iKey = result.getKey();
         String iSub = result.getSubkey();
         if(!key.equals(iKey) && (iSub == null || !key.equals(iKey + "." + iSub))) {
            result = null;
         }
      }

      return result;
   }

   public Set getAttributes(String gkey) {
      ItemAttribute temp = null;
      HashSet result = new HashSet();
      Iterator it = this.attributes.iterator();

      while(it.hasNext()) {
         temp = (ItemAttribute)it.next();
         if(temp.getKey().equals(gkey)) {
            result.add(temp);
         }
      }

      return result;
   }

   public boolean hasAttribute(Class clazz) {
      return this.getAttribute(clazz, false) != null;
   }

   public boolean hasAttribute(String key) {
      return this.getAttribute(key) != null;
   }

   public void removeAttribute(Class clazz) {
      this.attributes.remove(this.getAttribute(clazz, false));
   }

   public void removeAttribute(String key) {
      this.attributes.remove(this.getAttribute(key));
   }

   public void removeAttributes(String gkey) {
      Iterator var2 = this.getAttributes(gkey).iterator();

      while(var2.hasNext()) {
         ItemAttribute attribute = (ItemAttribute)var2.next();
         this.attributes.remove(attribute);
      }

   }

   public void addFlag(Class clazz) {
      this.flags.add(ItemFlag.init(this, clazz));
   }

   public void addFlag(String flag) {
      this.flags.add(ItemFlag.init(this, flag));
   }

   public boolean hasFlag(Class clazz) {
      return this.getFlag(clazz, false) != null;
   }

   public boolean hasFlag(String flag) {
      return this.getFlag(flag) != null;
   }

   public ItemFlag getFlag(Class clazz, boolean create) {
      ItemFlag result = null;
      Iterator it = this.flags.iterator();

      while(it.hasNext() && result == null) {
         result = (ItemFlag)it.next();
         if(!clazz.isInstance(result)) {
            result = null;
         }
      }

      if(create && result == null) {
         result = ItemFlag.init(this, clazz);
         this.flags.add(result);
      }

      return result;
   }

   public ItemFlag getFlag(String flag) {
      ItemFlag result = null;
      Iterator it = this.flags.iterator();

      while(it.hasNext() && result == null) {
         result = (ItemFlag)it.next();
         if(!result.getKey().equals(flag)) {
            result = null;
         }
      }

      return result;
   }

   public void removeFlag(Class clazz) {
      this.flags.remove(this.getFlag(clazz, false));
   }

   public void removeFlag(String flag) {
      this.flags.remove(this.getFlag(flag));
   }

   public void clearItem() {
      this.attributes.clear();
      this.flags.clear();
   }

   public boolean equals(Object that) {
      return that instanceof dItem && this.equals((dItem)that);
   }

   public boolean equals(dItem that) {
      boolean equals = true;
      equals = this.material.equals(that.getMaterial());
      equals &= this.material.getMaxDurability() == 0?this.materialData.equals(that.materialData):true;
      equals = equals?this.isMetadataMatching(that):equals;
      if(equals) {
         Iterator var3 = this.attributes.iterator();

         Iterator var5;
         while(var3.hasNext()) {
            ItemAttribute itemFlag = (ItemAttribute)var3.next();
            if(!equals) {
               break;
            }

            if(!itemFlag.getInfo().standalone()) {
               var5 = that.attributes.iterator();

               while(var5.hasNext()) {
                  ItemAttribute thatItemFlag = (ItemAttribute)var5.next();
                  if(itemFlag.getClass().equals(thatItemFlag.getClass())) {
                     equals &= itemFlag.same(thatItemFlag);
                  }
               }
            }
         }

         var3 = this.flags.iterator();

         while(var3.hasNext()) {
            ItemFlag itemFlag1 = (ItemFlag)var3.next();
            if(!equals) {
               break;
            }

            if(!itemFlag1.getInfo().standalone()) {
               var5 = that.flags.iterator();

               while(var5.hasNext()) {
                  ItemFlag thatItemFlag1 = (ItemFlag)var5.next();
                  if(itemFlag1.getClass().equals(thatItemFlag1.getClass())) {
                     equals &= itemFlag1.equals(thatItemFlag1);
                  }
               }
            }
         }
      }

      return equals;
   }

   public boolean similar(dItem that) {
      boolean equals = true;
      equals = this.material.equals(that.getMaterial()) && this.materialData.equals(that.materialData);
      return equals;
   }

   public final int priorityMatch(dItem that) {
      int priority = 0;
      if(this.material.getMaxDurability() == 0) {
         if(!this.material.equals(Material.AIR)) {
            priority += this.material.equals(that.material)?100:0;
            priority += this.materialData.equals(that.materialData)?130:0;
         } else {
            priority += this.getDurability() == that.getDurability()?120:0;
         }
      } else if(!this.material.equals(Material.AIR)) {
         priority += this.material.equals(that.material) && this.materialData.equals(that.materialData)?130:-2;
      }

      if(priority < 0) {
         return priority;
      } else {
         Iterator var3 = this.attributes.iterator();

         Iterator var5;
         while(var3.hasNext()) {
            ItemAttribute thisItemFlag = (ItemAttribute)var3.next();
            if(!thisItemFlag.getInfo().standalone()) {
               var5 = that.attributes.iterator();

               while(var5.hasNext()) {
                  ItemAttribute thatItemFlag = (ItemAttribute)var5.next();
                  if(thisItemFlag.getClass().equals(thatItemFlag.getClass()) && thisItemFlag.same(thatItemFlag)) {
                     priority += thisItemFlag.getInfo().priority();
                  }
               }
            }
         }

         var3 = this.flags.iterator();

         while(var3.hasNext()) {
            ItemFlag thisItemFlag1 = (ItemFlag)var3.next();
            if(!thisItemFlag1.getInfo().standalone()) {
               var5 = that.flags.iterator();

               while(var5.hasNext()) {
                  ItemFlag thatItemFlag1 = (ItemFlag)var5.next();
                  if(thisItemFlag1.getClass().equals(thatItemFlag1.getClass()) && thisItemFlag1.equals(thatItemFlag1)) {
                     priority += thisItemFlag1.getInfo().priority();
                  }
               }
            }
         }

         return priority;
      }
   }

   public String toString() {
      return this.serialize();
   }

   public int hashCode() {
      byte hash = 7;
      int hash1 = 73 * hash + (this.material != null?this.material.hashCode():0);
      hash1 = 73 * hash1 + (this.materialData != null?this.materialData.hashCode():0);
      hash1 = 73 * hash1 + (this.attributes != null?this.attributes.hashCode():0);
      hash1 = 73 * hash1 + (this.flags != null?this.flags.hashCode():0);
      return hash1;
   }

   private boolean isMetadataMatching(dItem item) {
      return this.checkAttributesMatching(item) && this.checkFlagsMatching(item);
   }

   private boolean checkAttributesMatching(dItem item) {
      boolean containsAll = true;

      Iterator var3;
      ItemAttribute key;
      for(var3 = item.attributes.iterator(); var3.hasNext(); containsAll = containsAll && !key.getInfo().standalone()?this.attributes.contains(key):containsAll) {
         key = (ItemAttribute)var3.next();
      }

      for(var3 = this.attributes.iterator(); var3.hasNext(); containsAll = containsAll && !key.getInfo().standalone()?item.attributes.contains(key):containsAll) {
         key = (ItemAttribute)var3.next();
      }

      return containsAll;
   }

   private boolean checkFlagsMatching(dItem item) {
      boolean containsAll = true;

      Iterator var3;
      ItemFlag key;
      for(var3 = item.flags.iterator(); var3.hasNext(); containsAll = containsAll && !key.getInfo().standalone()?this.flags.contains(key):containsAll) {
         key = (ItemFlag)var3.next();
      }

      for(var3 = this.flags.iterator(); var3.hasNext(); containsAll = containsAll && !key.getInfo().standalone()?item.flags.contains(key):containsAll) {
         key = (ItemFlag)var3.next();
      }

      return containsAll;
   }
}
