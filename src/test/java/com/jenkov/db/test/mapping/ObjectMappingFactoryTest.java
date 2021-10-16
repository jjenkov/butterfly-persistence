package com.jenkov.db.test.mapping;

import com.jenkov.db.itf.mapping.*;
import com.jenkov.db.itf.PersistenceException;
import com.jenkov.db.impl.mapping.method.*;
import com.jenkov.db.impl.mapping.ObjectMappingFactory;
import com.jenkov.db.test.objects.PersistentObject;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.io.InputStream;
import java.io.Reader;
import java.sql.*;
import java.util.Date;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 21-02-2004
 * Time: 15:18:42
 * To change this template use File | Settings | File Templates.
 */
public class ObjectMappingFactoryTest {

    IObjectMappingFactory mappingFactory = new ObjectMappingFactory();


    public void setUp(){

    }

    public void tearDown(){

    }


    @Test
    public void testCreateGetterMapping_correctInstanceType() throws Exception{
        assertEquals(ArrayGetterMapping.class,
                mappingFactory.createGetterMapping(Array.class).getClass(), "Array");
        assertEquals(AsciiStreamGetterMapping.class,
                mappingFactory.createGetterMapping(AsciiStream.class).getClass(), "AsciiStream");
        assertEquals(BooleanGetterMapping.class,
                mappingFactory.createGetterMapping(Boolean.class).getClass(), "Boolean");
        assertEquals(BooleanGetterMapping.class,
                mappingFactory.createGetterMapping(boolean.class).getClass(), "boolean");
        assertEquals(ByteGetterMapping.class,
                mappingFactory.createGetterMapping(Byte.class).getClass(), "Byte");
        assertEquals(ByteGetterMapping.class,
                mappingFactory.createGetterMapping(byte.class).getClass(), "byte");
        assertEquals(ByteArrayGetterMapping.class,
                mappingFactory.createGetterMapping(Byte[].class).getClass(), "Byte[]");
        assertEquals(ByteArrayGetterMapping.class,
                mappingFactory.createGetterMapping(byte[].class).getClass(), "byte[]");
        assertEquals(DoubleGetterMapping.class,
                mappingFactory.createGetterMapping(Double.class).getClass(), "Double");
        assertEquals(DoubleGetterMapping.class,
                mappingFactory.createGetterMapping(double.class).getClass(), "double");
        assertEquals(FloatGetterMapping.class,
                mappingFactory.createGetterMapping(Float.class).getClass(), "Float");
        assertEquals(FloatGetterMapping.class,
                mappingFactory.createGetterMapping(float.class).getClass(), "float");
        assertEquals(IntGetterMapping.class,
                mappingFactory.createGetterMapping(Integer.class).getClass(), "Integer");
        assertEquals(IntGetterMapping.class,
                mappingFactory.createGetterMapping(int.class).getClass(), "int");
        assertEquals(LongGetterMapping.class,
                mappingFactory.createGetterMapping(Long.class).getClass(), "Long");
        assertEquals(LongGetterMapping.class,
                mappingFactory.createGetterMapping(long.class).getClass(), "long");
        assertEquals(ShortGetterMapping.class,
                mappingFactory.createGetterMapping(Short.class).getClass(), "Short");
        assertEquals(ShortGetterMapping.class,
                mappingFactory.createGetterMapping(short.class).getClass(), "short");


        assertEquals(BigDecimalGetterMapping.class,
                mappingFactory.createGetterMapping(BigDecimal.class).getClass(), "BigDecimal");
        assertEquals(BinaryStreamGetterMapping.class,
                mappingFactory.createGetterMapping(InputStream.class).getClass(), "InputStream");
        assertEquals(BlobGetterMapping.class,
                mappingFactory.createGetterMapping(Blob.class).getClass(), "BlobMock");
        assertEquals(CharacterStreamGetterMapping.class,
                mappingFactory.createGetterMapping(Reader.class).getClass(), "Reader");
        assertEquals(ClobGetterMapping.class,
                mappingFactory.createGetterMapping(Clob.class).getClass(), "Clob");
        assertEquals(DateGetterMapping.class,
                mappingFactory.createGetterMapping(Date.class).getClass(), "Date");
        assertEquals(ObjectGetterMapping.class,
                mappingFactory.createGetterMapping(Object.class).getClass(), "Object");
        assertEquals(RefGetterMapping.class,
                mappingFactory.createGetterMapping(Ref.class).getClass(), "Ref");
        assertEquals(StringGetterMapping.class,
                mappingFactory.createGetterMapping(String.class).getClass(), "String");
        assertEquals(TimeGetterMapping.class,
                mappingFactory.createGetterMapping(Time.class).getClass(), "Time");
        assertEquals(TimestampGetterMapping.class,
                mappingFactory.createGetterMapping(Timestamp.class).getClass(), "Timestamp");
        assertEquals(UrlGetterMapping.class,
                mappingFactory.createGetterMapping(URL.class).getClass(), "URL");
    }

    @Test
    public void testCreateGetterMapping() throws Exception{
        IGetterMapping mapping1 = mappingFactory.createGetterMapping(
                PersistentObject.class.getMethod("getId", null), "id", true);

        assertEquals(PersistentObject.class.getMethod("getId", null), mapping1.getObjectMethod(), "wrong method");
        assertEquals("id", mapping1.getColumnName(), "wrong db method name");
        assertTrue(mapping1.isTableMapped(), "should be table mapped");
    }

    public void testCopyGetterMapping() throws Exception{
        IGetterMapping mapping1 = mappingFactory.createGetterMapping(
                PersistentObject.class.getMethod("getId", null), "id", true);

        IGetterMapping mapping2 = mappingFactory.copyGetterMapping(mapping1);

        assertNotSame(mapping1, mapping2, "should be copy, not same");
        assertEquals(mapping1, mapping2, "should be equal after copy");
    }

    public void testCreateSetterMapping_correctInstanceType() throws Exception{
        assertEquals(ArraySetterMapping.class,
                mappingFactory.createSetterMapping(Array.class).getClass(), "Array");
        assertEquals(AsciiStreamSetterMapping.class,
                mappingFactory.createSetterMapping(AsciiStream.class).getClass(), "AsciiStream");
        assertEquals(BooleanSetterMapping.class,
                mappingFactory.createSetterMapping(Boolean.class).getClass(), "Boolean");
        assertEquals(BooleanSetterMapping.class,
                mappingFactory.createSetterMapping(boolean.class).getClass(), "boolean");
        assertEquals(ByteSetterMapping.class,
                mappingFactory.createSetterMapping(Byte.class).getClass(), "Byte");
        assertEquals(ByteSetterMapping.class,
                mappingFactory.createSetterMapping(byte.class).getClass(), "byte");
        assertEquals(ByteArraySetterMapping.class,
                mappingFactory.createSetterMapping(Byte[].class).getClass(), "Byte[]");
        assertEquals(ByteArraySetterMapping.class,
                mappingFactory.createSetterMapping(byte[].class).getClass(), "byte[]");
        assertEquals(DoubleSetterMapping.class,
                mappingFactory.createSetterMapping(Double.class).getClass(), "Double");
        assertEquals(DoubleSetterMapping.class,
                mappingFactory.createSetterMapping(double.class).getClass(), "double");
        assertEquals(FloatSetterMapping.class,
                mappingFactory.createSetterMapping(Float.class).getClass(), "Float");
        assertEquals(FloatSetterMapping.class,
                mappingFactory.createSetterMapping(float.class).getClass(), "float");
        assertEquals(IntSetterMapping.class,
                mappingFactory.createSetterMapping(Integer.class).getClass(), "Integer");
        assertEquals(IntSetterMapping.class,
                mappingFactory.createSetterMapping(int.class).getClass(), "int");
        assertEquals(LongSetterMapping.class,
                mappingFactory.createSetterMapping(Long.class).getClass(), "Long");
        assertEquals(LongSetterMapping.class,
                mappingFactory.createSetterMapping(long.class).getClass(), "long");
        assertEquals(ShortSetterMapping.class,
                mappingFactory.createSetterMapping(Short.class).getClass(), "Short");
        assertEquals(ShortSetterMapping.class,
                mappingFactory.createSetterMapping(short.class).getClass(), "short");


        assertEquals(BigDecimalSetterMapping.class,
                mappingFactory.createSetterMapping(BigDecimal.class).getClass(), "BigDecimal");
        assertEquals(BinaryStreamSetterMapping.class,
                mappingFactory.createSetterMapping(InputStream.class).getClass(), "InputStream");
        assertEquals(BlobSetterMapping.class,
                mappingFactory.createSetterMapping(Blob.class).getClass(), "BlobMock");
        assertEquals(CharacterStreamSetterMapping.class,
                mappingFactory.createSetterMapping(Reader.class).getClass(), "Reader");
        assertEquals(ClobSetterMapping.class,
                mappingFactory.createSetterMapping(Clob.class).getClass(), "Clob");
        assertEquals(DateSetterMapping.class,
                mappingFactory.createSetterMapping(Date.class).getClass(), "Date");
        assertEquals(ObjectSetterMapping.class,
                mappingFactory.createSetterMapping(Object.class).getClass(), "Object");
        assertEquals(RefSetterMapping.class,
                mappingFactory.createSetterMapping(Ref.class).getClass(), "Ref");
        assertEquals(StringSetterMapping.class,
                mappingFactory.createSetterMapping(String.class).getClass(), "String");
        assertEquals(TimeSetterMapping.class,
                mappingFactory.createSetterMapping(Time.class).getClass(), "Time");
        assertEquals(TimestampSetterMapping.class,
                mappingFactory.createSetterMapping(Timestamp.class).getClass(), "Timestamp");
        assertEquals(UrlSetterMapping.class,
                mappingFactory.createSetterMapping(URL.class).getClass(), "URL");
    }

    @Test
    public void testCreateSetterMapping() throws Exception{
        ISetterMapping mapping1 = mappingFactory.createSetterMapping(
                PersistentObject.class.getMethod("setId", new Class[]{long.class}), "id", true);

        assertEquals(PersistentObject.class.getMethod("setId",  new Class[]{long.class}), mapping1.getObjectMethod(),
                "wrong method");
        assertEquals("id", mapping1.getColumnName(), "wrong db method name");
        assertTrue(mapping1.isTableMapped(), "should be table mapped");
    }

    @Test
    public void testCopySetterMapping() throws Exception{
        ISetterMapping mapping1 = mappingFactory.createSetterMapping(
                PersistentObject.class.getMethod("setId", new Class[]{long.class}), "id", true);

        ISetterMapping mapping2 = mappingFactory.copySetterMapping(mapping1);

        assertNotSame(mapping1, mapping2, "should be copy, not same");
        assertEquals(mapping1, mapping2, "should be equal after copy");
    }

    @Test
    public void testAddGetterMapping() throws Exception{
        IObjectMappingFactory factory = mappingFactory;

        IObjectMapping mapping = factory.createObjectMapping();
        try{
            factory.addGetterMapping(mapping, "getId", "id", true);
            fail("Should fail when no object class set on object method");
        } catch(PersistenceException e){
            //ignore, expected when no object class set on object method.
        } catch (NoSuchMethodException e){
            fail("should throw PersistenceException");
        }
        mapping.setObjectClass(PersistentObject.class);

        factory.addGetterMapping(mapping, "getId"  , "id"  , true);
        factory.addGetterMapping(mapping, "getName", "name", false);

        IGetterMapping getterMapping = mapping.getGetterMapping("id");
        assertNotNull(getterMapping, "getter method not added");
        assertEquals (PersistentObject.class.getMethod("getId", null), getterMapping.getObjectMethod(), "wrong getter method");
        assertEquals ("id", getterMapping.getColumnName(), "wrong column name");
        assertTrue   (getterMapping.isTableMapped(), "isTableMapped wrong");

        getterMapping = mapping.getGetterMapping("name");
        assertNotNull(getterMapping, "getter method not added");
        assertEquals (PersistentObject.class.getMethod("getName", null), getterMapping.getObjectMethod(),
                "wrong getter method");
        assertEquals ("name", getterMapping.getColumnName(), "wrong column name");
        assertFalse  (getterMapping.isTableMapped(), "isTableMapped wrong");
    }

    @Test
    public void testAddGetterMappingAllParameters() throws Exception{
        IObjectMappingFactory factory = mappingFactory;

        IObjectMapping mapping = factory.createObjectMapping();
        try{
            factory.addGetterMapping(mapping, "getId", "id", true, true);
            fail("Should fail when no object class set on object method");
        } catch(PersistenceException e){
            //ignore, expected when no object class set on object method.
        } catch (NoSuchMethodException e){
            fail("should throw PersistenceException");
        }
        mapping.setObjectClass(PersistentObject.class);

        factory.addGetterMapping(mapping, "getId"  , "id"  , true, true);
        factory.addGetterMapping(mapping, "getName", "name", false, false);

        IGetterMapping getterMapping = mapping.getGetterMapping("id");
        assertNotNull(getterMapping, "getter method not added");
        assertEquals (PersistentObject.class.getMethod("getId", null), getterMapping.getObjectMethod(),
                "wrong getter method");
        assertEquals (getterMapping.getColumnName(), "id", "wrong column name");
        assertTrue   (getterMapping.isTableMapped(), "isTableMapped wrong");
        assertTrue   (getterMapping.isAutoGenerated(), "isAutoGenerated wrong");

        getterMapping = mapping.getGetterMapping("name");
        assertNotNull(getterMapping, "getter method not added");
        assertEquals (PersistentObject.class.getMethod("getName", null), getterMapping.getObjectMethod(),
                "wrong getter method");
        assertEquals ("name", getterMapping.getColumnName(), "wrong column name");
        assertFalse  (getterMapping.isTableMapped(), "isTableMapped wrong");
        assertFalse  (getterMapping.isAutoGenerated(), "isAutoGenerated wrong");
    }

    @Test
    public void testAddSetterMapping() throws Exception{
        IObjectMappingFactory factory = mappingFactory;

        IObjectMapping mapping = factory.createObjectMapping();
        try{
            factory.addSetterMapping(mapping, "setId", "id", true);
            fail("Should fail when no object class set on object method");
        } catch(PersistenceException e){
            //ignore, expected when no object class set on object method.
        } catch (NoSuchMethodException e){
            fail("should throw PersistenceException");
        }
        mapping.setObjectClass(PersistentObject.class);

        factory.addSetterMapping(mapping, "setId"  , "id"  , true);
        factory.addSetterMapping(mapping, "setName", "name", false);

        ISetterMapping setterMapping = mapping.getSetterMapping("id");
        assertNotNull(setterMapping, "setter method not added");
        assertEquals (PersistentObject.class.getMethod("setId", new Class[]{long.class}),
                setterMapping.getObjectMethod(),
                "wrong setter method");
        assertEquals ("id", setterMapping.getColumnName(), "wrong column name");
        assertTrue   (setterMapping.isTableMapped(), "isTableMapped wrong");

        setterMapping = mapping.getSetterMapping("name");
        assertNotNull(setterMapping, "setter method not added");
        assertEquals (PersistentObject.class.getMethod("setName", new Class[]{String.class}),
                setterMapping.getObjectMethod(),
                "wrong setter method");
        assertEquals ("name", setterMapping.getColumnName(), "wrong column name");
        assertFalse  (setterMapping.isTableMapped(), "isTableMapped wrong");
    }

    @Test
    public void testAddSetterMappingAllParametersNoParameterType() throws Exception{
        IObjectMappingFactory factory = mappingFactory;

        IObjectMapping mapping = factory.createObjectMapping();
        try{
            factory.addSetterMapping(mapping, "setId", "id", true);
            fail("Should fail when no object class set on object method");
        } catch(PersistenceException e){
            //ignore, expected when no object class set on object method.
        } catch (NoSuchMethodException e){
            fail("should throw PersistenceException");
        }
        mapping.setObjectClass(PersistentObject.class);

        factory.addSetterMapping(mapping, "setId"  ,  "id"  , true);
        factory.addSetterMapping(mapping, "setName",  "name", false);

        ISetterMapping setterMapping = mapping.getSetterMapping("id");
        assertNotNull(setterMapping, "setter method not added");
        assertEquals (
                PersistentObject.class.getMethod("setId", new Class[]{long.class}),
                setterMapping.getObjectMethod(),
                "wrong setter method");
        assertEquals ("id", setterMapping.getColumnName(), "wrong column name");
        assertTrue   (setterMapping.isTableMapped(), "isTableMapped wrong");

        setterMapping = mapping.getSetterMapping("name");
        assertNotNull(setterMapping, "setter method not added");
        assertEquals (
                PersistentObject.class.getMethod("setName", new Class[]{String.class}),
                setterMapping.getObjectMethod(),
                "wrong setter method");
        assertEquals ("name", setterMapping.getColumnName(), "wrong column name");
        assertFalse  (setterMapping.isTableMapped(), "isTableMapped wrong");
    }


    @Test
    public void testAddSetterMappingWithParameterType() throws Exception{
        IObjectMappingFactory factory = mappingFactory;

        IObjectMapping mapping = factory.createObjectMapping();
        try{
            factory.addSetterMapping(mapping, "setId", long.class, "id", true);
            fail("Should fail when no object class set on object method");
        } catch(PersistenceException e){
            //ignore, expected when no object class set on object method.
        } catch (NoSuchMethodException e){
            fail("should throw PersistenceException");
        }
        mapping.setObjectClass(PersistentObject.class);

        factory.addSetterMapping(mapping, "setId"  , long.class,   "id"  , true);
        factory.addSetterMapping(mapping, "setName", String.class, "name", false);

        ISetterMapping setterMapping = mapping.getSetterMapping("id");
        assertNotNull(setterMapping, "setter method not added");
        assertEquals (PersistentObject.class.getMethod("setId", new Class[]{long.class}),
                setterMapping.getObjectMethod(),
                "wrong setter method");
        assertEquals ("id", setterMapping.getColumnName(), "wrong column name");
        assertTrue   (setterMapping.isTableMapped(), "isTableMapped wrong");

        setterMapping = mapping.getSetterMapping("name");
        assertNotNull(setterMapping, "setter method not added");
        assertEquals (
                PersistentObject.class.getMethod("setName", new Class[]{String.class}),
                setterMapping.getObjectMethod(),
                "wrong setter method");
        assertEquals ("name", setterMapping.getColumnName(), "wrong column name");
        assertFalse  (setterMapping.isTableMapped(), "isTableMapped wrong");
    }

    @Test
    public void testAddSetterMappingAllParametersWithParameterType() throws Exception{
        IObjectMappingFactory factory = mappingFactory;

        IObjectMapping mapping = factory.createObjectMapping();
        try{
            factory.addSetterMapping(mapping, "setId", long.class, "id", true);
            fail("Should fail when no object class set on object method");
        } catch(PersistenceException e){
            //ignore, expected when no object class set on object method.
        } catch (NoSuchMethodException e){
            fail("should throw PersistenceException");
        }
        mapping.setObjectClass(PersistentObject.class);

        factory.addSetterMapping(mapping, "setId"  , long.class,   "id"  , true);
        factory.addSetterMapping(mapping, "setName", String.class, "name", false);

        ISetterMapping setterMapping = mapping.getSetterMapping("id");
        assertNotNull(setterMapping, "setter method not added");
        assertEquals (
                PersistentObject.class.getMethod("setId", new Class[]{long.class}),
                setterMapping.getObjectMethod(),
                "wrong setter method"
                );
        assertEquals ("id", setterMapping.getColumnName(), "wrong column name");
        assertTrue   (setterMapping.isTableMapped(), "isTableMapped wrong");

        setterMapping = mapping.getSetterMapping("name");
        assertNotNull(setterMapping, "setter method not added");
        assertEquals (
                PersistentObject.class.getMethod("setName", new Class[]{String.class}),
                setterMapping.getObjectMethod(),
                "wrong setter method"
                );
        assertEquals ("name", setterMapping.getColumnName(), "wrong column name");
        assertFalse  (setterMapping.isTableMapped(), "isTableMapped wrong");
    }




}
