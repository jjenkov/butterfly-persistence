package com.jenkov.db.itf.mapping;

import com.jenkov.db.itf.mapping.IObjectMapping;

/**
 * The <code>IObjectMappingCache</code> is a cache for object mappings. The cache
 * is designed to hold different object mappings for the same class. For instance,
 * one object method for reading and one for writing. Also, if you need to
 * read or write only a subset of the object fields for some part of your application,
 * you can store an object method specialized for this purpose.
 * @author Jakob Jenkov,  Jenkov Development
 */
public interface IObjectMappingCache {


    /**
     * Returns true if this cache instance contains an object method for the given mappingKey.
     * @param mappingKey The method key to check if the cache contains an object method for.
     * @return True if the cache contains object mappings for the given method key. False if not.
     */
    public boolean containsObjectMapping(Object mappingKey);


    /**
     * Returns the object-to-database method for the given method key, stored in this object method cache.
     * If no object method is stored in the cache null is returned.
     * @return The object method matching the given method key. Null if no object method was found
     * for the given method key.
     */
    public IObjectMapping getObjectMapping(Object mappingKey);


    /**
     * Stores the given object-to-database method in this object method cache,
     * under the given method key.
     * @param mappingKey The method key to store this object method under.
     * @param mapping The object method to store.
     */
    public void storeObjectMapping(Object mappingKey, IObjectMapping mapping);

    /**
     * Removes the object method stored in this cache for the given method key.
     * @param mappingKey The key to give this particular object method.
     */
    public void removeObjectMapping(Object mappingKey);


    /**
     * Removes all object mappings stored in this cache.
     */
    public void clear();

    /**
     * Returns the number of object mappings stored in this cache.
     * @return The number of object mappings stored in this cache.
     */
    public int size();

}
