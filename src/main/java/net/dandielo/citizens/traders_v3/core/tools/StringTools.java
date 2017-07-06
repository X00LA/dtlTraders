package net.dandielo.citizens.traders_v3.core.tools;


public class StringTools {

   public static String stackTrace(StackTraceElement[] elements) {
      StringBuilder builder = new StringBuilder();
      builder.append("\n");
      builder.append("---\n");
      StackTraceElement[] var2 = elements;
      int var3 = elements.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         StackTraceElement element = var2[var4];
         builder.append(element).append("\n");
      }

      builder.append("---\n");
      return builder.toString();
   }
}
