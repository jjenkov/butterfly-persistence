package com.jenkov.db.itf.mapping;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * @author Jakob Jenkov - Copyright 2004-2006 Jenkov Development
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AGetterMapping {
//    String  mappingId()           default ""; //useless!
    String  columnName()          default "";
    String  columnType()          default "";   // number, string, date, binary
    boolean includeInWrites()     default true; // true = column is from table, false = column is from a view/query only.
    boolean databaseGenerated()   default false;
}
