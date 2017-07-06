package net.dandielo.citizens.traders_v3.utils.items;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import net.dandielo.citizens.traders_v3.TEntityStatus;

@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ShopStatus {

   TEntityStatus[] status() default {};
}
