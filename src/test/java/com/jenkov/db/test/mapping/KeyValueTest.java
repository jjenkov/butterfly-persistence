package com.jenkov.db.test.mapping;

import junit.framework.TestCase;
import com.jenkov.db.impl.mapping.KeyValue;

/**
 * @author Jakob Jenkov - Copyright 2005 Jenkov Development
 */
public class KeyValueTest extends TestCase {

    public void testToString(){
        KeyValue keyValue = new KeyValue();
        keyValue.addColumnValue("one", new Integer(1));
        keyValue.addColumnValue("two", "2.2");

        assertEquals("(one = 1, two = 2.2)", keyValue.toString());
    }
}
