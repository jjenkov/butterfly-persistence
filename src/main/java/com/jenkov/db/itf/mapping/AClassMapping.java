package com.jenkov.db.itf.mapping;

import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Jakob Jenkov - Copyright 2004-2006 Jenkov Development
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AClassMapping {
    String mappingMode() default "modify";  //manual / modify
    String tableName()   default "";
}
