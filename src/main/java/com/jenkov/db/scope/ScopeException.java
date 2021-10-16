package com.jenkov.db.scope;

/**
 * Exceptions of this type are thrown from either connection or transaction
 * scopes, if an exception is thrown inside the scope. The exception thrown
 * inside the scope is wrapped in a ScopeException and rethrown, once the
 * scope object has closed the connection correctly and/or committed/rolled back
 * the transaction.
 *
 * @author Jakob Jenkov - Copyright 2005 Jenkov Development
 */
public class ScopeException extends RuntimeException {

    protected Throwable closeException              = null;
    protected Throwable commitException             = null;
    protected Throwable rollbackException           = null;
    protected Throwable setAutoCommitFalseException = null;

    public ScopeException() {
    }

    public ScopeException(String message) {
        super(message);
    }

    public ScopeException(Throwable cause) {
        super(cause);
    }

    public ScopeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ScopeException(Throwable cause, Throwable commitException, Throwable rollbackException, Throwable setAutoCommitFalseException, Throwable closeException){
        super(cause);
        this.commitException             = commitException;
        this.rollbackException           = rollbackException;
        this.closeException              = closeException;
        this.setAutoCommitFalseException = setAutoCommitFalseException;
    }

    public ScopeException(String message, Throwable cause, Throwable commitException, Throwable rollbackException, Throwable setAutoCommitFalseException, Throwable closeException){
        super(message, cause);
        this.commitException             = commitException;
        this.rollbackException           = rollbackException;
        this.closeException              = closeException;
        this.setAutoCommitFalseException = setAutoCommitFalseException;
    }


    /**
     * Returns the root cause of this ScopeException.
     * @return The cause of this ScopeException
     */
    public Throwable getCause() {
        if (super.getCause()                 != null) return super.getCause();
        if (this.commitException             != null) return commitException;
        if (this.rollbackException           != null) return this.rollbackException;
        if (this.setAutoCommitFalseException != null) return this.setAutoCommitFalseException;
        if (this.closeException              != null) return this.closeException;
        return null;
    }

    /**
     * Returns the exception thrown when closing the connection.
     * If no exception was thrown when closing the connection, null is returned.
     * @return The exception thrown when closing the connection, or null.
     */
    public Throwable getCloseException() {
        return closeException;
    }

    /**
     * Returns the exception thrown when committing the transaction.
     * If no exception was thrown when committing the transaction, null is returned.
     * @return The exception thrown when committing the transaction, or null.
     */
    public Throwable getCommitException() {
        return commitException;
    }

    /**
     * Returns the exception thrown when rolling back the transaction.
     * If no exception was thrown when rolling back the transaction, null is returned.
     * @return The exception thrown when rolling back the transaction, or null.
     */
    public Throwable getRollbackException() {
        return rollbackException;
    }

    /**
     * Returns the exception thrown when calling setAutoCommit(false) on the connection.
     * If no exception was thrown when calling setAutoCommit(false) on the connection, null is returned.
     * @return The exception thrown when calling setAutoCommit(false) on the connection, or null.
     */
    public Throwable getSetAutoCommitFalseException() {
        return setAutoCommitFalseException;
    }

    public String toString(){
        StringBuffer buffer = new StringBuffer();
        buffer.append(getClass().getName());
        if(getMessage() != null) buffer.append(": "  + getMessage());
        if(getCause() != null)   buffer.append(" - Caused By: " + getCause().toString());
        return buffer.toString();
    }
}
