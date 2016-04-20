package com.jenkov.db.test.util;

import com.jenkov.db.util.CollectionUtils;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Jakob Jenkov - Copyright 2005 Jenkov Development
 */
public class CollectionUtilTest extends TestCase{

    public void testToList() throws Exception {
        List list = new ArrayList();
        assertSame(list, CollectionUtils.toList(list));

        Set  set   = new HashSet();
        set.add("test1");
        set.add("test2");
        set.add("test3");

        List list2 = CollectionUtils.toList(set);
        assertEquals("size should be 3", 3, list2.size());
        assertTrue(list2.contains("test1"));
        assertTrue(list2.contains("test2"));
        assertTrue(list2.contains("test3"));
    }

}
