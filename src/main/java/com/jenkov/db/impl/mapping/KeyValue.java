package com.jenkov.db.impl.mapping;

import com.jenkov.db.itf.mapping.IKeyValue;

import java.util.Map;
import java.util.TreeMap;
import java.util.Iterator;

/**
 * @author Jakob Jenkov - Copyright 2005 Jenkov Development
 */
public class KeyValue implements IKeyValue{

    protected Map columnValues = new TreeMap();

    public IKeyValue addColumnValue(String column, Object value) {
        this.columnValues.put(column, value);
        return this;
    }

    public IKeyValue removeColumnValue(String column) {
        this.columnValues.remove(column);
        return this;
    }

    public Object getColumnValue(String column) {
        Object columnValue = columnValues.get(column);
        if(columnValue == null){
            columnValue = columnValues.get(column.toUpperCase());
        }
        if(columnValue == null){
            columnValue = columnValues.get(column.toLowerCase());
        }
        return columnValue;
    }

    public Map getColumnValues() {
        return this.columnValues;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("(");

        Iterator iterator = columnValues.keySet().iterator();
        while(iterator.hasNext()){
            String column = (String) iterator.next();
            buffer.append(column);
            buffer.append(" = ");
            buffer.append(getColumnValue(column));
            if(iterator.hasNext()){
                buffer.append(", ");
            }
        }

        buffer.append(")");

        return buffer.toString();
    }

}
