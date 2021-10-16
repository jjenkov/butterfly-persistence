package com.jenkov.db.scope;

/**
 * A factory capable of creating IScopeBoundary objects. IScopeBoundary
 * objects can be either ConnectionScope or TransactionScope objects.
 *
 * The default implementation of this IScopeFactory interface is
 * the ScopeFactory class.
 *
 * @author Jakob Jenkov - Copyright 2005 Jenkov Development
 */
public interface IScopeFactory {


    /**
     * Returns the ScopingDataSource used by this IScopeFactory.
     * @return The ScopingDataSource used by this IScopeFactory.
     */
    public ScopingDataSource getScopingDataSource();


    /**
     * Creates a connection scope object.
     * @return A new connection scope object.
     */
    public IScopeBoundary createConnectionScope();


    /**
     * Creates a connection scope object that wraps
     * a connection scope around methods of the scopeTarget
     * object. Only methods that are implementations of
     * interface methods will be wrapped.
     *
     * The returned object implements the same interfaces as the scopeTarget.
     * You can therefore cast the returned object to any of those interfaces.
     * Whenever you call one of the methods on the returned object, which
     * are part of one of the implemented interfaces, a connection scope
     * will be wrapped around the method call. In other words,
     * any opened connections inside the interface method will be reused, and
     * the connection automatically closed afterwards. This will only work
     * if the scopeTarget object obtains it's connections from the
     * ScopingDatasource referenced by this ScopeFactory instance.
     *
     * @param scopeTarget The object to wrap in a connection scope object.
     * @return An object implementing the same interfaces as the scopeTarget,
     *         and has all interface methods wrapped in a connection scope.
     */
    public Object createConnectionScope(Object scopeTarget);


    /**
     * Creates a transaction scope object.
     * @return A new transaction scope object.
     */
    public IScopeBoundary createTransactionScope();

    /**
     * Creates a transaction scope object that wraps
     * a transaction scope around methods of the scopeTarget
     * object. Only methods that are implementations of
     * interface methods will be wrapped.
     *
     * The returned object implements the same interfaces as the scopeTarget.
     * You can therefore cast the returned object to any of those interfaces.
     * Whenever you call one of the methods on the returned object, which
     * are part of one of the implemented interfaces, a transaction scope
     * will be wrapped around the method call. In other words,
     * any opened connections inside the interface method will be reused, and
     * the transaction automatically committed/rolled back, and
     * connection automatically closed afterwards. This will only work
     * if the scopeTarget object obtains it's connections from the
     * ScopingDatasource referenced by this ScopeFactory instance.
     *
     * @param scopeTarget The object to wrap in a connection scope object.
     * @return An object implementing the same interfaces as the scopeTarget,
     *         and has all interface methods wrapped in a connection scope.
     */
    public Object createTransactionScope(Object scopeTarget);




}
