package net.dandielo.citizens.traders_v3;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.citizensnpcs.api.npc.NPC;
import net.dandielo.citizens.traders_v3.TradingEntity;
import net.dandielo.citizens.traders_v3.tNpcType;
import net.dandielo.citizens.traders_v3.bukkit.DtlTraders;
import net.dandielo.citizens.traders_v3.core.dB;
import net.dandielo.citizens.traders_v3.core.exceptions.InvalidTraderTypeException;
import net.dandielo.citizens.traders_v3.core.exceptions.TraderTypeNotFoundException;
import net.dandielo.citizens.traders_v3.core.exceptions.TraderTypeRegistrationException;
import net.dandielo.citizens.traders_v3.traders.Trader;
import net.dandielo.citizens.traders_v3.traders.types.Private;
import net.dandielo.citizens.traders_v3.traders.types.Server;
import net.dandielo.citizens.traders_v3.traits.WalletTrait;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class tNpcManager {

   private static final tNpcManager instance = new tNpcManager();
   private Map relations = new HashMap();
   private Map tNpcInventories = new HashMap();
   private static final Map types = new HashMap();


   public static tNpcManager instance() {
      return instance;
   }

   public boolean checkRelationType(String player, Class clazz) {
      return this.inRelation(player)?clazz.isInstance(this.relations.get(player)):false;
   }

   public boolean inRelation(Player player) {
      return this.inRelation(player.getName());
   }

   public boolean inRelation(String player) {
      return this.relations.containsKey(player);
   }

   public void registerRelation(Player player, TradingEntity npc) {
      this.relations.put(player.getName(), npc);
   }

   public TradingEntity getRelation(String player, Class clazz) {
      return this.checkRelationType(player, clazz)?(TradingEntity)this.relations.get(player):null;
   }

   public Trader getTraderRelation(HumanEntity player) {
      return this.getTraderRelation(player.getName());
   }

   public Trader getTraderRelation(Player player) {
      return this.getTraderRelation(player.getName());
   }

   public Trader getTraderRelation(String player) {
      return (Trader)this.getRelation(player, Trader.class);
   }

   public void removeRelation(HumanEntity player) {
      this.removeRelation(player.getName());
   }

   public void removeRelation(Player player) {
      this.removeRelation(player.getName());
   }

   public void removeRelation(String player) {
      this.relations.remove(player);
   }

   public boolean tNpcInventoryOpened(Player player) {
      return this.tNpcInventories.containsKey(player.getName());
   }

   public void registerOpenedInventory(Player player, Inventory inventory) {
      this.tNpcInventories.put(player.getName(), inventory);
   }

   public Collection getInventories() {
      return this.tNpcInventories.values();
   }

   public void removeOpenedInventory(Player player) {
      this.tNpcInventories.remove(player.getName());
   }

   public static void registerTypes() {
      try {
         dB.info(new Object[]{"Register server trader type"});
         registerType(Server.class);
         dB.info(new Object[]{"Register private banker type"});
         Trader.registerHandlers(Server.class);
         Trader.registerHandlers(Private.class);
         DtlTraders.info("Registered types: " + typesAsString());
      } catch (TraderTypeRegistrationException var1) {
         var1.printStackTrace();
      }

   }

   public static void registerType(Class class1) throws TraderTypeRegistrationException {
      if(!class1.isAnnotationPresent(tNpcType.class)) {
         throw new TraderTypeRegistrationException();
      } else {
         types.put(class1.getAnnotation(tNpcType.class), class1);
      }
   }

   public static TradingEntity create_tNpc(NPC npc, String type, Player player, Class traitClazz) throws TraderTypeNotFoundException, InvalidTraderTypeException {
      tNpcType typeInfo = null;
      Iterator clazz = types.keySet().iterator();

      while(clazz.hasNext()) {
         tNpcType resultNpc = (tNpcType)clazz.next();
         if(resultNpc.name().equals(type)) {
            typeInfo = resultNpc;
         }
      }

      if(typeInfo == null) {
         throw new TraderTypeNotFoundException(type);
      } else {
         if(!npc.hasTrait(WalletTrait.class)) {
            npc.addTrait(WalletTrait.class);
         }

         Class clazz1 = (Class)types.get(typeInfo);
         TradingEntity resultNpc1 = null;

         try {
            if(clazz1.getConstructor(new Class[]{traitClazz, WalletTrait.class, Player.class}) != null) {
               resultNpc1 = (TradingEntity)clazz1.getConstructor(new Class[]{traitClazz, WalletTrait.class, Player.class}).newInstance(new Object[]{npc.getTrait(traitClazz), npc.getTrait(WalletTrait.class), player});
            }

            return resultNpc1;
         } catch (Exception var8) {
            dB.critical(new Object[]{"Invalid type: " + typeInfo.name() + ", author: " + typeInfo.author()});
            throw new InvalidTraderTypeException(type);
         }
      }
   }

   private static String typesAsString() {
      String result = "";

      tNpcType attr;
      for(Iterator var1 = types.keySet().iterator(); var1.hasNext(); result = result + " ," + ChatColor.YELLOW + attr.name() + ChatColor.RESET) {
         attr = (tNpcType)var1.next();
      }

      return ChatColor.WHITE + "[" + result.substring(2) + ChatColor.WHITE + "]";
   }

}
