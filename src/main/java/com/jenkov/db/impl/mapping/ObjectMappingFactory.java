package com.jenkov.db.impl.mapping;

import com.jenkov.db.impl.mapping.method.*;
import com.jenkov.db.itf.PersistenceException;
import com.jenkov.db.itf.mapping.*;
import com.jenkov.db.util.ClassUtil;

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * @author Jakob Jenkov
 *         Copyright 2004-2005 Jenkov Development
 */
public class ObjectMappingFactory implements IObjectMappingFactory {

    /*
    public IObjectMappingKey createInstance(Class objectClass) {
        return ObjectMappingKey.createInstance(objectClass);
    }

    public IObjectMappingKey createInstance(Class objectClass, String name) {
        return ObjectMappingKey.createInstance(objectClass, name);
    }

    public IObjectMappingKey createInstance(Class objectClass, ICustomObjectMapper mapper) {
        return ObjectMappingKey.createInstance(objectClass, mapper);
    }

    public IObjectMappingKey createInstance(Class objectClass, String name, ICustomObjectMapper mapper) {
        return ObjectMappingKey.createInstance(objectClass, name, mapper);
    }
    */

    public IObjectMapping createObjectMapping() {
        return new ObjectMapping();
    }

    public IObjectMapping createObjectMapping(Class objectClass, String tableName) {
        IObjectMapping mapping = createObjectMapping();
        mapping.setObjectClass  (objectClass);
        mapping.setTableName    (tableName);
        return mapping;
    }

    public IObjectMapping createObjectMapping(Class objectClass, String tableName, String primaryKeyColumn) {
        IObjectMapping mapping = createObjectMapping();
        mapping.setObjectClass  (objectClass);
        mapping.setTableName    (tableName);
        mapping.getPrimaryKey().addColumn(primaryKeyColumn);
        return mapping;
    }

    public IObjectMapping copyObjectMapping(IObjectMapping source) {
        return null;
    }

    public IGetterMapping createGetterMapping(Class memberType){
        if(Array.class.equals(memberType))         return new ArrayGetterMapping();
        if(AsciiStream  .class.equals(memberType)) return new AsciiStreamGetterMapping();
        if(Boolean      .class.equals(memberType) || boolean.class.equals(memberType))  return new BooleanGetterMapping();
        if(Byte         .class.equals(memberType) || byte.class.equals(memberType))     return new ByteGetterMapping();
        if(Byte[]       .class.equals(memberType) || byte[].class.equals(memberType))   return new ByteArrayGetterMapping();
        if(Double       .class.equals(memberType) || double.class.equals(memberType))   return new DoubleGetterMapping();
        if(Float        .class.equals(memberType) || float.class.equals(memberType))    return new FloatGetterMapping();
        if(Integer      .class.equals(memberType) || int.class.equals(memberType))      return new IntGetterMapping();
        if(Long         .class.equals(memberType) || long.class.equals(memberType))     return new LongGetterMapping();
        if(Short        .class.equals(memberType) || short.class.equals(memberType))    return new ShortGetterMapping();

        if (BigDecimal   .class.equals(memberType)    ) return new BigDecimalGetterMapping();
        if (InputStream  .class.equals(memberType)    ) return new BinaryStreamGetterMapping();
        if (BinaryStream.class.equals(memberType)     )  return new BinaryStreamGetterMapping();
        if (Blob         .class.equals(memberType)    ) return new BlobGetterMapping();
        if (Reader       .class.equals(memberType)    ) return new CharacterStreamGetterMapping();
        if (CharacterStream.class.equals(memberType)  ) return new CharacterStreamGetterMapping();
        if (Clob         .class.equals(memberType)    ) return new ClobGetterMapping();
        if (java.sql.Date.class.equals(memberType)    ) return new SqlDateGetterMapping();
        if (java.util.Date.class.equals(memberType)   ) return new DateGetterMapping();
        if (Calendar.class.equals(memberType)         ) return new CalendarGetterMapping();
        if (GregorianCalendar.class.equals(memberType)) return new CalendarGetterMapping();
        if (Object       .class.equals(memberType)    ) return new ObjectGetterMapping();
        if (Ref          .class.equals(memberType)    ) return new RefGetterMapping();
        if (String       .class.equals(memberType)    ) return new StringGetterMapping();
        if (Time         .class.equals(memberType)    ) return new TimeGetterMapping();
        if (Timestamp    .class.equals(memberType)    ) return new TimestampGetterMapping();
        if (URL          .class.equals(memberType)    ) return new UrlGetterMapping();

        return null;
        //throw new IllegalArgumentException("Member type not supported: " + memberType.getName());
    }

    public IGetterMapping createGetterMapping(Class theClass, String methodName, String columnName) throws PersistenceException {
        return createGetterMapping(theClass, methodName, columnName, true);
    }

    public IGetterMapping createGetterMapping(Class theClass, String methodName, String columnName, boolean isTableMapped) throws PersistenceException {
        try {
            Method method = theClass.getMethod(methodName, null);
            if(method.getReturnType().equals(void.class)){
                throw new IllegalArgumentException("The getter must return a value, not void");
            }
            return createGetterMapping(method, columnName, isTableMapped);
        } catch (NoSuchMethodException e) {
            throw new PersistenceException("Error finding getter for method " + methodName, e);
        }
    }

    public IGetterMapping createGetterMapping(Method member,
                                              String columnName, boolean isTableMapped){
        Class fieldType = getMemberType(member);
        IGetterMapping fieldMapping = createGetterMapping(fieldType);

        if(fieldMapping == null) return null;
        fieldMapping.setObjectMethod(member);
        fieldMapping.setColumnName(columnName);
        fieldMapping.setTableMapped(isTableMapped);

        return fieldMapping;
    }

    public IGetterMapping copyGetterMapping(IGetterMapping source) {
        IGetterMapping copy = createGetterMapping(
                source.getObjectMethod(), source.getColumnName(), source.isTableMapped());

        return copy;
    }

    public ISetterMapping createSetterMapping(Class memberType){
        if(Array.class.equals(memberType))         return new ArraySetterMapping();
        if(AsciiStream  .class.equals(memberType)) return new AsciiStreamSetterMapping();
        if(Boolean      .class.equals(memberType) || boolean.class.equals(memberType))  return new BooleanSetterMapping();
        if(Byte         .class.equals(memberType) || byte.class.equals(memberType))     return new ByteSetterMapping();
        if(Byte[]       .class.equals(memberType) || byte[].class.equals(memberType))   return new ByteArraySetterMapping();
        if(Double       .class.equals(memberType) || double.class.equals(memberType))   return new DoubleSetterMapping();
        if(Float        .class.equals(memberType) || float.class.equals(memberType))    return new FloatSetterMapping();
        if(Integer      .class.equals(memberType) || int.class.equals(memberType))      return new IntSetterMapping();
        if(Long         .class.equals(memberType) || long.class.equals(memberType))     return new LongSetterMapping();
        if(Short        .class.equals(memberType) || short.class.equals(memberType))    return new ShortSetterMapping();

        if(BigDecimal   .class.equals(memberType)) return new BigDecimalSetterMapping();
        if(InputStream  .class.equals(memberType)) return new BinaryStreamSetterMapping();
        if(Blob         .class.equals(memberType)) return new BlobSetterMapping();
        if(Reader       .class.equals(memberType)) return new CharacterStreamSetterMapping();
        if(Clob         .class.equals(memberType)) return new ClobSetterMapping();
        if(java.sql.Date.class.equals(memberType)) return new SqlDateSetterMapping();
        if(java.util.Date.class.equals(memberType)) return new DateSetterMapping();
        if(Calendar.class.equals(memberType)) return new CalendarSetterMapping();
        if(GregorianCalendar.class.equals(memberType)) return new CalendarSetterMapping();
        if(Object       .class.equals(memberType)) return new ObjectSetterMapping();
        if(Ref          .class.equals(memberType)) return new RefSetterMapping();
        if(String       .class.equals(memberType)) return new StringSetterMapping();
        if(Time         .class.equals(memberType)) return new TimeSetterMapping();
        if(Timestamp    .class.equals(memberType)) return new TimestampSetterMapping();
        if(URL          .class.equals(memberType)) return new UrlSetterMapping();

        return null;
//        throw new IllegalArgumentException("member type  " + memberType.getName()
//                + "  was not recognized.");
    }


    public ISetterMapping createSetterMapping(Class methodOwner, String methodName, String columnName) {
        Method setter = findMatchingSetter(methodOwner, methodName);
        return createSetterMapping(setter, columnName, true);
    }


    public ISetterMapping createSetterMapping(Class methodOwner, String methodName, String columnName, boolean isTableMapped) {
        Method setter = findMatchingSetter(methodOwner, methodName);
        return createSetterMapping(setter, columnName, isTableMapped);
    }

    public ISetterMapping createSetterMapping(Class theClass, String methodName, Class parameterType, String columnName) throws PersistenceException {
        return createSetterMapping(theClass, methodName, parameterType, columnName, true);
    }

    public ISetterMapping createSetterMapping(Class theClass, String methodName, Class parameterType, String columnName, boolean isTableMapped) throws PersistenceException {
        try {
            Method method = theClass.getMethod(methodName, new Class[]{parameterType});
            return createSetterMapping(method, columnName, isTableMapped);
        } catch (NoSuchMethodException e) {
            throw new PersistenceException("Error finding setter for method " + methodName, e);
        }
    }


    public ISetterMapping createSetterMapping(Method member, String columnName, boolean isTableMapped){
        Class fieldType = getMemberType(member);
        ISetterMapping fieldMapping = createSetterMapping(fieldType);
        if(fieldMapping == null) return null;
        fieldMapping.setObjectMethod(member);
        fieldMapping.setColumnName(columnName);
        fieldMapping.setTableMapped(isTableMapped);

        return fieldMapping;
    }

    public ISetterMapping copySetterMapping(ISetterMapping source) {
        ISetterMapping copy = createSetterMapping(
                source.getObjectMethod(), source.getColumnName(), source.isTableMapped());

        return copy;
    }

    public void addGetterMapping(IObjectMapping mapping, String methodName,
                                 String columnName, boolean isTableMapped)
    throws NoSuchMethodException, PersistenceException {
        assureObjectClass(mapping);

        Method method = mapping.getObjectClass().getMethod(methodName, null);

        IGetterMapping getterMapping = createGetterMapping(method, columnName, isTableMapped);
        mapping.addGetterMapping(getterMapping);
    }

    public void addGetterMapping(IObjectMapping mapping, String methodName, String columnName,
                                 boolean isTableMapped, boolean isAutoGenerated)
    throws NoSuchMethodException, PersistenceException {

        addGetterMapping(mapping, methodName, columnName, isTableMapped);
        mapping.getGetterMapping(columnName).setAutoGenerated(isAutoGenerated);
    }


    public void addSetterMapping(IObjectMapping mapping, String methodName, String columnName, boolean isTableMapped)
    throws NoSuchMethodException, PersistenceException {
        assureObjectClass(mapping);

        Method[] methods        = mapping.getObjectClass().getMethods();
        Method   method         = null;
        boolean  methodFound    = false;

        for(int i=0; i<methods.length; i++){
            if(methods[i].getName().equals(methodName)){
                methodFound = true;
                method      = methods[i];
                break;
            }
        }

        if(!methodFound){
            throw new NoSuchMethodException("The method " + methodName +
                    "(...) was not found in class " + mapping.getObjectClass());
        }

        mapping.addSetterMapping(createSetterMapping(method, columnName, isTableMapped));
    }


    public void addSetterMapping(IObjectMapping mapping, String methodName, Class parameterType, String columnName, boolean isTableMapped) throws NoSuchMethodException, PersistenceException {
        assureObjectClass(mapping);

        Method method = mapping.getObjectClass().getMethod(methodName, new Class[]{parameterType});

        ISetterMapping setterMapping = createSetterMapping(method, columnName, isTableMapped);
        mapping.addSetterMapping(setterMapping);
    }


    public IKey createKey() {
        return new Key();
    }


    protected Class getMemberType(Method method){
        if(ClassUtil.isGetter(method)){
          Class returnType = method.getReturnType();
          return returnType;
        }
        if(ClassUtil.isSetter(method)){
            return method.getParameterTypes()[0];
        }
        return null;
    }

    protected void assureObjectClass(IObjectMapping mapping) throws PersistenceException{
        if(mapping.getObjectClass() == null){
            throw new PersistenceException("No object class was set on the object method " +
                    "(getObjectClass() returned null)");
        }

    }


    private Method findMatchingSetter(Class methodOwner, String methodName) {
        Method[] methods = methodOwner.getMethods();
        Set matchingSetters = new HashSet();
        for(int i=0; i<methods.length; i++){
            if(methods[i].getName().equals(methodName)){
                if(methods[i].getParameterTypes().length == 1){
                    matchingSetters.add(methods[i]);
                }
            }
        }
        if(matchingSetters.size() > 1){
            throw new IllegalArgumentException("More than one setter was found with the name " + methodName
                    + " in class " + methodOwner + ". Specify parameter type in createSetterMapping call.");
        }
        if(matchingSetters.size() < 1){
            throw new IllegalArgumentException("No setter was found with the name " + methodName
                    + " in class " + methodOwner );
        }

        Method setter = (Method) matchingSetters.iterator().next();
        return setter;
    }

}
