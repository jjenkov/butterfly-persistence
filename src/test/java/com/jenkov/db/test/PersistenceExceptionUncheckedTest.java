package com.jenkov.db.test;

import com.jenkov.db.itf.PersistenceExceptionUnchecked;
import com.jenkov.db.itf.PersistenceException;
import junit.framework.TestCase;

import java.sql.SQLException;

/**
 * @author Jakob Jenkov - Copyright 2005 Jenkov Development
 */
public class PersistenceExceptionUncheckedTest extends TestCase {

    public void testConstructors(){
        PersistenceExceptionUnchecked unchecked = new PersistenceExceptionUnchecked();
        assertNull(unchecked.getMessage());
        assertNull(unchecked.getCause());
        assertNull(unchecked.getConnectionRollbackException());
        assertNull(unchecked.getConnectionCloseException());

        unchecked = new PersistenceExceptionUnchecked("some message");
        assertEquals("some message", unchecked.getMessage());
        assertNull(unchecked.getCause());
        assertNull(unchecked.getConnectionRollbackException());
        assertNull(unchecked.getConnectionCloseException());

        PersistenceException cause = new PersistenceException();
        unchecked = new PersistenceExceptionUnchecked("some message", cause);
        assertEquals("some message", unchecked.getMessage());
        assertSame(cause, unchecked.getCause());
        assertNull(unchecked.getConnectionRollbackException());
        assertNull(unchecked.getConnectionCloseException());

        cause = new PersistenceException();
        unchecked = new PersistenceExceptionUnchecked(cause);
        assertEquals("com.jenkov.db.itf.PersistenceException", unchecked.getMessage());
        assertSame(cause, unchecked.getCause());
        assertNull(unchecked.getConnectionRollbackException());
        assertNull(unchecked.getConnectionCloseException());

        cause = new PersistenceException();
        SQLException closeException    = new SQLException();
        SQLException rollbackException = new SQLException();
        cause.setConnectionCloseException(closeException);
        cause.setConnectionRollbackException(rollbackException);
        unchecked = new PersistenceExceptionUnchecked(cause);
        assertEquals("com.jenkov.db.itf.PersistenceException", unchecked.getMessage());
        assertSame(cause, unchecked.getCause());
        assertSame(rollbackException, unchecked.getConnectionRollbackException());
        assertSame(closeException, unchecked.getConnectionCloseException());


    }
}
