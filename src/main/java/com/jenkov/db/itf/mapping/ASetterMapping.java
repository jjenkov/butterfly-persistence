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
public @interface ASetterMapping {
//    String  mappingId()           default ""; //useless
    String  columnName()          default "";
    String  columnType()          default "";    // number, string, date, binary

    //is this ever necessary to set?
    //setters don't care where the data comes from... table or query.
//    boolean columnExistsInTable() default true; // true = column is from table, false = column is from a view/query only.

}
