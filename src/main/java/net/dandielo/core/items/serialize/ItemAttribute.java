package net.dandielo.core.items.serialize;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.dandielo.citizens.traders_v3.bukkit.DtlTraders;
import net.dandielo.core.items.dItem;
import net.dandielo.core.items.serialize.Attribute;
import org.bukkit.inventory.ItemStack;

public abstract class ItemAttribute {

   protected final dItem item;
   protected final String key;
   protected final String sub;
   private static Map attributeKeys = new HashMap();
   private static Map attributeClasses = new HashMap();


   protected ItemAttribute(dItem item, String key) {
      this(item, key, (String)null);
   }

   protected ItemAttribute(dItem item, String key, String sub) {
      this.item = item;
      this.key = key;
      this.sub = sub;
   }

   public void onAssign(ItemStack item, boolean abstrac) {}

   public boolean onRefactor(ItemStack item) {
      return false;
   }

   public ItemStack onNativeAssign(ItemStack item, boolean abstrac) {
      this.onAssign(item, abstrac);
      return item;
   }

   public void getDescription(List description) {}

   public String getKey() {
      return this.key;
   }

   public String getSubkey() {
      return this.sub;
   }

   public Attribute getInfo() {
      return (Attribute)this.getClass().getAnnotation(Attribute.class);
   }

   public final String toString() {
      return this.key + (this.sub != null?"." + this.sub:"") + ":" + this.serialize();
   }

   public abstract String serialize();

   public abstract boolean deserialize(String var1);

   public boolean same(ItemAttribute that) {
      return this.key.equals(that.key);
   }

   public boolean similar(ItemAttribute that) {
      return this.same(that);
   }

   public final boolean equals(Object that) {
      boolean var10000;
      if(that instanceof ItemAttribute && this.key.equals(((ItemAttribute)that).key)) {
         label27: {
            if(this.sub == null) {
               if(((ItemAttribute)that).sub != null) {
                  break label27;
               }
            } else if(!this.sub.equals(((ItemAttribute)that).sub)) {
               break label27;
            }

            var10000 = true;
            return var10000;
         }
      }

      var10000 = false;
      return var10000;
   }

   public int hashCode() {
      return (this.key + (this.sub == null?"":"." + this.sub)).hashCode();
   }

   public static Set getRequiredAttributes() {
      HashSet result = new HashSet();
      Iterator var1 = attributeClasses.entrySet().iterator();

      while(var1.hasNext()) {
         Entry attr = (Entry)var1.next();

         try {
            if(((Attribute)attr.getKey()).required()) {
               ItemAttribute attrInstance = (ItemAttribute)((Class)attr.getValue()).getConstructor(new Class[]{dItem.class, String.class}).newInstance(new Object[]{((Attribute)attr.getKey()).key()});
               result.add(attrInstance);
            }
         } catch (Exception var4) {
            ;
         }
      }

      return result;
   }

   public static List initAllAttributes(dItem item) {
      ArrayList result = new ArrayList();
      Iterator var2 = attributeClasses.entrySet().iterator();

      while(var2.hasNext()) {
         Entry attributeEntry = (Entry)var2.next();
         Attribute attrInfo = (Attribute)attributeEntry.getKey();
         if(attrInfo.required() || Arrays.binarySearch(attrInfo.items(), item.getMaterial()) >= 0 || attrInfo.items().length == 0 && !attrInfo.standalone()) {
            try {
               try {
                  Constructor constr = ((Class)attributeEntry.getValue()).getConstructor(new Class[]{dItem.class, String.class});
                  ItemAttribute itemAttribute = (ItemAttribute)constr.newInstance(new Object[]{item, attrInfo.key()});
                  result.add(itemAttribute);
               } catch (NoSuchMethodException var10) {
                  ;
               }

               String[] var12 = attrInfo.sub();
               int var13 = var12.length;

               for(int var7 = 0; var7 < var13; ++var7) {
                  String sub = var12[var7];
                  ItemAttribute iAttr = (ItemAttribute)((Class)attributeEntry.getValue()).getConstructor(new Class[]{dItem.class, String.class, String.class}).newInstance(new Object[]{item, attrInfo.key(), sub});
                  result.add(iAttr);
               }
            } catch (Exception var11) {
               ;
            }
         }
      }

      return result;
   }

   public static ItemAttribute init(dItem item, Class clazz) {
      Attribute aInfo = (Attribute)clazz.getAnnotation(Attribute.class);
      ItemAttribute result = null;
      if(aInfo != null) {
         try {
            result = (ItemAttribute)clazz.getConstructor(new Class[]{dItem.class, String.class}).newInstance(new Object[]{item, aInfo.key()});
         } catch (Exception var5) {
            ;
         }
      }

      return result;
   }

   public static ItemAttribute init(dItem item, String key, String value) {
      Attribute aInfo = (Attribute)attributeKeys.get(key);
      ItemAttribute result = null;
      Class clazz;
      if(aInfo != null && (clazz = (Class)attributeClasses.get(aInfo)) != null) {
         String[] keyPair = key.split("\\.");
         if(keyPair.length == 1) {
            try {
               result = (ItemAttribute)clazz.getConstructor(new Class[]{dItem.class, String.class}).newInstance(new Object[]{item, keyPair[0]});
            } catch (Exception var9) {
               ;
            }
         } else {
            try {
               result = (ItemAttribute)clazz.getConstructor(new Class[]{dItem.class, String.class, String.class}).newInstance(new Object[]{item, keyPair[0], keyPair[1]});
            } catch (Exception var8) {
               ;
            }
         }

         if(result != null && !result.deserialize(value)) {
            result = null;
         }
      }

      return result;
   }

   public static void registerAttr(Class clazz) {
      if(!clazz.isAnnotationPresent(Attribute.class)) {
         DtlTraders.warning("Couldnt register the following attribute class: " + clazz.getSimpleName());
      } else {
         Attribute attr = (Attribute)clazz.getAnnotation(Attribute.class);
         attributeClasses.put(attr, clazz);
         attributeKeys.put(attr.key(), attr);
         String[] var2 = attr.sub();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            String sub = var2[var4];
            attributeKeys.put(attr.key() + "." + sub, attr);
         }

      }
   }

   public static void extendAttrKey(String key, Class clazz) {
      if(!clazz.isAnnotationPresent(Attribute.class)) {
         DtlTraders.warning("Couldnt register the following attribute class: " + clazz.getSimpleName());
      } else {
         Attribute attr = (Attribute)clazz.getAnnotation(Attribute.class);
         attributeClasses.put(attr, clazz);
         String[] var3 = attr.sub();
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            String sub = var3[var5];
            attributeKeys.put(key + "." + sub, attr);
         }

      }
   }

}
