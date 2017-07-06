package net.dandielo.core.items.serialize.core;

import java.util.HashMap;
import net.dandielo.core.items.dItem;
import net.dandielo.core.items.serialize.Attribute;
import net.dandielo.core.items.serialize.ItemAttribute;
import net.dandielo.core.utils.NBTItemStack;
import net.dandielo.core.utils.NBTReader;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

@Attribute(
   name = "Spawn egg",
   key = "egg",
   priority = 5,
   items = {Material.MONSTER_EGG}
)
public class SpawnEgg extends ItemAttribute {

   private EntityType entityType = null;
   private static HashMap nameMappings = new HashMap();
   private static HashMap typeMappings;


   public SpawnEgg(dItem item, String key) {
      super(item, key);
   }

   public boolean onRefactor(ItemStack item) {
      NBTItemStack nItem = new NBTItemStack(item);
      if(nItem.hasKey("EntityTag")) {
         NBTReader tag = nItem.getTagReader("EntityTag");
         if(tag.hasKey("id")) {
            String id = tag.getString("id").replace("minecraft:", "").toLowerCase();

            try {
               if(nameMappings.containsKey(id)) {
                  this.entityType = (EntityType)nameMappings.get(id);
               } else {
                  this.entityType = EntityType.valueOf(id.toUpperCase());
               }
            } catch (Exception var6) {
               ;
            }
         }
      }

      return this.entityType != null;
   }

   public ItemStack onNativeAssign(ItemStack item, boolean abstrac) {
      NBTItemStack nItem = new NBTItemStack(item);
      if(this.entityType != null) {
         NBTReader tag = new NBTReader(new NBTTagCompound());
         if(this.entityType == EntityType.EVOKER) {
            tag.setString("id", "evocation_illager");
         } else if(this.entityType == EntityType.VINDICATOR) {
            tag.setString("id", "vindication_illager");
         } else if(this.entityType == EntityType.PIG_ZOMBIE) {
            tag.setString("id", "zombie_pigman");
         } else {
            String id = (String)typeMappings.get(this.entityType);
            if(id == null) {
               id = WordUtils.capitalize(this.entityType.name().toLowerCase());
            }

            tag.setString("id", id);
         }

         nItem.setTag("EntityTag", tag.asCompoundTag());
      }

      return nItem.getItemStack();
   }

   public String serialize() {
      return this.entityType == null?"unknown":this.entityType.name().toLowerCase();
   }

   public boolean deserialize(String data) {
      this.entityType = EntityType.valueOf(data.toUpperCase());
      return this.entityType != null;
   }

   public boolean same(ItemAttribute attr) {
      return this.entityType.equals(((SpawnEgg)attr).entityType);
   }

   public boolean similar(ItemAttribute attr) {
      return this.same(attr);
   }

   static {
      nameMappings.put("ozelot", EntityType.OCELOT);
      nameMappings.put("entityhorse", EntityType.HORSE);
      nameMappings.put("zombie_pigman", EntityType.PIG_ZOMBIE);
      nameMappings.put("pigzombie", EntityType.PIG_ZOMBIE);
      nameMappings.put("cavespider", EntityType.CAVE_SPIDER);
      nameMappings.put("lavaslime", EntityType.MAGMA_CUBE);
      nameMappings.put("mooshroom", EntityType.MUSHROOM_COW);
      nameMappings.put("mushroomcow", EntityType.MUSHROOM_COW);
      nameMappings.put("vindication_illager", EntityType.VINDICATOR);
      nameMappings.put("vindicator", EntityType.VINDICATOR);
      nameMappings.put("evocation_illager", EntityType.EVOKER);
      nameMappings.put("evoker", EntityType.EVOKER);
      typeMappings = new HashMap();
      typeMappings.put(EntityType.OCELOT, "Ozelot");
      typeMappings.put(EntityType.HORSE, "EntityHorse");
      typeMappings.put(EntityType.PIG_ZOMBIE, "PigZombie");
      typeMappings.put(EntityType.CAVE_SPIDER, "CaveSpider");
      typeMappings.put(EntityType.MAGMA_CUBE, "LavaSlime");
      typeMappings.put(EntityType.MUSHROOM_COW, "MushroomCow");
      typeMappings.put(EntityType.VINDICATOR, "Vindicator");
      typeMappings.put(EntityType.EVOKER, "Evoker");
   }
}
