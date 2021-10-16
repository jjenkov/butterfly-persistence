package com.jenkov.db.scope;

/**
 * Represents the boundaries of a scope. A scope is started,
 * the scope action executed, and the scope ended. The object
 * returned is the return value of the scope action.
 *
 * @author Jakob Jenkov - Copyright 2005 Jenkov Development
 */
public interface IScopeBoundary {

    /**
     * Executes the given action within this scope.
     * @param action The action to be executed within this scope.
     * @return The return value of the scope action's inScope() method.
     */
    public Object scope(IScopeAction action);
}
