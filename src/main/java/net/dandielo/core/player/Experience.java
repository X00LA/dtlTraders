package net.dandielo.core.player;

import org.bukkit.entity.Player;

public class Experience {

   public static int getTotalExperience(Player p) {
      return getTotalExperience(p.getLevel(), (double)p.getExp());
   }

   public static int getTotalExperience(int level, double bar) {
      return getTotalExpToLevel(level) + (int)((double)getExpToLevel(level + 1) * bar + 0.5D);
   }

   public static int getExpToLevel(int level) {
      return level < 16?17:(level < 31?3 * level - 31:7 * level - 155);
   }

   public static int getTotalExpToLevel(int level) {
      return level < 16?17 * level:(level < 31?(int)(1.5D * (double)level * (double)level - 29.5D * (double)level + 360.0D):(int)(3.5D * (double)level * (double)level - 151.5D * (double)level + 2220.0D));
   }

   public static void resetExperience(Player player) {
      player.setTotalExperience(0);
      player.setLevel(0);
      player.setExp(0.0F);
   }

   public static int countLevel(int exp, int toLevel, int level) {
      if(exp < toLevel) {
         return level;
      } else {
         int var10000 = exp - toLevel;
         int var10001 = getTotalExpToLevel(level + 2) - getTotalExpToLevel(level + 1);
         ++level;
         return countLevel(var10000, var10001, level);
      }
   }

   public static void giveSilentExperience(Player player, int exp) {
      int currentExp = getTotalExperience(player);
      resetExperience(player);
      int newexp = currentExp + exp;
      if(newexp > 0) {
         int level = countLevel(newexp, 17, 0);
         player.setLevel(level);
         int epxToLvl = newexp - getTotalExpToLevel(level);
         player.setExp(epxToLvl < 0?0.0F:(float)epxToLvl / (float)getExpToLevel(level + 1));
      }

   }
}
