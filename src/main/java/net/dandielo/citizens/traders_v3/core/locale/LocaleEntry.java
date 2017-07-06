package net.dandielo.citizens.traders_v3.core.locale;


public class LocaleEntry {

   private String newkey;
   private String ver;
   private String key;


   public LocaleEntry(String key, String ver) {
      this.key = key;
      this.ver = ver;
   }

   public LocaleEntry(String key, String newkey, String ver) {
      this.key = key;
      this.ver = ver;
      this.newkey = newkey;
   }

   public boolean hasNewkey() {
      return !this.newkey.isEmpty();
   }

   public String newkey() {
      return this.newkey.isEmpty()?this.key:this.newkey;
   }

   public String key() {
      return this.key;
   }

   public String ver() {
      return this.ver;
   }

   public String toString() {
      return this.key;
   }

   public int hashCode() {
      return this.key.hashCode();
   }

   public boolean equals(Object o) {
      return o instanceof LocaleEntry && this.key.equals(((LocaleEntry)o).key);
   }
}
