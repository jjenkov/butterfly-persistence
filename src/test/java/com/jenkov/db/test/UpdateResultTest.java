package com.jenkov.db.test;

import com.jenkov.db.itf.UpdateResult;
import junit.framework.TestCase;

import java.math.BigDecimal;

/**
 * @author Jakob Jenkov - Copyright 2005 Jenkov Development
 */
public class UpdateResultTest extends TestCase {

    public void testAddGetGeneratedKeys(){
        UpdateResult result = new UpdateResult();
        result.addGeneratedKey("123");
        assertEquals(1, result.getGeneratedKeys().size());
        assertEquals("123", result.getGeneratedKeys().get(0));

        assertEquals(123L, result.getGeneratedKeyAsLong(0));
        assertEquals(new BigDecimal(123), result.getGeneratedKeyAsBigDecimal(0));
    }

    public void testAddGetGeneratedKeysAsNumbers(){
        UpdateResult result = new UpdateResult();
        result.addGeneratedKey(new Long(123));

        assertEquals(123L, result.getGeneratedKeyAsLong(0));
        assertEquals(new BigDecimal(123), result.getGeneratedKeyAsBigDecimal(0));

        result.addGeneratedKey(new BigDecimal(999));
        assertEquals(999L, result.getGeneratedKeyAsLong(1));
        assertEquals(new BigDecimal(999), result.getGeneratedKeyAsBigDecimal(1));


    }
}
