package com.jenkov.db.scope;

import javax.sql.DataSource;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Jakob Jenkov - Copyright 2005 Jenkov Development
 */
public class ScopeFactory implements IScopeFactory{

    protected ScopingDataSource scopingDataSource = null;

    /**
     * Creates a new ScopeFactory. The given DataSource is wrapped
     * internally in a ScopingDataSource instance. You can obtain
     * the ScopingDataSource instance by calling the getScopingDataSource()
     * method.
     * @param dataSource The DataSource to wrap in a ScopingDataSource.
     */
    public ScopeFactory(DataSource dataSource) {
        this.scopingDataSource = new ScopingDataSource(dataSource);
    }

    /**
     * Creates a new ScopeFactory. The given ScopingDataSource
     * is used internally to implement scoping. 
     *
     * @param dataSource The ScopingDataSource to use by this ScopeFactory.
     */
    public ScopeFactory(ScopingDataSource dataSource) {
        this.scopingDataSource = dataSource;
    }

    public ScopingDataSource getScopingDataSource(){
        return this.scopingDataSource;
    }

    public IScopeBoundary createConnectionScope(){
        return new ConnectionScope(this.scopingDataSource);
    }

    public Object createConnectionScope(Object scopeTarget){
        Class[] interfaces = getInterfacesForObject(scopeTarget);

        return java.lang.reflect.Proxy.newProxyInstance(
                getClass().getClassLoader(),
                interfaces,
                new ConnectionScope(this.scopingDataSource, scopeTarget));
    }

    public IScopeBoundary createTransactionScope(){
        return new TransactionScope(this.scopingDataSource);
    }

    public Object createTransactionScope(Object scopeTarget){
        Class[] interfaces = getInterfacesForObject(scopeTarget);

        return java.lang.reflect.Proxy.newProxyInstance(
                getClass().getClassLoader(),
                interfaces,
                new TransactionScope(this.scopingDataSource, scopeTarget));
    }


    /**
     * Returns all interfaces implemented by this objects class. In contrast
     * to the Class.getInterfaces() method this method also includes the
     * interfaces implemented by any of the object class's superclasses.
     * @param object The object to return the interfaces of.
     * @return An array of the interfaces this objects class implements.
     */
    protected static Class[] getInterfacesForObject(Object object){
        Set interfaceSet = new HashSet();

        Class objectClass = object.getClass();
        while(!(Object.class.equals(objectClass))){
            Class[] classInterfaces = objectClass.getInterfaces();
            for (int i = 0; i < classInterfaces.length; i++) {
                interfaceSet.add(classInterfaces[i]);
            }
            objectClass = objectClass.getSuperclass();
        }

        Class[] interfaceArray = new Class[interfaceSet.size()];
        Iterator iterator = interfaceSet.iterator();
        int i = 0;
        while(iterator.hasNext()) interfaceArray[i++] = (Class) iterator.next();
        return interfaceArray;
    }}
