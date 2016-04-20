package com.jenkov.db.test.mapping;

import junit.framework.TestCase;
import com.jenkov.db.impl.mapping.ObjectMappingCache;
import com.jenkov.db.impl.mapping.ObjectMapping;
import com.jenkov.db.itf.mapping.IObjectMapping;

/**
 * @author Jakob Jenkov - Copyright 2005 Jenkov Development
 */
public class ObjectMappingCacheTest extends TestCase {

    public void testAddGetRemoveObjectMapping(){
        ObjectMappingCache cache = new ObjectMappingCache();
        IObjectMapping mapping1 = new ObjectMapping();
        IObjectMapping mapping2 = new ObjectMapping();

        assertEquals(0, cache.size());
        cache.storeObjectMapping("1", mapping1);
        cache.storeObjectMapping("2", mapping2);
        assertEquals(2, cache.size());

        assertSame(mapping1, cache.getObjectMapping("1"));
        assertSame(mapping2, cache.getObjectMapping("2"));

        cache.removeObjectMapping("1");
        assertNull(cache.getObjectMapping("1"));
        cache.removeObjectMapping("2");
        assertNull(cache.getObjectMapping("2"));
        assertEquals(0, cache.size());
    }
}
