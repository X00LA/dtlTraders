package net.dandielo.citizens.traders_v3.traders.limits;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class LimitEntry {

   private String id;
   private int limit;
   private long timeout;
   private int playerLimit;
   private long playerTimeout;
   private Map playerUsed;


   public LimitEntry(String id, int limit, long timeout) {
      this.id = id;
      this.limit = limit;
      this.timeout = timeout;
      this.playerLimit = -1;
      this.playerTimeout = -1L;
      this.playerUsed = new HashMap();
   }

   public LimitEntry(String id, int limit, long timeout, int plimit, long ptimeout) {
      this.id = id;
      this.limit = limit;
      this.timeout = timeout;
      this.playerLimit = plimit;
      this.playerTimeout = ptimeout;
      this.playerUsed = new HashMap();
   }

   public void limitRefresh() {
      if(this.timeout != -1L) {
         long now = (new Date()).getTime();
         Iterator var3 = this.playerUsed.values().iterator();

         while(var3.hasNext()) {
            Map entries = (Map)var3.next();
            Iterator it = entries.entrySet().iterator();

            while(it.hasNext()) {
               if(now >= ((Long)((Entry)it.next()).getKey()).longValue() + this.timeout * 1000L) {
                  it.remove();
               }
            }
         }
      }

   }

   public int totalPlayer(String palyerEntry) {
      int result = 0;

      Integer value;
      for(Iterator var3 = ((Map)this.playerUsed.get(palyerEntry)).values().iterator(); var3.hasNext(); result += value.intValue()) {
         value = (Integer)var3.next();
      }

      return result;
   }

   public int totalUsed() {
      int result = 0;

      String playerEntry;
      for(Iterator var2 = this.playerUsed.keySet().iterator(); var2.hasNext(); result += this.totalPlayer(playerEntry)) {
         playerEntry = (String)var2.next();
      }

      return result;
   }

   public boolean isAvailable(String type, int amount) {
      boolean result = false;
      if(type.equals("buy")) {
         int h = this.totalUsed();
         result = h + amount <= this.limit;
      } else if(type.equals("sell")) {
         result = this.totalUsed() + amount <= this.limit;
      }

      return result;
   }

   public boolean isPlayerAvailable(String player, String type, int amount) {
      boolean var10000;
      if(this.playerLimit != -1) {
         label25: {
            if(this.playerUsed.containsKey(player + "@" + type)) {
               if(Math.abs(this.totalPlayer(player + "@" + type)) + Math.abs(amount) <= this.playerLimit) {
                  break label25;
               }
            } else if(Math.abs(amount) <= this.playerLimit) {
               break label25;
            }

            var10000 = false;
            return var10000;
         }
      }

      var10000 = true;
      return var10000;
   }

   public void playerUpdate(String player, String type, int amount) {
      if(!this.playerUsed.containsKey(player + "@" + type)) {
         this.playerUsed.put(player + "@" + type, new HashMap());
      }

      long time = (new Date()).getTime();
      ((Map)this.playerUsed.get(player + "@" + type)).put(Long.valueOf(time), Integer.valueOf(amount));
   }

   public void playerLoad(String playerEntry, long time, int amount) {
      if(!this.playerUsed.containsKey(playerEntry)) {
         this.playerUsed.put(playerEntry, new HashMap());
      }

      ((Map)this.playerUsed.get(playerEntry)).put(Long.valueOf(time), Integer.valueOf(amount));
   }

   int getLimit() {
      return this.limit;
   }

   long getTimeout() {
      return this.timeout;
   }

   int getPlayerLimit() {
      return this.playerLimit;
   }

   long getPlayerTimeout() {
      return this.playerTimeout;
   }

   Map playerEntries() {
      return this.playerUsed;
   }
}
