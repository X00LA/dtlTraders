package net.dandielo.core.items.serialize;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.bukkit.Material;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Attribute {

   String name();

   String key();

   String[] sub() default {};

   Material[] items() default {};

   int priority() default -1;

   boolean required() default false;

   boolean standalone() default false;
}
