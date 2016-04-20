package com.jenkov.db.test.mapping;

import com.jenkov.db.itf.mapping.AClassMapping;
import com.jenkov.db.itf.mapping.AGetterMapping;
import com.jenkov.db.itf.mapping.ASetterMapping;

/**
 * @author Jakob Jenkov - Copyright 2004-2006 Jenkov Development
 */

//@AClassMapping(tableName = "persistent_object")
@AClassMapping(tableName = "PERSISTENT_OBJECT")
public class AnnotatedClass {
    protected long   id   = -1;
    protected String name = null;

    protected long   someValue = -1;
    protected String someName  = null;



    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @AGetterMapping(columnName = "SOME_BOOLEAN", includeInWrites = true)
    public long getSomeValue(){
        return this.someValue;
    }

    @ASetterMapping(columnName = "SOME_BOOLEAN")
    public void setSomeValue(long value){
        this.someValue = value;
    }

    @AGetterMapping(columnName = "FIELDVALUE", includeInWrites = true)
    public String getSomeName(){
        return this.someName;
    }

    @ASetterMapping( columnName = "FIELDVALUE")
    public void setSomeName(String name){
        this.someName = name;
    }


}
