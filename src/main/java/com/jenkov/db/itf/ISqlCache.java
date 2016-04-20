package com.jenkov.db.itf;

/**
 * This interface represents all functions made available by the SQL caches in Butterfly Persistence.
 * The responsibility of the SQL cache is to keep generated SQL strings in memory so they
 * don't have to be regenerated everytime they are needed.
 * @author Jakob Jenkov,  Jenkov Development
 */
public interface ISqlCache {

    /**
     * Returns true if the SQL cache contains a statement for the given object method key.
     * False if not.
     * @param mappingKey The object method key to check if the SQL cache contains a statement for.
     * @return True if the SQL cache contains a statement for the given mappingkey . False if not.
     */
    public boolean containsStatement(Object mappingKey);



    /**
     * Returns the SQL statement for this object method key. Returns null if no
     * SQL statement is stored for this object method key in this cache.
     * @param mappingKey The object method key to get the SQL statement for.
     * @return The SQL statement for the given object method key. Null if no SQL
     * statement is stored in the cache for the given object method key.
     */
    public String getStatement(Object  mappingKey);



    /**
     * Stores the given string as an SQL statement for the given object method key.
     * @param mappingKey The object method key to store the statement for.
     * @param insertStatement The SQL statement to store.
     */
    public void storeStatement(Object mappingKey, String insertStatement);



    /**
     * Removes the SQL statement stored for this object method key.
     * @param mappingKey The object method to remove the statement for.
     */
    public void removeStatement(Object mappingKey);


    /**
     * Removes all SQL statements stored in this cache.
     */
    public void clear();


    /**
     * Returns the number of SQL statements stored in this cache.
     * @return The number of SQL statements stored in this cache.
     */
    public int size();
}
