package com.jenkov.db.test.mapping;

import junit.framework.TestCase;
import com.jenkov.db.itf.mapping.*;
import com.jenkov.db.itf.PersistenceException;
import com.jenkov.db.impl.mapping.method.*;
import com.jenkov.db.impl.mapping.ObjectMappingFactory;
import com.jenkov.db.test.objects.PersistentObject;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.io.InputStream;
import java.io.Reader;
import java.sql.*;
import java.util.Date;
import java.net.URL;


/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 21-02-2004
 * Time: 15:18:42
 * To change this template use File | Settings | File Templates.
 */
public class ObjectMappingFactoryTest extends TestCase{

    IObjectMappingFactory mappingFactory = new ObjectMappingFactory();


    public void setUp(){

    }

    public void tearDown(){

    }



    public void testCreateGetterMapping_correctInstanceType() throws Exception{
        assertEquals("Array", ArrayGetterMapping.class,
                mappingFactory.createGetterMapping(Array.class).getClass());
        assertEquals("AsciiStream", AsciiStreamGetterMapping.class,
                mappingFactory.createGetterMapping(AsciiStream.class).getClass());
        assertEquals("Boolean", BooleanGetterMapping.class,
                mappingFactory.createGetterMapping(Boolean.class).getClass());
        assertEquals("boolean", BooleanGetterMapping.class,
                mappingFactory.createGetterMapping(boolean.class).getClass());
        assertEquals("Byte", ByteGetterMapping.class,
                mappingFactory.createGetterMapping(Byte.class).getClass());
        assertEquals("byte", ByteGetterMapping.class,
                mappingFactory.createGetterMapping(byte.class).getClass());
        assertEquals("Byte[]", ByteArrayGetterMapping.class,
                mappingFactory.createGetterMapping(Byte[].class).getClass());
        assertEquals("byte[]", ByteArrayGetterMapping.class,
                mappingFactory.createGetterMapping(byte[].class).getClass());
        assertEquals("Double", DoubleGetterMapping.class,
                mappingFactory.createGetterMapping(Double.class).getClass());
        assertEquals("double", DoubleGetterMapping.class,
                mappingFactory.createGetterMapping(double.class).getClass());
        assertEquals("Float", FloatGetterMapping.class,
                mappingFactory.createGetterMapping(Float.class).getClass());
        assertEquals("float", FloatGetterMapping.class,
                mappingFactory.createGetterMapping(float.class).getClass());
        assertEquals("Integer", IntGetterMapping.class,
                mappingFactory.createGetterMapping(Integer.class).getClass());
        assertEquals("int", IntGetterMapping.class,
                mappingFactory.createGetterMapping(int.class).getClass());
        assertEquals("Long", LongGetterMapping.class,
                mappingFactory.createGetterMapping(Long.class).getClass());
        assertEquals("long", LongGetterMapping.class,
                mappingFactory.createGetterMapping(long.class).getClass());
        assertEquals("Short", ShortGetterMapping.class,
                mappingFactory.createGetterMapping(Short.class).getClass());
        assertEquals("short", ShortGetterMapping.class,
                mappingFactory.createGetterMapping(short.class).getClass());


        assertEquals("BigDecimal", BigDecimalGetterMapping.class,
                mappingFactory.createGetterMapping(BigDecimal.class).getClass());
        assertEquals("InputStream", BinaryStreamGetterMapping.class,
                mappingFactory.createGetterMapping(InputStream.class).getClass());
        assertEquals("BlobMock", BlobGetterMapping.class,
                mappingFactory.createGetterMapping(Blob.class).getClass());
        assertEquals("Reader", CharacterStreamGetterMapping.class,
                mappingFactory.createGetterMapping(Reader.class).getClass());
        assertEquals("Clob", ClobGetterMapping.class,
                mappingFactory.createGetterMapping(Clob.class).getClass());
        assertEquals("Date", DateGetterMapping.class,
                mappingFactory.createGetterMapping(Date.class).getClass());
        assertEquals("Object", ObjectGetterMapping.class,
                mappingFactory.createGetterMapping(Object.class).getClass());
        assertEquals("Ref", RefGetterMapping.class,
                mappingFactory.createGetterMapping(Ref.class).getClass());
        assertEquals("String", StringGetterMapping.class,
                mappingFactory.createGetterMapping(String.class).getClass());
        assertEquals("Time", TimeGetterMapping.class,
                mappingFactory.createGetterMapping(Time.class).getClass());
        assertEquals("Timestamp", TimestampGetterMapping.class,
                mappingFactory.createGetterMapping(Timestamp.class).getClass());
        assertEquals("URL", UrlGetterMapping.class,
                mappingFactory.createGetterMapping(URL.class).getClass());
    }

    public void testCreateGetterMapping() throws Exception{
        IGetterMapping mapping1 = mappingFactory.createGetterMapping(
                PersistentObject.class.getMethod("getId", null), "id", true);

        assertEquals("wrong method",
                PersistentObject.class.getMethod("getId", null), mapping1.getObjectMethod());
        assertEquals("wrong db method name", "id", mapping1.getColumnName());
        assertTrue("should be table mapped", mapping1.isTableMapped());
    }

    public void testCopyGetterMapping() throws Exception{
        IGetterMapping mapping1 = mappingFactory.createGetterMapping(
                PersistentObject.class.getMethod("getId", null), "id", true);

        IGetterMapping mapping2 = mappingFactory.copyGetterMapping(mapping1);

        assertNotSame("should be copy, not same", mapping1, mapping2);
        assertEquals("should be equal after copy", mapping1, mapping2);
    }

    public void testCreateSetterMapping_correctInstanceType() throws Exception{
        assertEquals("Array", ArraySetterMapping.class,
                mappingFactory.createSetterMapping(Array.class).getClass());
        assertEquals("AsciiStream", AsciiStreamSetterMapping.class,
                mappingFactory.createSetterMapping(AsciiStream.class).getClass());
        assertEquals("Boolean", BooleanSetterMapping.class,
                mappingFactory.createSetterMapping(Boolean.class).getClass());
        assertEquals("boolean", BooleanSetterMapping.class,
                mappingFactory.createSetterMapping(boolean.class).getClass());
        assertEquals("Byte", ByteSetterMapping.class,
                mappingFactory.createSetterMapping(Byte.class).getClass());
        assertEquals("byte", ByteSetterMapping.class,
                mappingFactory.createSetterMapping(byte.class).getClass());
        assertEquals("Byte[]", ByteArraySetterMapping.class,
                mappingFactory.createSetterMapping(Byte[].class).getClass());
        assertEquals("byte[]", ByteArraySetterMapping.class,
                mappingFactory.createSetterMapping(byte[].class).getClass());
        assertEquals("Double", DoubleSetterMapping.class,
                mappingFactory.createSetterMapping(Double.class).getClass());
        assertEquals("double", DoubleSetterMapping.class,
                mappingFactory.createSetterMapping(double.class).getClass());
        assertEquals("Float", FloatSetterMapping.class,
                mappingFactory.createSetterMapping(Float.class).getClass());
        assertEquals("float", FloatSetterMapping.class,
                mappingFactory.createSetterMapping(float.class).getClass());
        assertEquals("Integer", IntSetterMapping.class,
                mappingFactory.createSetterMapping(Integer.class).getClass());
        assertEquals("int", IntSetterMapping.class,
                mappingFactory.createSetterMapping(int.class).getClass());
        assertEquals("Long", LongSetterMapping.class,
                mappingFactory.createSetterMapping(Long.class).getClass());
        assertEquals("long", LongSetterMapping.class,
                mappingFactory.createSetterMapping(long.class).getClass());
        assertEquals("Short", ShortSetterMapping.class,
                mappingFactory.createSetterMapping(Short.class).getClass());
        assertEquals("short", ShortSetterMapping.class,
                mappingFactory.createSetterMapping(short.class).getClass());


        assertEquals("BigDecimal", BigDecimalSetterMapping.class,
                mappingFactory.createSetterMapping(BigDecimal.class).getClass());
        assertEquals("InputStream", BinaryStreamSetterMapping.class,
                mappingFactory.createSetterMapping(InputStream.class).getClass());
        assertEquals("BlobMock", BlobSetterMapping.class,
                mappingFactory.createSetterMapping(Blob.class).getClass());
        assertEquals("Reader", CharacterStreamSetterMapping.class,
                mappingFactory.createSetterMapping(Reader.class).getClass());
        assertEquals("Clob", ClobSetterMapping.class,
                mappingFactory.createSetterMapping(Clob.class).getClass());
        assertEquals("Date", DateSetterMapping.class,
                mappingFactory.createSetterMapping(Date.class).getClass());
        assertEquals("Object", ObjectSetterMapping.class,
                mappingFactory.createSetterMapping(Object.class).getClass());
        assertEquals("Ref", RefSetterMapping.class,
                mappingFactory.createSetterMapping(Ref.class).getClass());
        assertEquals("String", StringSetterMapping.class,
                mappingFactory.createSetterMapping(String.class).getClass());
        assertEquals("Time", TimeSetterMapping.class,
                mappingFactory.createSetterMapping(Time.class).getClass());
        assertEquals("Timestamp", TimestampSetterMapping.class,
                mappingFactory.createSetterMapping(Timestamp.class).getClass());
        assertEquals("URL", UrlSetterMapping.class,
                mappingFactory.createSetterMapping(URL.class).getClass());
    }

    public void testCreateSetterMapping() throws Exception{
        ISetterMapping mapping1 = mappingFactory.createSetterMapping(
                PersistentObject.class.getMethod("setId", new Class[]{long.class}), "id", true);

        assertEquals("wrong method",
                PersistentObject.class.getMethod("setId",  new Class[]{long.class}), mapping1.getObjectMethod());
        assertEquals("wrong db method name", "id", mapping1.getColumnName());
        assertTrue("should be table mapped", mapping1.isTableMapped());
    }

    public void testCopySetterMapping() throws Exception{
        ISetterMapping mapping1 = mappingFactory.createSetterMapping(
                PersistentObject.class.getMethod("setId", new Class[]{long.class}), "id", true);

        ISetterMapping mapping2 = mappingFactory.copySetterMapping(mapping1);

        assertNotSame("should be copy, not same", mapping1, mapping2);
        assertEquals("should be equal after copy", mapping1, mapping2);
    }


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
        assertNotNull("getter method not added", getterMapping);
        assertEquals ("wrong getter method",
                PersistentObject.class.getMethod("getId", null), getterMapping.getObjectMethod());
        assertEquals ("wrong column name", "id", getterMapping.getColumnName());
        assertTrue   ("isTableMapped wrong", getterMapping.isTableMapped());

        getterMapping = mapping.getGetterMapping("name");
        assertNotNull("getter method not added", getterMapping);
        assertEquals ("wrong getter method",
                PersistentObject.class.getMethod("getName", null), getterMapping.getObjectMethod());
        assertEquals ("wrong column name", "name", getterMapping.getColumnName());
        assertFalse  ("isTableMapped wrong", getterMapping.isTableMapped());
    }


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
        assertNotNull("getter method not added", getterMapping);
        assertEquals ("wrong getter method",
                PersistentObject.class.getMethod("getId", null), getterMapping.getObjectMethod());
        assertEquals ("wrong column name", "id", getterMapping.getColumnName());
        assertTrue   ("isTableMapped wrong"  , getterMapping.isTableMapped());
        assertTrue   ("isAutoGenerated wrong", getterMapping.isAutoGenerated());

        getterMapping = mapping.getGetterMapping("name");
        assertNotNull("getter method not added", getterMapping);
        assertEquals ("wrong getter method",
                PersistentObject.class.getMethod("getName", null), getterMapping.getObjectMethod());
        assertEquals ("wrong column name", "name", getterMapping.getColumnName());
        assertFalse  ("isTableMapped wrong", getterMapping.isTableMapped());
        assertFalse  ("isAutoGenerated wrong", getterMapping.isAutoGenerated());
    }

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
        assertNotNull("setter method not added", setterMapping);
        assertEquals ("wrong setter method",
                PersistentObject.class.getMethod("setId", new Class[]{long.class}),
                setterMapping.getObjectMethod());
        assertEquals ("wrong column name", "id", setterMapping.getColumnName());
        assertTrue   ("isTableMapped wrong", setterMapping.isTableMapped());

        setterMapping = mapping.getSetterMapping("name");
        assertNotNull("setter method not added", setterMapping);
        assertEquals ("wrong setter method",
                PersistentObject.class.getMethod("setName", new Class[]{String.class}),
                setterMapping.getObjectMethod());
        assertEquals ("wrong column name", "name", setterMapping.getColumnName());
        assertFalse  ("isTableMapped wrong", setterMapping.isTableMapped());
    }

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
        assertNotNull("setter method not added", setterMapping);
        assertEquals ("wrong setter method",
                PersistentObject.class.getMethod("setId", new Class[]{long.class}),
                setterMapping.getObjectMethod());
        assertEquals ("wrong column name", "id", setterMapping.getColumnName());
        assertTrue   ("isTableMapped wrong", setterMapping.isTableMapped());

        setterMapping = mapping.getSetterMapping("name");
        assertNotNull("setter method not added", setterMapping);
        assertEquals ("wrong setter method",
                PersistentObject.class.getMethod("setName", new Class[]{String.class}),
                setterMapping.getObjectMethod());
        assertEquals ("wrong column name", "name", setterMapping.getColumnName());
        assertFalse  ("isTableMapped wrong", setterMapping.isTableMapped());
    }



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
        assertNotNull("setter method not added", setterMapping);
        assertEquals ("wrong setter method",
                PersistentObject.class.getMethod("setId", new Class[]{long.class}),
                setterMapping.getObjectMethod());
        assertEquals ("wrong column name", "id", setterMapping.getColumnName());
        assertTrue   ("isTableMapped wrong", setterMapping.isTableMapped());

        setterMapping = mapping.getSetterMapping("name");
        assertNotNull("setter method not added", setterMapping);
        assertEquals ("wrong setter method",
                PersistentObject.class.getMethod("setName", new Class[]{String.class}),
                setterMapping.getObjectMethod());
        assertEquals ("wrong column name", "name", setterMapping.getColumnName());
        assertFalse  ("isTableMapped wrong", setterMapping.isTableMapped());
    }


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
        assertNotNull("setter method not added", setterMapping);
        assertEquals ("wrong setter method",
                PersistentObject.class.getMethod("setId", new Class[]{long.class}),
                setterMapping.getObjectMethod());
        assertEquals ("wrong column name", "id", setterMapping.getColumnName());
        assertTrue   ("isTableMapped wrong", setterMapping.isTableMapped());

        setterMapping = mapping.getSetterMapping("name");
        assertNotNull("setter method not added", setterMapping);
        assertEquals ("wrong setter method",
                PersistentObject.class.getMethod("setName", new Class[]{String.class}),
                setterMapping.getObjectMethod());
        assertEquals ("wrong column name", "name", setterMapping.getColumnName());
        assertFalse  ("isTableMapped wrong", setterMapping.isTableMapped());
    }




}
