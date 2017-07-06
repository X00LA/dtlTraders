package net.dandielo.citizens.traders_v3.bukkit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;
import net.dandielo.citizens.traders_v3.TEntityListener;
import net.dandielo.citizens.traders_v3.tNpcManager;
import net.dandielo.citizens.traders_v3.bukkit.commands.GeneralCommands;
import net.dandielo.citizens.traders_v3.bukkit.commands.TraderCommands;
import net.dandielo.citizens.traders_v3.core.PluginSettings;
import net.dandielo.citizens.traders_v3.core.dB;
import net.dandielo.citizens.traders_v3.core.commands.CommandManager;
import net.dandielo.citizens.traders_v3.traders.limits.LimitManager;
import net.dandielo.citizens.traders_v3.traders.setting.GlobalSettings;
import net.dandielo.citizens.traders_v3.traits.TraderTrait;
import net.dandielo.citizens.traders_v3.traits.WalletTrait;
import net.dandielo.citizens.traders_v3.utils.items.attributes.BlockCurrency;
import net.dandielo.citizens.traders_v3.utils.items.attributes.Limit;
import net.dandielo.citizens.traders_v3.utils.items.attributes.Multiplier;
import net.dandielo.citizens.traders_v3.utils.items.attributes.PatternItem;
import net.dandielo.citizens.traders_v3.utils.items.attributes.PlayerResourcesCurrency;
import net.dandielo.citizens.traders_v3.utils.items.attributes.Price;
import net.dandielo.citizens.traders_v3.utils.items.attributes.Slot;
import net.dandielo.citizens.traders_v3.utils.items.attributes.Tier;
import net.dandielo.citizens.traders_v3.utils.items.flags.AnyLore;
import net.dandielo.citizens.traders_v3.utils.items.flags.NoStack;
import net.dandielo.citizens.traders_v3.utils.items.flags.Regex;
import net.dandielo.citizens.traders_v3.utils.items.flags.StackPrice;
import net.dandielo.core.items.serialize.ItemAttribute;
import net.dandielo.core.items.serialize.ItemFlag;
import net.dandielo.core.items.serialize.core.Amount;
import net.dandielo.core.items.serialize.core.Banner;
import net.dandielo.core.items.serialize.core.Book;
import net.dandielo.core.items.serialize.core.Durability;
import net.dandielo.core.items.serialize.core.Enchants;
import net.dandielo.core.items.serialize.core.Firework;
import net.dandielo.core.items.serialize.core.GenericDamage;
import net.dandielo.core.items.serialize.core.GenericHealth;
import net.dandielo.core.items.serialize.core.GenericKnockback;
import net.dandielo.core.items.serialize.core.GenericSpeed;
import net.dandielo.core.items.serialize.core.HideFlags;
import net.dandielo.core.items.serialize.core.LeatherColor;
import net.dandielo.core.items.serialize.core.Map;
import net.dandielo.core.items.serialize.core.Name;
import net.dandielo.core.items.serialize.core.Potion;
import net.dandielo.core.items.serialize.core.Shield;
import net.dandielo.core.items.serialize.core.Skull;
import net.dandielo.core.items.serialize.core.SpawnEgg;
import net.dandielo.core.items.serialize.core.StoredEnchant;
import net.dandielo.core.items.serialize.flags.Lore;
import net.dandielo.core.items.serialize.flags.SplashPotion;
import net.dandielo.core.items.serialize.flags.UnbreakableFlag;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;

public class DtlTraders extends JavaPlugin implements Listener {

   public static final String PREFIX = "[dtlTraders]" + ChatColor.WHITE;
   private static ConsoleCommandSender console;
   private static DtlTraders instance;
   LimitManager limits;


   public void onLoad() {}

   public void onEnable() {
      this.getServer().getPluginManager().registerEvents(this, this);
      this.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cDtlCore is implemented with DtlTraders and is now ONE plugin :) &aCreated with our experience @4Creation."));
      if(this.checkCore()) {
         this.getServer().getPluginManager().disablePlugin(this.getServer().getPluginManager().getPlugin("dtlCore"));
         this.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cDtlCore has been found and disabled."));
      }

      instance = this;
      console = this.getServer().getConsoleSender();
      this.saveDefaultConfig();
      PluginSettings.initPluginSettings();
      dB.info(new Object[]{"Enabling plugin"});
      info("Loading config files");
      info("Registering attributes and flags...");
      ItemAttribute.registerAttr(StoredEnchant.class);
      ItemAttribute.registerAttr(LeatherColor.class);
      ItemAttribute.registerAttr(Durability.class);
      ItemAttribute.registerAttr(HideFlags.class);
      ItemAttribute.registerAttr(Enchants.class);
      ItemAttribute.registerAttr(SpawnEgg.class);
      ItemAttribute.registerAttr(Firework.class);
      ItemAttribute.registerAttr(Amount.class);
      ItemAttribute.registerAttr(Banner.class);
      ItemAttribute.registerAttr(Shield.class);
      ItemAttribute.registerAttr(Potion.class);
      ItemAttribute.registerAttr(Skull.class);
      ItemAttribute.registerAttr(Book.class);
      ItemAttribute.registerAttr(Name.class);
      ItemAttribute.registerAttr(Map.class);
      ItemAttribute.extendAttrKey("g", GenericKnockback.class);
      ItemAttribute.extendAttrKey("g", GenericDamage.class);
      ItemAttribute.extendAttrKey("g", GenericHealth.class);
      ItemAttribute.extendAttrKey("g", GenericSpeed.class);
      ItemFlag.registerFlag(UnbreakableFlag.class);
      ItemFlag.registerFlag(SplashPotion.class);
      ItemFlag.registerFlag(Lore.class);
      if(!this.initVault()) {
         this.setEnabled(false);
         this.getPluginLoader().disablePlugin(this);
         severe("Vault plugin not found, disabling dtlTraders.");
      } else if(!this.checkCitizens()) {
         this.setEnabled(false);
         this.getPluginLoader().disablePlugin(this);
         severe("Citizen2 plugin not found, disabling dtlTraders.");
      } else {
         info("Loading config files");
         GlobalSettings.initGlobalSettings();
         CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(TraderTrait.class).withName("trader"));
         CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(WalletTrait.class).withName("wallet"));
         ItemAttribute.registerAttr(PatternItem.class);
         ItemAttribute.registerAttr(Multiplier.class);
         ItemAttribute.registerAttr(Limit.class);
         ItemAttribute.registerAttr(Price.class);
         ItemAttribute.registerAttr(Slot.class);
         ItemAttribute.registerAttr(Tier.class);
         ItemAttribute.extendAttrKey("p", PlayerResourcesCurrency.class);
         ItemAttribute.extendAttrKey("p", BlockCurrency.class);
         ItemFlag.registerFlag(StackPrice.class);
         ItemFlag.registerFlag(AnyLore.class);
         ItemFlag.registerFlag(NoStack.class);
         ItemFlag.registerFlag(Regex.class);
         tNpcManager.registerTypes();
         this.getServer().getPluginManager().registerEvents(TEntityListener.instance(), this);
         CommandManager.manager.registerCommands(GeneralCommands.class);
         CommandManager.manager.registerCommands(TraderCommands.class);
         this.initDenizens();
         this.initStats();
         this.limits = LimitManager.self;
         this.limits.init();
         info("Enabled");
      }
   }

   public void onDisable() {
      this.limits.save();
   }

   private void initDenizens() {}

   private void initStats() {}

   private boolean initVault() {
      if(this.getServer().getPluginManager().getPlugin("Vault") == null) {
         warning("Vault plugin not found! Disabling plugin");
         return false;
      } else {
         return Econ.econ.isEnabled();
      }
   }

   private boolean checkCitizens() {
      return this.getServer().getPluginManager().getPlugin("Citizens") != null;
   }

   private boolean checkCore() {
      return this.getServer().getPluginManager().getPlugin("dtlCore") != null;
   }

   public static DtlTraders getInstance() {
      return instance;
   }

   public static void info(String message) {
      console.sendMessage(PREFIX + "[INFO] " + message);
   }

   public static void warning(String message) {
      console.sendMessage(PREFIX + ChatColor.GOLD + "[WARNING] " + ChatColor.RESET + message);
   }

   public static void severe(String message) {
      console.sendMessage(PREFIX + ChatColor.RED + "[SEVERE] " + ChatColor.RESET + message);
   }

   @EventHandler
   public void onPlayerJoin(PlayerJoinEvent event) {
      Player player = event.getPlayer();
      if(player.isOp()) {
         try {
            JSONObject json = this.readJsonFromUrl("http://mc.4creation.pro/dtlTraders/version.php");
            if(!json.getString("version").equals(this.getDescription().getVersion())) {
               player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eDtltraders &c&oThere is a new update available: &3&ohttps://dev.bukkit.org/projects/dtltraders"));
            }
         } catch (Exception var4) {
            ;
         }

         if(!this.getConfig().isSet("version")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&oSince dtlTraders \\\"3.5.3\\\" you need to reset the old files of dtlTraders otherwise the plugin wont work like we made it, without bugs. (we are not responsible for the problems if you don\'t do it)."));
         } else if(!this.getConfig().getString("version").equals("3.5.3")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&oSince dtlTraders \\\"3.5.3\\\" you need to reset the old files of dtlTraders otherwise the plugin wont work like we made it, without bugs. (we are not responsible for the problems if you don\'t do it)."));
         }
      }

   }

   private String readAll(Reader rd) throws IOException {
      StringBuilder sb = new StringBuilder();

      int cp;
      while((cp = rd.read()) != -1) {
         sb.append((char)cp);
      }

      return sb.toString();
   }

   public JSONObject readJsonFromUrl(String url) throws Exception {
      InputStream is = (new URL(url)).openStream();

      JSONObject var6;
      try {
         BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
         String jsonText = this.readAll(rd);
         JSONObject json = new JSONObject(jsonText);
         var6 = json;
      } finally {
         is.close();
      }

      return var6;
   }

}
