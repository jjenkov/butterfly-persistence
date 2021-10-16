package com.jenkov.db.test.util;

import com.jenkov.db.util.CollectionUtils;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Jakob Jenkov - Copyright 2005 Jenkov Development
 */
public class CollectionUtilTest {

    @Test
    public void testToList() throws Exception {
        List list = new ArrayList();
        assertSame(list, CollectionUtils.toList(list));

        Set  set   = new HashSet();
        set.add("test1");
        set.add("test2");
        set.add("test3");

        List list2 = CollectionUtils.toList(set);
        assertEquals(3, list2.size(), "size should be 3");
        assertTrue(list2.contains("test1"));
        assertTrue(list2.contains("test2"));
        assertTrue(list2.contains("test3"));
    }

}
