package com.jenkov.db.scope;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * An implementation of the IScopeBoundary that can mark
 * the boundaries of a connection scope. Users of the scoping features
 * will not use this class directly. It is used by the ScopeFactory class.
 * 
 * @author Jakob Jenkov - Copyright 2005 Jenkov Development
 */
public class ConnectionScope implements IScopeBoundary, IScopeAction, InvocationHandler {

    protected ScopingDataSource dataSource  = null;
    protected Object            scopeTarget = null;

    public ConnectionScope(ScopingDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public ConnectionScope(ScopingDataSource dataSource, Object scopeTarget) {
        this.dataSource = dataSource;
        this.scopeTarget = scopeTarget;
    }

    public Object scope(){
        return scope(this);
    }

    public Object scope(IScopeAction scopeAction){
        Object returnValue = null;
        try{
            this.dataSource.beginConnectionScope();
            returnValue = scopeAction.inScope();
        } catch(Throwable t){
            this.dataSource.endConnectionScope(t);
        }
        this.dataSource.endConnectionScope();
        return returnValue;
    }

    ///CLOVER:OFF
    public Object inScope() throws Throwable{
        return null;
    }
    ///CLOVER:ON

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object returnValue = null;

        if(method.getName().equals("toString") || method.getName().equals("hashCode") || method.getName().equals("getClass")){
            return method.invoke(this.scopeTarget, args);
        }
        try{
            this.dataSource.beginConnectionScope();
            returnValue = method.invoke(this.scopeTarget, args);
        } catch(Throwable t){
            this.dataSource.endConnectionScope(t);
        }
        this.dataSource.endConnectionScope();
        return returnValue;
    }
}
