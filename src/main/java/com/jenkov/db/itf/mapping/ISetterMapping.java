package com.jenkov.db.itf.mapping;

import com.jenkov.db.itf.PersistenceException;

import java.sql.ResultSet;

/**
 * This interface represents functions special to the setter method mappings.
 * Setter method mappings represent a method from a setter method in a class
 * to a column in the database.
 *
 * Setter method mappings are used by the object reader
 * when reading objects from the database (moving the values from the
 * <code>ResultSet</code> into the objects by calling their setter methods)
 *
 * @author Jakob Jenkov, Jenkov Development
 */
public interface ISetterMapping extends IMethodMapping{

    /**
     * Moves the value of the column referenced by this setter method method
     * from the <code>ResultSet</code> into the given object, by calling the
     * <code>Method</code> instance associated with this setter method method
     * with the given object as target, and the extracted value as a parameter.
     *
     * @param target The object to insert the value into.
     * @param result The <code>ResultSet</code> to read the value from.
     * @throws PersistenceException If anything goes wrong during the transfer.
     */
    public void    insertValueIntoObject  (Object target, ResultSet result) throws PersistenceException;


    /**
     * Returns the value from column in the ResultSet that this Setter mapping maps to.
     * This is used when extracting the primary key value of a record in order to read
     * the object from a cache instead of re-instantiating it.
     * @param result The ResultSet to read the value from.
     * @return The read value.
     * @throws PersistenceException If the column this setter mapping maps to is not present in the ResultSet
     */
    public Object  getValueFromResultSet(ResultSet result) throws PersistenceException;
    
}
