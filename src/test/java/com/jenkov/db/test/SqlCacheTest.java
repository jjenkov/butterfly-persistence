package com.jenkov.db.test;

import com.jenkov.db.impl.SqlCache;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Jakob Jenkov - Copyright 2004-2006 Jenkov Development
 */
public class SqlCacheTest {


    @Test
    public void test() throws Exception {
        SqlCache cache = new SqlCache();

        String key = "key";

        cache.storeStatement(key, "test");
        assertTrue(cache.containsStatement(key));
        assertEquals("test", cache.getStatement(key));

        assertEquals(1, cache.size());

        cache.removeStatement(key);
        assertEquals(0, cache.size());

        assertFalse(cache.containsStatement(key));
        assertNull(cache.getStatement(key));
    }
}
