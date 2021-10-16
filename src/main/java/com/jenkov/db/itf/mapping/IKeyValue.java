package com.jenkov.db.itf.mapping;

import java.util.Map;

/**
 * This interface represents a concrete compound key. You can add values
 * for each column in the key.
 *
 * @author Jakob Jenkov - Copyright 2005 Jenkov Development
 */
public interface IKeyValue {

    /**
     * Adds a value to a column of this key.
     * @param column The column to specify the value of.
     * @param value  The column value.
     * @return       The same IKeyValue instance this method was called on.
     */
    public IKeyValue addColumnValue(String column, Object value);

    /**
     * Removes a column value from this key value.
     * @param column The column to remove the value of.
     * @return       The same IKeyValue instance this method was called on.
     */
    public IKeyValue removeColumnValue(String column);

    /**
     * Returns the value for the given column. If there is no
     * value for that column null is returned.
     * @param column The column to return the value of.
     * @return The column value if present. Otherwise null.
     */
    public Object getColumnValue(String column);

    /**
     * Returns a map of all column values in this key value.
     * @return A map of all column values in this key value.
     */
    public Map  getColumnValues();

}
