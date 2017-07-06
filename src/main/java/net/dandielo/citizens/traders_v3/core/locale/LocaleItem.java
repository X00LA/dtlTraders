package net.dandielo.citizens.traders_v3.core.locale;

import java.util.ArrayList;
import java.util.List;

public class LocaleItem {

   private String name;
   private List lore;


   public LocaleItem(String name, List lore) {
      this.name = name;
      this.lore = lore;
      if(this.lore == null) {
         this.lore = new ArrayList();
      }

   }

   public String name() {
      return this.name;
   }

   public List lore() {
      return this.lore;
   }
}
