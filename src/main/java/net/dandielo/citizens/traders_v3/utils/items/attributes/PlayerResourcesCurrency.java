package net.dandielo.citizens.traders_v3.utils.items.attributes;

import java.util.Iterator;
import java.util.List;
import net.dandielo.citizens.traders_v3.core.locale.LocaleManager;
import net.dandielo.citizens.traders_v3.traders.transaction.CurrencyHandler;
import net.dandielo.citizens.traders_v3.traders.transaction.TransactionInfo;
import net.dandielo.citizens.traders_v3.utils.items.StockItemAttribute;
import net.dandielo.core.items.dItem;
import net.dandielo.core.items.serialize.Attribute;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@Attribute(
   name = "Player Resources Currency",
   key = "p",
   sub = {"h", "f", "e", "l"},
   standalone = true,
   priority = 0
)
public class PlayerResourcesCurrency extends StockItemAttribute implements CurrencyHandler {

   private int experience;
   private double health;
   private int level;
   private int food;


   public PlayerResourcesCurrency(dItem item, String key, String sub) {
      super(item, key, sub);
   }

   public boolean finalizeTransaction(TransactionInfo info) {
      String stock = info.getStock().name().toLowerCase();
      Player player = info.getPlayerParticipant();
      int amount = info.getAmount();
      if(stock.equals("sell")) {
         if(this.getSubkey().equals("h")) {
            player.setHealth(player.getHealth() - this.health * (double)amount);
         }

         if(this.getSubkey().equals("f")) {
            player.setFoodLevel(player.getFoodLevel() - this.food * amount);
         }

         if(this.getSubkey().equals("e")) {
            giveSilentExperience(player, (int)((double)(-this.experience) * info.getTotalScaling()));
         }

         if(this.getSubkey().equals("l")) {
            player.setLevel(player.getLevel() - this.level * amount);
         }
      } else if(stock.equals("buy")) {
         if(this.getSubkey().equals("h")) {
            double fd = this.health * (double)amount + player.getHealth();
            player.setHealth(fd > player.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).getBaseValue()?player.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).getBaseValue():fd);
         }

         if(this.getSubkey().equals("f")) {
            int fd1 = this.food * amount + player.getFoodLevel();
            player.setFoodLevel(fd1 > 20?20:fd1);
         }

         if(this.getSubkey().equals("e")) {
            giveSilentExperience(player, (int)((double)this.experience * info.getTotalScaling()));
         }

         if(this.getSubkey().equals("l")) {
            player.setLevel(player.getLevel() + this.level * amount);
         }
      }

      return true;
   }

   public boolean allowTransaction(TransactionInfo info) {
      String stock = info.getStock().name().toLowerCase();
      Player player = info.getPlayerParticipant();
      int amount = info.getAmount();
      if(stock.equals("sell")) {
         if(this.getSubkey().equals("h")) {
            return player.getHealth() > (double)amount * this.health;
         }

         if(this.getSubkey().equals("f")) {
            return player.getFoodLevel() >= this.food * amount;
         }

         if(this.getSubkey().equals("e")) {
            return getTotalExperience(player) >= (int)(info.getTotalScaling() * (double)this.experience);
         }

         if(this.getSubkey().equals("l")) {
            return player.getLevel() >= amount * this.level;
         }
      } else if(stock.equals("buy")) {
         return info.getBuyer() != null;
      }

      return false;
   }

   public double getTotalPrice(TransactionInfo info) {
      return this.getSubkey().equals("h")?this.health * (double)info.getAmount():(this.getSubkey().equals("f")?(double)(this.food * info.getAmount()):(this.getSubkey().equals("e")?(double)((int)((double)this.experience * info.getTotalScaling())):(this.getSubkey().equals("l")?(double)(this.level * info.getAmount()):0.0D)));
   }

   public void getDescription(TransactionInfo info, List lore) {
      int amount = info.getAmount();
      LocaleManager lm = LocaleManager.locale;
      ChatColor mReqColor = this.allowTransaction(info)?ChatColor.GREEN:ChatColor.RED;
      Iterator var6 = lm.getLore("item-currency-price").iterator();

      while(var6.hasNext()) {
         String lLine = (String)var6.next();
         if(this.getSubkey().equals("h")) {
            lore.add(lLine.replace("{amount}", String.valueOf((double)amount * this.health)).replace("{text}", " ").replace("{currency}", mReqColor + lm.getKeyword("health-points")));
         }

         if(this.getSubkey().equals("f")) {
            lore.add(lLine.replace("{amount}", String.valueOf(amount * this.food)).replace("{text}", " ").replace("{currency}", mReqColor + lm.getKeyword("food-level")));
         }

         if(this.getSubkey().equals("e")) {
            lore.add(lLine.replace("{amount}", String.valueOf(info.getTotalScaling() * (double)this.experience)).replace("{text}", " ").replace("{currency}", mReqColor + lm.getKeyword("experience")));
         }

         if(this.getSubkey().equals("l")) {
            lore.add(lLine.replace("{amount}", String.valueOf(amount * this.level)).replace("{text}", " ").replace("{currency}", mReqColor + lm.getKeyword("level")));
         }
      }

   }

   public String getName() {
      return this.getSubkey().equals("h")?"Health currency":(this.getSubkey().equals("f")?"Food currency":(this.getSubkey().equals("e")?"Experience currency":(this.getSubkey().equals("l")?"Level currency":"Error!")));
   }

   public boolean deserialize(String data) {
      if(data == "") {
         return false;
      } else {
         if(this.getSubkey().equals("h")) {
            this.health = Double.parseDouble(data);
         }

         if(this.getSubkey().equals("f")) {
            this.food = Integer.parseInt(data);
         }

         if(this.getSubkey().equals("e")) {
            this.experience = Integer.parseInt(data);
         }

         if(this.getSubkey().equals("l")) {
            this.level = Integer.parseInt(data);
         }

         return true;
      }
   }

   public String serialize() {
      return this.getSubkey().equals("h")?String.valueOf(this.health):(this.getSubkey().equals("f")?String.valueOf(this.food):(this.getSubkey().equals("e")?String.valueOf(this.experience):(this.getSubkey().equals("l")?String.valueOf(this.level):"")));
   }

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
