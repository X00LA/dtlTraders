package net.dandielo.citizens.traders_v3;


public enum TEntityStatus {

   SELL("SELL", 0, TEntityStatus.StatusType.TRADER, "sell"),
   BUY("BUY", 1, TEntityStatus.StatusType.TRADER, "buy"),
   SELL_AMOUNTS("SELL_AMOUNTS", 2, TEntityStatus.StatusType.TRADER, "amounts"),
   MANAGE_SELL("MANAGE_SELL", 3, TEntityStatus.StatusType.TRADER, "mSell"),
   MANAGE_BUY("MANAGE_BUY", 4, TEntityStatus.StatusType.TRADER, "mBuy"),
   MANAGE_AMOUNTS("MANAGE_AMOUNTS", 5, TEntityStatus.StatusType.TRADER, "mAmounts"),
   MANAGE_PRICE("MANAGE_PRICE", 6, TEntityStatus.StatusType.TRADER, "mPrice"),
   MANAGE_LIMIT("MANAGE_LIMIT", 7, TEntityStatus.StatusType.TRADER, "mLimit"),
   MANAGE_PLIMIT("MANAGE_PLIMIT", 8, TEntityStatus.StatusType.TRADER, "mpLimit"),
   MANAGE_UNLOCKED("MANAGE_UNLOCKED", 9, TEntityStatus.StatusType.TRADER, "mUnlocked");
   TEntityStatus.StatusType type;
   String statusName;
   // $FF: synthetic field
   private static final TEntityStatus[] $VALUES = new TEntityStatus[]{SELL, BUY, SELL_AMOUNTS, MANAGE_SELL, MANAGE_BUY, MANAGE_AMOUNTS, MANAGE_PRICE, MANAGE_LIMIT, MANAGE_PLIMIT, MANAGE_UNLOCKED};


   private TEntityStatus(String var1, int var2, TEntityStatus.StatusType type, String statusName) {
      this.type = type;
      this.statusName = statusName;
   }

   public String toString() {
      return this.statusName;
   }

   public boolean inManagementMode() {
      return !this.equals(SELL) && !this.equals(BUY) && !this.equals(SELL_AMOUNTS);
   }

   public static TEntityStatus parseBaseManageStatus(TEntityStatus oldStatus, TEntityStatus newStatus) {
      return !newStatus.equals(MANAGE_SELL) && !newStatus.equals(MANAGE_BUY) && !newStatus.equals(SELL) && !newStatus.equals(BUY)?oldStatus:newStatus;
   }

   public static TEntityStatus baseManagementStatus(String status) {
      return MANAGE_SELL.name().toLowerCase().contains(status)?MANAGE_SELL:MANAGE_BUY;
   }

   public static TEntityStatus baseStatus(String status) {
      return SELL.name().toLowerCase().equals(status)?SELL:BUY;
   }

   public String asStock() {
      return !this.equals(BUY) && !this.equals(MANAGE_BUY)?"sell":"buy";
   }


   public static enum StatusType {

      TRADER("TRADER", 0);
      // $FF: synthetic field
      private static final TEntityStatus.StatusType[] $VALUES = new TEntityStatus.StatusType[]{TRADER};


      private StatusType(String var1, int var2) {}

   }
}
