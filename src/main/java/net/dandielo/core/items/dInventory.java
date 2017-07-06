package net.dandielo.core.items;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.dandielo.core.items.dItem;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class dInventory implements InventoryHolder {

   private dItem[] vInventory;
   private List items;


   public dInventory(int size) {
      this.items = new ArrayList();
      this.vInventory = new dItem[size];
   }

   public dInventory(Inventory inventory, int size) {
      this(Math.max(size, inventory.getSize()));
   }

   public dInventory(List items, int size) {
      this(Math.max(size, items.size()));
   }

   public Inventory getInventory() {
      Inventory result = Bukkit.createInventory(this, 54);
      return result;
   }

   public void addItem(dItem item) {
      this.items.add(item);
   }

   public void addItem(ItemStack item) {
      this.addItem(new dItem(item));
   }

   public void setItem(int at, dItem item) {
      this.items.add(item);
   }

   public void setItem(int at, ItemStack item) {
      this.setItem(at, new dItem(item));
   }

   public dItem getItemAt(int at) {
      return this.vInventory[at];
   }

   public dItem removeAt(int at) {
      dItem result = this.vInventory[at];
      this.vInventory[at] = null;
      return result;
   }

   public int removeItem(dItem item) {
      return -1;
   }

   public int removeItem(ItemStack item) {
      return -1;
   }

   public void clear() {}

   public int nextEmptySlot() {
      return 0;
   }

   public int findItem(dItem item) {
      return -1;
   }

   public int findSimilarItem(dItem item) {
      return -1;
   }

   public boolean containsItem(dItem item) {
      return false;
   }

   public boolean containsItem(ItemStack item) {
      return false;
   }

   public int totalAmountOf(dItem item) {
      return -1;
   }

   public int totalAmountOf(ItemStack item) {
      return -1;
   }

   public String toString() {
      return this.serialize();
   }

   public String serialize() {
      return null;
   }

   public void saveToFile(File file) {}

   public YamlConfiguration toYaml() {
      return null;
   }

   public static dInventory fromFile(File file) {
      return null;
   }

   public static dInventory fromString(String str) {
      return null;
   }

   public static dInventory fromYaml(YamlConfiguration yaml) {
      return null;
   }

   public boolean equals(Object that) {
      return false;
   }

   public int hashCode() {
      return 0;
   }
}
