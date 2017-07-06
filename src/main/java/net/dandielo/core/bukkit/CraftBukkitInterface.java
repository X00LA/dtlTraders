package net.dandielo.core.bukkit;

import org.bukkit.Bukkit;

public class CraftBukkitInterface {

   private static final String CRAFTBUKKIT_PREFIX = "org.bukkit.craftbukkit";
   private static final String MINECRAFT_NET_PREFIX = "net.minecraft.server";
   private static final String VERSION;


   public static String getCBClassName(String simpleName) {
      return VERSION == null?null:"org.bukkit.craftbukkit" + VERSION + simpleName;
   }

   public static Class getCBClass(String name) {
      if(VERSION == null) {
         return null;
      } else {
         try {
            return Class.forName(getCBClassName(name));
         } catch (ClassNotFoundException var2) {
            return null;
         }
      }
   }

   public static String getMNClassName(String simpleName) {
      return VERSION == null?null:"net.minecraft.server" + VERSION + simpleName;
   }

   public static Class getNMClass(String name) {
      if(VERSION == null) {
         return null;
      } else {
         try {
            return Class.forName(getMNClassName(name));
         } catch (ClassNotFoundException var2) {
            return null;
         }
      }
   }

   static {
      Class serverClass = Bukkit.getServer().getClass();
      if(!serverClass.getSimpleName().equals("CraftServer")) {
         VERSION = null;
      } else if(serverClass.getName().equals("org.bukkit.craftbukkit.CraftServer")) {
         VERSION = ".";
      } else {
         String name = serverClass.getName();
         name = name.substring("org.bukkit.craftbukkit".length());
         name = name.substring(0, name.length() - "CraftServer".length());
         VERSION = name;
      }

   }
}
