package com.jenkov.db.test.mapping;

import com.jenkov.db.impl.mapping.KeyValue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Jakob Jenkov - Copyright 2005 Jenkov Development
 */
public class KeyValueTest {

    @Test
    public void testToString(){
        KeyValue keyValue = new KeyValue();
        keyValue.addColumnValue("one", new Integer(1));
        keyValue.addColumnValue("two", "2.2");

        assertEquals("(one = 1, two = 2.2)", keyValue.toString());
    }
}
