package com.jenkov.db.itf;

import java.sql.SQLException;

/**
 * This exception is the super type of all exceptions thrown in Butterfly Persistence. At present
 * it is also the only exception thrown anywhere in the Butterfly Persistence API, but we
 * may later throw subclasses so you can catch and handle the various exceptions more
 * precisely. The interface won't be changed though, and still only throw this super
 * type, PersistenceException.
 *
 * Like any other exception PersistenceException can have a root cause (an exception)
 * attached to it. In addition to this normal root cause a PersistenceException can have
 * two other exceptions attached to it:
 *
 * <ol>
 *    <li>A connection rollback exception
 *        If an exception is thrown during execution of a dao command in a transaction,
 *        and the connection.rollback() method throws an exception too, then the
 *        exception thrown by connection.rollback() is attached.
 *    </li>
 *    <li>A connection close exception
 *        If an exception is thrown from a dao command, and the connection.close()
 *        call throws an exception too, then the exception thrown from the connection.close()
 *        method is attached.
 *    </li>
 * </ol>
 *
 * If an exception is thrown during connection.close() and no exception has been thrown earlier,
 * then a PersistenceException will be thrown. This PersistenceException will not have a close
 * exception attached since it is itself the close exception. This situation cannot occur
 * with the rollback exception. The transaction is only rolled back if an exception is thrown
 * during dao command execution, so there will always be a prior exception.
 *
 *
 * @author Jakob Jenkov, Jenkov Development
 */
public class PersistenceException extends Exception{

    protected SQLException connectionRollbackException = null;
    protected SQLException connectionCloseException    = null;

    public PersistenceException(){
    }

    public PersistenceException(String msg){
        super(msg);
    }

    public PersistenceException(String msg, Throwable throwable){
        super(msg, throwable);
    }

    public PersistenceException(Throwable throwable){
        super(throwable);
    }

    public SQLException getConnectionRollbackException() {
        return connectionRollbackException;
    }

    public void setConnectionRollbackException(SQLException connectionRollbackException) {
        this.connectionRollbackException = connectionRollbackException;
    }

    public SQLException getConnectionCloseException() {
        return connectionCloseException;
    }

    public void setConnectionCloseException(SQLException connectionCloseException) {
        this.connectionCloseException = connectionCloseException;
    }

}
