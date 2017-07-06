package net.dandielo.citizens.traders_v3.core.exceptions.attributes;

import net.dandielo.citizens.traders_v3.core.exceptions.attributes.AttributeException;
import net.dandielo.core.items.serialize.Attribute;

public class AttributeInvalidValueException extends AttributeException {

   private static final long serialVersionUID = 1L;
   private Attribute attr;
   private String data;


   public AttributeInvalidValueException(Attribute attr, String data) {
      this.attr = attr;
      this.data = data;
   }

   public Attribute getAttrInfo() {
      return this.attr;
   }

   public String getData() {
      return this.data;
   }
}
