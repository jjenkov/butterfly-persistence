package com.jenkov.db.itf;

import java.sql.SQLException;

/**
 * An unchecked version of PersistenceException. Used by the unchecked dao command executor.
 * Usually wraps a PersistenceException.
 *
 * <br/><br/>
 * Call getPersistenceException() to obtain it if there is one. The wrapped PersistenceException
 * may have connection rollback and close exceptions attached.
 * You can also use the shortcut methods getConnectionRollbackException() and getConnectionCloseException().
 *
 *
 * <br/><br/>
 * See PersistenceException for more information.
 *
 *
 * @author Jakob Jenkov, Jenkov Development
 */
public class PersistenceExceptionUnchecked extends RuntimeException{

    public PersistenceExceptionUnchecked(){
    }

    public PersistenceExceptionUnchecked(String msg){
        super(msg);
    }

    public PersistenceExceptionUnchecked(String msg, Throwable throwable){
        super(msg, throwable);
    }

    public PersistenceExceptionUnchecked(Throwable throwable){
        super(throwable);
    }

    /**
     * Returns the wrapped PersistenceException if any.
     * @return The wrapped PersistenceException if any.
     */
    public PersistenceException getPersistenceException(){
        if(getCause() instanceof PersistenceException){
            return (PersistenceException) getCause();
        }
        return null;
    }

    /**
     * If the wrapped exception is a PersistenceException, then this
     * method returns getPersistenceException().getConnectionRollbackException();
     * Else it returns null;
     * @return The connection rollback connection of the wrapped PersistenceException or null.
     */
    public SQLException getConnectionRollbackException(){
        if(getPersistenceException() != null) {
            return getPersistenceException().getConnectionRollbackException();
        }
        return null;
    }

    /**
     * If the wrapped exception is a PersistenceException, then this
     * method returns getPersistenceException().getConnectionCloseException();
     * Else it returns null;
     * @return The connection close connection of the wrapped PersistenceException or null.
     */
    public SQLException getConnectionCloseException(){
        if(getPersistenceException() != null) {
            return getPersistenceException().getConnectionCloseException();
        }
        return null;
    }

}
