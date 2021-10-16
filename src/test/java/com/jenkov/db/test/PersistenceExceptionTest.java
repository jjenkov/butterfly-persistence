package com.jenkov.db.test;

import com.jenkov.db.itf.PersistenceException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Jakob Jenkov - Copyright 2005 Jenkov Development
 */
public class PersistenceExceptionTest{


    public void testConstructors(){

        PersistenceException exception = new PersistenceException();

        assertNull(exception.getCause());
        assertNull(exception.getMessage());

        PersistenceException exception2 = new PersistenceException("test message");
        assertEquals("test message", exception2.getMessage());
        exception = new PersistenceException("cause", exception2);
        assertSame(exception2, exception.getCause());
        assertEquals("cause", exception.getMessage());

        exception = new PersistenceException(exception2);
        assertSame(exception2, exception.getCause());
        assertEquals("com.jenkov.db.itf.PersistenceException: test message", exception.getMessage());
    }
}
