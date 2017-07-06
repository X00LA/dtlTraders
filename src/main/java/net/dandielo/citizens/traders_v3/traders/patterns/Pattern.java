package net.dandielo.citizens.traders_v3.traders.patterns;

import org.bukkit.configuration.ConfigurationSection;

public abstract class Pattern {

   private Pattern.Type type;
   private String name;
   protected int priority = 0;
   protected boolean tier = false;


   protected Pattern(String name, Pattern.Type type) {
      this.type = type;
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   public Pattern.Type getType() {
      return this.type;
   }

   public abstract void loadItems(ConfigurationSection var1);

   public static enum Type {

      PRICE("PRICE", 0),
      ITEM("ITEM", 1),
      LAYOUT("LAYOUT", 2),
      CHANCE("CHANCE", 3);
      // $FF: synthetic field
      private static final Pattern.Type[] $VALUES = new Pattern.Type[]{PRICE, ITEM, LAYOUT, CHANCE};


      private Type(String var1, int var2) {}

      public boolean isItem() {
         return this.equals(ITEM);
      }

      public boolean isPrice() {
         return this.equals(PRICE);
      }

      public boolean isLayout() {
         return this.equals(LAYOUT);
      }

      public boolean isChance() {
         return this.equals(CHANCE);
      }

   }
}
