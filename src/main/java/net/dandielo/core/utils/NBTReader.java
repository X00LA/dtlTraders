package net.dandielo.core.utils;

import net.minecraft.server.v1_12_R1.NBTBase;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagList;
import net.minecraft.server.v1_12_R1.NBTTagString;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public class NBTReader {

   private NBTReader.ObjectType objectType;
   private Object object;


   public NBTReader(ItemStack item) {
      this.objectType = NBTReader.ObjectType.Unknown;
      this.objectType = NBTReader.ObjectType.Item;
      this.object = CraftItemStack.asNMSCopy(item);
      if(this.asNativeItemStack().getTag() == null) {
         this.asNativeItemStack().setTag(new NBTTagCompound());
      }

   }

   public NBTReader(Entity entity) {
      this.objectType = NBTReader.ObjectType.Unknown;
      this.objectType = NBTReader.ObjectType.Entity;
      this.object = ((CraftEntity)entity).getHandle();
   }

   public NBTReader(NBTTagCompound nbtTag) {
      this.objectType = NBTReader.ObjectType.Unknown;
      this.objectType = NBTReader.ObjectType.NBTTag;
      this.object = nbtTag;
   }

   public NBTReader(NBTTagList nbtTagList) {
      this.objectType = NBTReader.ObjectType.Unknown;
      this.objectType = NBTReader.ObjectType.NBTList;
      this.object = nbtTagList;
   }

   public ItemStack getItemStack() {
      return this.objectType == NBTReader.ObjectType.Item?CraftItemStack.asCraftMirror(this.asNativeItemStack()):null;
   }

   public Entity getEntity() {
      return (Entity)this.object;
   }

   public NBTTagCompound asCompoundTag() {
      return this.objectType == NBTReader.ObjectType.NBTTag?(NBTTagCompound)this.object:null;
   }

   protected net.minecraft.server.v1_12_R1.ItemStack asNativeItemStack() {
      return this.object instanceof net.minecraft.server.v1_12_R1.ItemStack?(net.minecraft.server.v1_12_R1.ItemStack)this.object:null;
   }

   protected net.minecraft.server.v1_12_R1.Entity asNativeEntity() {
      return this.object instanceof net.minecraft.server.v1_12_R1.Entity?(net.minecraft.server.v1_12_R1.Entity)this.object:null;
   }

   protected NBTTagCompound asNBTTagCompound() {
      NBTTagCompound result = null;
      if(this.objectType == NBTReader.ObjectType.NBTTag) {
         result = (NBTTagCompound)this.object;
      }

      if(this.objectType == NBTReader.ObjectType.Item) {
         result = this.asNativeItemStack().getTag();
      }

      return result;
   }

   public NBTTagList asNBTTagList() {
      NBTTagList result = null;
      if(this.objectType == NBTReader.ObjectType.NBTList) {
         result = (NBTTagList)this.object;
      }

      return result;
   }

   public boolean isTagCompound() {
      return this.objectType == NBTReader.ObjectType.NBTTag;
   }

   public boolean hasKey(String subKey) {
      return this.asNBTTagCompound().hasKey(subKey);
   }

   public boolean hasKeyOfType(String subKey, NBTReader.NBTTagType type) {
      return this.asNBTTagCompound().hasKeyOfType(subKey, type.ID);
   }

   public String getString(String subKey) {
      return this.asNBTTagCompound().getString(subKey);
   }

   public boolean getBoolean(String subKey) {
      return this.asNBTTagCompound().getBoolean(subKey);
   }

   public double getDouble(String subKey) {
      return this.asNBTTagCompound().getDouble(subKey);
   }

   public float getFloat(String subKey) {
      return this.asNBTTagCompound().getFloat(subKey);
   }

   public long getLong(String subKey) {
      return this.asNBTTagCompound().getLong(subKey);
   }

   public int getInt(String subKey) {
      return this.asNBTTagCompound().getInt(subKey);
   }

   public short getShort(String subKey) {
      return this.asNBTTagCompound().getShort(subKey);
   }

   public byte getByte(String subKey) {
      return this.asNBTTagCompound().getByte(subKey);
   }

   public byte[] getByteArray(String subKey) {
      return this.asNBTTagCompound().getByteArray(subKey);
   }

   public int[] getIntArray(String subKey) {
      return this.asNBTTagCompound().getIntArray(subKey);
   }

   public void setTag(String subKey, NBTBase compound) {
      this.asNBTTagCompound().set(subKey, compound);
   }

   public void setString(String key, String value) {
      this.asNBTTagCompound().setString(key, value);
   }

   public void setByte(String key, byte value) {
      this.asNBTTagCompound().setByte(key, value);
   }

   public void setInt(String key, int value) {
      this.asNBTTagCompound().setInt(key, value);
   }

   public void setBoolean(String key, boolean value) {
      this.asNBTTagCompound().setBoolean(key, value);
   }

   public NBTReader getTagReader(String subKey) {
      return this.asNBTTagCompound().hasKeyOfType(subKey, NBTReader.NBTTagType.COMPOUND.ID)?new NBTReader(this.asNBTTagCompound().getCompound(subKey)):null;
   }

   public NBTReader getListReader(String subKey, NBTReader.NBTTagType type) {
      return this.asNBTTagCompound().hasKeyOfType(subKey, NBTReader.NBTTagType.LIST.ID)?new NBTReader(this.asNBTTagCompound().getList(subKey, type.ID)):null;
   }

   public boolean isTagList() {
      return this.objectType == NBTReader.ObjectType.NBTList;
   }

   public int getListSize() {
      return this.objectType == NBTReader.ObjectType.NBTList?this.asNBTTagList().size():-1;
   }

   public String getStringAt(int index) {
      return this.asNBTTagList().getString(index);
   }

   public void addString(String value) {
      this.asNBTTagList().add(new NBTTagString(value));
   }

   public void addTag(NBTBase base) {
      this.asNBTTagList().add(base);
   }

   public NBTReader getReaderAt(int index) {
      return this.objectType == NBTReader.ObjectType.NBTList?new NBTReader(this.asNBTTagList().get(index)):null;
   }

   public static NBTTagCompound buildTagTree(NBTTagCompound tag, String key) {
      String tkey;
      int end;
      for(tkey = key; tkey.contains("."); tkey = tkey.substring(end + 1)) {
         end = tkey.indexOf(".");
         String sub = tkey.substring(0, end);
         if(!tag.hasKeyOfType(sub, NBTReader.NBTTagType.COMPOUND.ID)) {
            tag.set(sub, new NBTTagCompound());
         }

         tag = tag.getCompound(sub);
      }

      if(!tag.hasKeyOfType(tkey, NBTReader.NBTTagType.COMPOUND.ID)) {
         tag.set(tkey, new NBTTagCompound());
      }

      return tag.getCompound(tkey);
   }

   public static NBTTagCompound getTagCompound(NBTTagCompound tag, String key) {
      String tkey = key;

      NBTTagCompound result;
      int end;
      for(result = tag; tkey.contains(".") && result != null; tkey = tkey.substring(end + 1)) {
         end = tkey.indexOf(".");
         String sub = tkey.substring(0, end);
         result = result.hasKeyOfType(sub, NBTReader.NBTTagType.COMPOUND.ID)?result.getCompound(sub):null;
      }

      return result != null && result.hasKeyOfType(tkey, NBTReader.NBTTagType.COMPOUND.ID)?result.getCompound(tkey):null;
   }

   public static ItemStack setString(ItemStack item, String key, String value) {
      net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
      NBTTagCompound tag = nmsItem.getTag();
      if(tag == null) {
         tag = new NBTTagCompound();
      }

      String keyBase = null;
      String keyName = key;
      if(key.contains(".")) {
         keyBase = key.substring(0, key.lastIndexOf("."));
         keyName = key.substring(key.lastIndexOf(".") + 1);
      }

      NBTTagCompound endTag = tag;
      if(keyBase != null) {
         endTag = buildTagTree(tag, keyBase);
      }

      endTag.setString(keyName, value);
      nmsItem.setTag(tag);
      return CraftItemStack.asCraftMirror(nmsItem);
   }

   public static String getString(ItemStack item, String key) {
      net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
      NBTTagCompound tag = nmsItem.getTag();
      if(tag == null) {
         return null;
      } else {
         String keyBase = null;
         String keyName = key;
         if(key.contains(".")) {
            keyBase = key.substring(0, key.lastIndexOf("."));
            keyName = key.substring(key.lastIndexOf(".") + 1);
         }

         if(keyBase != null) {
            tag = getTagCompound(tag, keyBase);
         }

         return tag != null?tag.getString(keyName):null;
      }
   }

   private static enum ObjectType {

      Unknown("Unknown", 0),
      Item("Item", 1),
      Entity("Entity", 2),
      NBTTag("NBTTag", 3),
      NBTList("NBTList", 4);
      // $FF: synthetic field
      private static final NBTReader.ObjectType[] $VALUES = new NBTReader.ObjectType[]{Unknown, Item, Entity, NBTTag, NBTList};


      private ObjectType(String var1, int var2) {}

   }

   public static enum NBTTagType {

      UNKNOWN("UNKNOWN", 0, -1),
      END("END", 1, 0),
      BYTE("BYTE", 2, 1),
      SHORT("SHORT", 3, 2),
      INT("INT", 4, 3),
      LONG("LONG", 5, 4),
      FLOAT("FLOAT", 6, 5),
      DOUBLE("DOUBLE", 7, 6),
      BYTE_ARRAY("BYTE_ARRAY", 8, 7),
      STRING("STRING", 9, 8),
      LIST("LIST", 10, 9),
      COMPOUND("COMPOUND", 11, 10),
      INT_ARRAY("INT_ARRAY", 12, 11);
      public int ID;
      // $FF: synthetic field
      private static final NBTReader.NBTTagType[] $VALUES = new NBTReader.NBTTagType[]{UNKNOWN, END, BYTE, SHORT, INT, LONG, FLOAT, DOUBLE, BYTE_ARRAY, STRING, LIST, COMPOUND, INT_ARRAY};


      private NBTTagType(String var1, int var2, int typeID) {
         this.ID = typeID;
      }

   }
}
