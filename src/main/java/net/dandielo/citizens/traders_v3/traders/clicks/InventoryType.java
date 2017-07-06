package net.dandielo.citizens.traders_v3.traders.clicks;


public enum InventoryType {

   TRADER("TRADER", 0),
   PLAYER("PLAYER", 1);
   // $FF: synthetic field
   private static final InventoryType[] $VALUES = new InventoryType[]{TRADER, PLAYER};


   private InventoryType(String var1, int var2) {}

   public boolean equals(boolean b) {
      return b?this.equals(TRADER):this.equals(PLAYER);
   }

}
