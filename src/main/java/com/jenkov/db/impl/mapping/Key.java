package com.jenkov.db.impl.mapping;

import com.jenkov.db.itf.PersistenceException;
import com.jenkov.db.itf.mapping.IKey;
import com.jenkov.db.itf.mapping.IKeyValue;
import com.jenkov.db.itf.mapping.IObjectMapping;
import com.jenkov.db.util.ClassUtil;
import com.jenkov.db.util.CollectionUtils;

import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * Represents a key, either primary or foreign key, in a database table. The key consists of
 * one or more columns. To create key values for a compound primary or foreign key,
 * use the IKeyValue interface and the KeyValue implementation.
 *
 * @author Jakob Jenkov - Copyright 2005 Jenkov Development
 * @see IKeyValue
 * @see KeyValue
 */
public class Key implements IKey{

    protected IObjectMapping objectMapping = null;
    protected String         table         = null;
    protected Collection     columns       = new TreeSet();

    /**
     * Creates an empty key instance.
     * You can add columns to the key after creation.
     */
    public Key() {
    }

    /**
     * Creates a key consisting of the given columns.
     * You can add more columns to the key after creation.
     * @param columns The names of the columns in the key.
     */
    public Key(Collection columns) {
        this.columns = columns;
    }

    /**
     * Creates a key consisting of a single column.
     * You can add more columns to the key after creation.
     * @param column The name of the key column.
     */
    public Key(String column){
        this.columns.add(column);
    }

    /**
     * Creates a key consisting of the given columns.
     * You can add more columns to the key after creation.
     * @param columns The names of the columns in the key.
     */
    public Key(String[] columns){
        for (int i = 0; i < columns.length; i++) {
            String column = columns[i];
            this.columns.add(columns[i]);
        }
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public Collection getColumns() {
        return columns;
    }

    public void setColumns(Collection columns) {
        this.columns = columns;
    }

    public int size() {
        return this.columns.size();
    }

    public void addColumn(String column) {
        this.columns.add(column);
    }

    public void removeColumn(String column) {
        this.columns.remove(column);
    }

    public boolean isValid(IKeyValue keyValue) {
        Iterator iterator = this.columns.iterator();
        while(iterator.hasNext()){
            String column = (String) iterator.next();
            if(keyValue.getColumnValue(column) == null      &&
               keyValue.getColumnValue(column.toUpperCase()) == null &&
               keyValue.getColumnValue(column.toLowerCase()) == null ){
                return false;
            }
        }
        return true;
    }

    public String getColumn() throws PersistenceException {
        if(columns.size() == 1){
            return (String) columns.iterator().next();
        }
        throw new PersistenceException("Key does not consist of a single column. Key was: " + toString());
    }

    public IKeyValue toKeyValue(Object keyValueObject) throws PersistenceException{
        if(keyValueObject == null){
            throw new PersistenceException("Key value object was null. Object must be non-null.");
        }

        if(columns.size() != 1){
            throw new PersistenceException("The key must consist of exactly one column. " +
                    "Key was: " + toString());
        }
        if(keyValueObject instanceof IKeyValue) return (IKeyValue) keyValueObject;

        IKeyValue keyValue = new KeyValue();
        keyValue.addColumnValue((String) columns.iterator().next(), keyValueObject);
        return keyValue;
    }

    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(! (obj instanceof IKey)) return false;

        IKey other = (IKey) obj;
        if(! ClassUtil.areEqual(getTable(), other.getTable())) return false;

        return CollectionUtils.areEqual(getColumns(), other.getColumns());
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        
        buffer.append(table);
        buffer.append("(");
        Iterator iterator = columns.iterator();
        while(iterator.hasNext()){
            buffer.append(iterator.next());
            if(iterator.hasNext()){
                buffer.append(", ");
            }
        }
        buffer.append(")");
        return buffer.toString();
    }

}
