package com.jenkov.db.itf.mapping;

import com.jenkov.db.itf.mapping.IObjectMapping;

/**
 * The <code>IObjectMappingCache</code> is a cache for object mappings. The cache
 * is designed to hold different object mappings for the same class. For instance,
 * one object mapping for reading and one for writing. Also, if you need to
 * read or write only a subset of the object fields for some part of your application,
 * you can store an object mapping specialized for this purpose.
 * @author Jakob Jenkov,  Jenkov Development
 */
public interface IObjectMappingCache {


    /**
     * Returns true if this cache instance contains an object mapping for the given mappingKey.
     * @param mappingKey The object mapping key to check if the cache contains an object mapping for.
     * @return True if the cache contains object mappings for the given method key. False if not.
     */
    public boolean containsObjectMapping(Object mappingKey);


    /**
     * Returns the object-to-database method for the given method key, stored in this object mapping cache.
     * If no object mapping is stored in the cache null is returned.
     * @param mappingKey The object mapping key to check if the cache contains an object mapping for.
     * @return The object mapping matching the given method key. Null if no object mapping was found
     * for the given method key.
     */
    public IObjectMapping getObjectMapping(Object mappingKey);


    /**
     * Stores the given object-to-database method in this object mapping cache,
     * under the given mapping key.
     * @param mappingKey The method key to store this object mapping under.
     * @param mapping The object mapping to store.
     */
    public void storeObjectMapping(Object mappingKey, IObjectMapping mapping);

    /**
     * Removes the object mapping stored in this cache for the given method key.
     * @param mappingKey The key to give this particular object mapping.
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
