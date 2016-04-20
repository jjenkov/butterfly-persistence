package com.jenkov.db.test.objects;

import com.jenkov.db.itf.mapping.IObjectMapping;

import java.util.Date;
import java.sql.ResultSet;


public class NonSimilarTableName {

    private static IObjectMapping persistentObjectMapping = null;


    protected long   id   = 0;
    protected String name = null;
    protected Date   autoColumn = null;

    protected boolean wasResultSetConstructorCalled = false;

    public NonSimilarTableName(){
    }

    public NonSimilarTableName(ResultSet result){
        this.wasResultSetConstructorCalled = true;
    }

    public long getId(){
        return this.id;
    }

    public void setId(long id){
        this.id = id;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }


    public String getObjectValue(){
        return null;
    }

    public Object getObject(){
        return null;
    }

    public boolean wasResultSetConstructorCalled(){
        return this.wasResultSetConstructorCalled;
    }


    public String toString(){
        return "id = " + getId() + "   name = " + getName();
    }

    public Date getAutoColumn() {
        return autoColumn;
    }

    public void setAutoColumn(Date autoColumn) {
        this.autoColumn = autoColumn;
    }

}
