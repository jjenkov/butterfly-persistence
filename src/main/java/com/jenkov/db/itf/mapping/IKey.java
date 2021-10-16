package com.jenkov.db.itf.mapping;

import com.jenkov.db.itf.PersistenceException;

import java.util.Collection;

/**
 * This interface represents a mapping of a primary or foreing key in a database, to a
 * list of getter and setter mappings. This key mapping is a replacement for the
 * previous primary key mappings in the IObjectMapping interface. The previous
 * primary key mapping constructs did not support compound keys (multi-column keys).
 * The <code>IKey</code> supports that. In addition a <code>IKey</code>
 * can be used to map foreign keys too.
 *
 * A key mapping consists of a table name and collections of getter and setter methods
 * that are mapped to the columns included in the key. For a primary key the table name
 * is the same table
 * as the key exists in. For a foreign key the table name is the table the primary key the
 * foreign key points to exists in.
 *
 * @author Jakob Jenkov - Copyright 2005 Jenkov Development
 */
public interface IKey {


    /**
     * Returns the table this key mapping points to. For a primary key this is the same table
     * as the key exists in. For a foreign key this is the table the primary key the
     * foreign key points to exists in.
     * @return The table this key mapping points to.
     */
    public String getTable() ;

    /**
     * Sets the table this key points to. For a primary key this is the same table
     * as the key exists in. For a foreign key this is the table the primary key the
     * foreign key points to exists in.
     *
     * @param table The table this key mapping is to point to.
     */
    public void   setTable(String table);


    /**
     * Adds a column to this key mapping.
     * @param column The column to add to this key mapping.
     */
    public void addColumn(String column);


    /**
     * Removes a column from this key mapping
     * @param column The column to remove
     */
    public void removeColumn(String column);


    /**
     * Returns the collection of columns used in this key mapping.
     * @return The collection of columns used in this key mapping.
     */
    public Collection getColumns();


    /**
     * Sets the collection of columns to use in this key mapping.
     * @param columns The collection of columns to use in this key mapping.
     */
    public void  setColumns(Collection columns);


    /**
     * Returns the number of columns in this key.
     * @return The number of columns in this key.
     */
    public int size();

    /**
     * Validates an <code>IKeyValue</code> instance against this key.
     * If the key value contains values for all columns in this key,
     * this method returns true. False if not.
     *
     * If the key value contains values for columns that are not
     * part of this key, these column values are ignored.
     *
     * @param   keyValue The key value to validate.
     * @return  True if the key is valid. False if not.
     */
    public boolean isValid(IKeyValue keyValue);



    /**
     * If this key only consists of one column, this method will return that column.
     * Otherwise a PersistenceException is thrown.
     *
     * @return The column of this key, if it consists of only one column.
     * @throws PersistenceException If this key contains zero, or more than one
     *         column.
     */
    public String getColumn() throws PersistenceException;

    /**
     * Wraps the given key value object in a <code>IKeyValue</code> instance.
     * If the object to be wrapped is already a <code>IKeyValue</code> instance
     * it will be returned unchanged.
     * This will only work if this <code>IKey</code> instance only consists of
     * a single column.
     *
     * @param  keyValueObject The object to wrap in a <code>IKeyValue</code> instance.
     * @return An <code>IKeyValue</code> instance wrapping the given object.
     * @throws PersistenceException if this <code>IKey</code> instance is a
     *         compound key (multi column key), or if empty.
     */
    public IKeyValue toKeyValue(Object keyValueObject) throws PersistenceException;

}
