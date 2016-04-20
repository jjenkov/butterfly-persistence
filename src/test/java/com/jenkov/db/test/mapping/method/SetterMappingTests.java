package com.jenkov.db.test.mapping.method;

import com.jenkov.db.impl.mapping.method.*;
import com.jenkov.testing.mock.impl.MethodInvocation;
import com.jenkov.testing.mock.impl.MockFactory;
import com.jenkov.testing.mock.itf.IMock;
import junit.framework.TestCase;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;

/**
 * @author Jakob Jenkov - Copyright 2005 Jenkov Development
 */
public class SetterMappingTests extends TestCase{

    public void testByteArrraySetterMappiing() throws Exception {
        ByteArraySetterMapping mapping = new ByteArraySetterMapping();
        mapping.setObjectMethod(DataObject.class.getMethod("setByteArray", new Class[]{byte[].class}));

        ResultSet result = (ResultSet) MockFactory.createProxy(ResultSet.class);
        IMock mock = MockFactory.getMock(result);
        byte[] bytes = new byte[]{(byte) 1, (byte) 2, (byte) 3};
        mock.addReturnValue(bytes);

        DataObject dataObject = new DataObject();
        assertNull(dataObject.getByteArray());

        mapping.insertValueIntoObject(dataObject, result);
        assertSame(bytes, dataObject.getByteArray());
    }

    public void testAsciiStreamSetterMapping() throws Exception {
        AsciiStreamSetterMapping      mapping = new AsciiStreamSetterMapping();
        mapping.setObjectMethod(DataObject.class.getMethod("setAsciiStream", new Class[] {AsciiStream.class}));

        ResultSet result = (ResultSet) MockFactory.createProxy(ResultSet.class);
        IMock mock = (IMock) result;
        InputStream input = new ByteArrayInputStream(new byte[0]);
        mock.addReturnValue(input);

        DataObject dataObject = new DataObject();
        mapping.insertValueIntoObject(dataObject, result);
        //System.out.println(mock.getInvocations());
        mock.assertInvoked(new MethodInvocation("getAsciiStream", new Class[]{String.class}, new Object[]{null}));
        assertEquals("wrong value set on data object", new AsciiStream(input), dataObject.getAsciiStream());
    }


    public void testBigDecimalSetterMapping() throws Exception {
        BigDecimalSetterMapping      mapping = new BigDecimalSetterMapping();
        mapping.setObjectMethod(DataObject.class.getMethod("setBigDecimal", new Class[] {BigDecimal.class}));

        ResultSet result = (ResultSet) MockFactory.createProxy(ResultSet.class);
        IMock Mock = (IMock) result;
        Mock.addReturnValue(new BigDecimal(12));


        DataObject dataObject = new DataObject();
        mapping.insertValueIntoObject(dataObject, result);
        //System.out.println(Mock.getInvocations());
        Mock.assertInvoked(new MethodInvocation("getBigDecimal", new Class[]{String.class}, new Object[]{null}));
        assertEquals("wrong value set on data object", new BigDecimal(12), dataObject.getBigDecimal());
    }

    public void testBinaryStreamSetterMapping() throws Exception {
        BinaryStreamSetterMapping      mapping = new BinaryStreamSetterMapping();
        mapping.setObjectMethod(DataObject.class.getMethod("setBinaryStream", new Class[] {BinaryStream.class}));

        ResultSet result = (ResultSet) MockFactory.createProxy(ResultSet.class);
        IMock resultMock = (IMock) result;
        InputStream input = new ByteArrayInputStream(new byte[0]);
        resultMock.addReturnValue(input);


        DataObject dataObject = new DataObject();
        mapping.insertValueIntoObject(dataObject, result);
        //System.out.println(Mock.getInvocations());
        resultMock.assertInvoked(new MethodInvocation("getBinaryStream", new Class[]{String.class}, new Object[]{null}));
        assertEquals("wrong value set on data object", new BinaryStream(input), dataObject.getBinaryStream());
    }



    public void testBlobSetterMapping() throws Exception {
        BlobSetterMapping      mapping = new BlobSetterMapping();
        mapping.setObjectMethod(DataObject.class.getMethod("setBlob", new Class[] {Blob.class}));

        ResultSet result = (ResultSet) MockFactory.createProxy(ResultSet.class);
        IMock resultMock = (IMock) result;
        BlobMock blob = new BlobMock();
        resultMock.addReturnValue(blob);

        DataObject dataObject = new DataObject();
        mapping.insertValueIntoObject(dataObject, result);
        //System.out.println(resultMock.getInvocations());
        resultMock.assertInvoked(new MethodInvocation("getBlob", new Class[]{String.class}, new Object[]{null}));
        assertSame("wrong value set on data object", blob, dataObject.getBlob());
    }

    public void testBooleanSetterMapping() throws Exception {
        BooleanSetterMapping      mapping = new BooleanSetterMapping();
        mapping.setObjectMethod(DataObject.class.getMethod("setBoolean", new Class[] {boolean.class}));
        mapping.setColumnType(java.sql.Types.BOOLEAN);

        ResultSet result = (ResultSet) MockFactory.createProxy(ResultSet.class);
        IMock resultMock = (IMock) result;
        resultMock.addReturnValue(new Boolean(true));

        DataObject dataObject = new DataObject();
        mapping.insertValueIntoObject(dataObject, result);
        //System.out.println(Mock.getInvocations());
        resultMock.assertInvoked(new MethodInvocation("getBoolean", new Class[]{String.class}, new Object[]{null}));
        assertEquals("wrong value set on data object", true, dataObject.isBoolean());

        resultMock.clear();
        mapping.insertValueIntoObject(dataObject, result);
        resultMock.assertInvoked(new MethodInvocation("getObject", new Class[]{String.class}, new Object[]{null}));
        resultMock.assertNotInvoked(new MethodInvocation("getBoolean", new Class[]{String.class}, new Object[]{null}));
        assertEquals(1, resultMock.getInvocations().size());

        resultMock.clear();
        mapping.setColumnType(java.sql.Types.INTEGER);
        resultMock.addReturnValue(new Integer(1));
        resultMock.addReturnValue(new Integer(1));
        mapping.insertValueIntoObject(dataObject, result);
        assertTrue(dataObject.isBoolean());
//        System.out.println(resultMock.getInvocations());
        resultMock.assertInvoked(new MethodInvocation("getObject", new Class[]{String.class}, new Object[]{null}));
        resultMock.assertInvoked(new MethodInvocation("getInt"   , new Class[]{String.class}, new Object[]{null}));

    }

    public void testByteSetterMapping() throws Exception {
        ByteSetterMapping      mapping = new ByteSetterMapping();
        mapping.setObjectMethod(DataObject.class.getMethod("setByteData", new Class[] {byte.class}));

        ResultSet result = (ResultSet) MockFactory.createProxy(ResultSet.class);
        IMock resultMock = MockFactory.getMock(result);
        resultMock.addReturnValue(new Byte((byte)25));
        resultMock.addReturnValue(new Byte((byte)25));

        DataObject dataObject = new DataObject();
        mapping.insertValueIntoObject(dataObject, result);
//        System.out.println(resultMock.getInvocations());
        resultMock.assertInvoked(new MethodInvocation("getByte", new Class[]{String.class}, new Object[]{null}));
        assertEquals("wrong value set on data object", 25, dataObject.getByteData());

        resultMock.clear();
        mapping.insertValueIntoObject(dataObject, result);
        resultMock.assertInvoked(new MethodInvocation("getObject", new Class[]{String.class}, new Object[]{null}));
        resultMock.assertNotInvoked(new MethodInvocation("getByte", new Class[]{String.class}, new Object[]{null}));
        assertEquals(1, resultMock.getInvocations().size());
    }

    public void testCharacterStreamSetterMapping() throws Exception {
        CharacterStreamSetterMapping      mapping = new CharacterStreamSetterMapping();
        mapping.setObjectMethod(DataObject.class.getMethod("setCharacterStream", new Class[] {CharacterStream.class}));

        ResultSet result = (ResultSet) MockFactory.createProxy(ResultSet.class);
        IMock resultMock = (IMock) result;
        Reader reader = new StringReader("testString");
        resultMock.addReturnValue(reader);

        DataObject dataObject = new DataObject();
        mapping.insertValueIntoObject(dataObject, result);
        //System.out.println(resultMock.getInvocations());
        resultMock.assertInvoked(new MethodInvocation("getCharacterStream", new Class[]{String.class}, new Object[]{null}));
        assertEquals("wrong value set on data object", new CharacterStream(reader), dataObject.getCharacterStream());
    }


    public void testClobSetterMapping() throws Exception {
        ClobSetterMapping      mapping = new ClobSetterMapping();
        mapping.setObjectMethod(DataObject.class.getMethod("setClob", new Class[] {Clob.class}));

        ResultSet result = (ResultSet) MockFactory.createProxy(ResultSet.class);
        IMock resultMock = (IMock) result;
        ClobMock clob = new ClobMock();
        resultMock.addReturnValue(clob);


        DataObject dataObject = new DataObject();
        mapping.insertValueIntoObject(dataObject, result);
        //System.out.println(resultMock.getInvocations());
        resultMock.assertInvoked(new MethodInvocation("getClob", new Class[]{String.class}, new Object[]{null}));
        assertSame("wrong value set on data object", clob, dataObject.getClob());
    }

    public void testDoubleSetterMapping() throws Exception {
        DoubleSetterMapping      mapping = new DoubleSetterMapping();
        mapping.setObjectMethod(DataObject.class.getMethod("setDouble", new Class[] {double.class}));

        ResultSet result = (ResultSet) MockFactory.createProxy(ResultSet.class);
        IMock resultMock = (IMock) result;
        resultMock.addReturnValue(new Double((double) 10));
        resultMock.addReturnValue(new Double((double) 10));

        DataObject dataObject = new DataObject();
        mapping.insertValueIntoObject(dataObject, result);
        //System.out.println(resultMock.getInvocations());
        resultMock.assertInvoked(new MethodInvocation("getDouble", new Class[]{String.class}, new Object[]{null}));
        assertEquals("wrong value set on data object", new Double(10), new Double(dataObject.getDouble()));

        resultMock.clear();
        mapping.insertValueIntoObject(dataObject, result);
        resultMock.assertInvoked(new MethodInvocation("getObject", new Class[]{String.class}, new Object[]{null}));
        resultMock.assertNotInvoked(new MethodInvocation("getDouble", new Class[]{String.class}, new Object[]{null}));
        assertEquals(1, resultMock.getInvocations().size());

    }

    public void testFloatSetterMapping() throws Exception {
        FloatSetterMapping      mapping = new FloatSetterMapping();
        mapping.setObjectMethod(DataObject.class.getMethod("setFloatData", new Class[] {float.class}));

        ResultSet result = (ResultSet) MockFactory.createProxy(ResultSet.class);
        IMock resultMock = (IMock) result;
        resultMock.addReturnValue(new Float((float) 10));
        resultMock.addReturnValue(new Float((float) 10));


        DataObject dataObject = new DataObject();
        mapping.insertValueIntoObject(dataObject, result);
        //System.out.println(resultMock.getInvocations());
        resultMock.assertInvoked(new MethodInvocation("getFloat", new Class[]{String.class}, new Object[]{null}));
        assertEquals("wrong value set on data object", new Float(10), new Float(dataObject.getFloatData()));

        resultMock.clear();
        mapping.insertValueIntoObject(dataObject, result);
        resultMock.assertInvoked(new MethodInvocation("getObject", new Class[]{String.class}, new Object[]{null}));
        resultMock.assertNotInvoked(new MethodInvocation("getFloat", new Class[]{String.class}, new Object[]{null}));
        assertEquals(1, resultMock.getInvocations().size());

    }


    public void testIntSetterMapping() throws Exception {
        IntSetterMapping      mapping = new IntSetterMapping();
        mapping.setObjectMethod(DataObject.class.getMethod("setIntData", new Class[] {int.class}));

        ResultSet result = (ResultSet) MockFactory.createProxy(ResultSet.class);
        IMock resultMock = (IMock) result;
        resultMock.addReturnValue(new Integer((int) 10));
        resultMock.addReturnValue(new Integer((int) 10));

        DataObject dataObject = new DataObject();
        mapping.insertValueIntoObject(dataObject, result);
        //System.out.println(resultMock.getInvocations());
        resultMock.assertInvoked(new MethodInvocation("getInt", new Class[]{String.class}, new Object[]{null}));
        assertEquals("wrong value set on data object", 10, dataObject.getIntData());

        resultMock.clear();
        mapping.insertValueIntoObject(dataObject, result);
        resultMock.assertInvoked(new MethodInvocation("getObject", new Class[]{String.class}, new Object[]{null}));
        resultMock.assertNotInvoked(new MethodInvocation("getInt", new Class[]{String.class}, new Object[]{null}));
        assertEquals(1, resultMock.getInvocations().size());
    }

    public void testLongSetterMapping() throws Exception {
        LongSetterMapping      mapping = new LongSetterMapping();
        mapping.setObjectMethod(DataObject.class.getMethod("setLongData", new Class[] {long.class}));

        ResultSet result = (ResultSet) MockFactory.createProxy(ResultSet.class);
        IMock resultMock = (IMock) result;
        resultMock.addReturnValue(new Long((int) 10));
        resultMock.addReturnValue(new Long((int) 10));

        DataObject dataObject = new DataObject();
        mapping.insertValueIntoObject(dataObject, result);
        //System.out.println(resultMock.getInvocations());
        resultMock.assertInvoked(new MethodInvocation("getLong", new Class[]{String.class}, new Object[]{null}));
        assertEquals("wrong value set on data object", 10, dataObject.getLongData());

        resultMock.clear();
        mapping.insertValueIntoObject(dataObject, result);
        resultMock.assertInvoked(new MethodInvocation("getObject", new Class[]{String.class}, new Object[]{null}));
        resultMock.assertNotInvoked(new MethodInvocation("getLong", new Class[]{String.class}, new Object[]{null}));
        assertEquals(1, resultMock.getInvocations().size());
    }


    public void testShortSetterMapping() throws Exception {
        ShortSetterMapping      mapping = new ShortSetterMapping();
        mapping.setObjectMethod(DataObject.class.getMethod("setShort", new Class[] {short.class}));

        ResultSet result = (ResultSet) MockFactory.createProxy(ResultSet.class);
        IMock resultMock = (IMock) result;
        resultMock.addReturnValue(new Short((short)10));
        resultMock.addReturnValue(new Short((short)10));

        DataObject dataObject = new DataObject();
        mapping.insertValueIntoObject(dataObject, result);
        //System.out.println(resultMock.getInvocations());
        resultMock.assertInvoked(new MethodInvocation("getShort", new Class[]{String.class}, new Object[]{null}));
        assertEquals("wrong value set on data object", 10, dataObject.getShortData());

        resultMock.clear();
        mapping.insertValueIntoObject(dataObject, result);
        resultMock.assertInvoked(new MethodInvocation("getObject", new Class[]{String.class}, new Object[]{null}));
        resultMock.assertNotInvoked(new MethodInvocation("getShort", new Class[]{String.class}, new Object[]{null}));
        assertEquals(1, resultMock.getInvocations().size());

    }


    public void testSqlDateSetterMapping() throws Exception {
        SqlDateSetterMapping      mapping = new SqlDateSetterMapping();
        mapping.setObjectMethod(DataObject.class.getMethod("setSqlDate", new Class[] {java.sql.Date.class}));

        ResultSet result = (ResultSet) MockFactory.createProxy(ResultSet.class);
        IMock resultMock = (IMock) result;
        long time = System.currentTimeMillis();
        resultMock.addReturnValue(new java.sql.Date(time));

        DataObject dataObject = new DataObject();
        mapping.insertValueIntoObject(dataObject, result);
        //System.out.println(resultMock.getInvocations());
        resultMock.assertInvoked(new MethodInvocation("getDate", new Class[]{String.class}, new Object[]{null}));
        assertEquals("wrong value set on data object", time, dataObject.getSqlDate().getTime());
    }


    public void testTimeSetterMapping() throws Exception {
        TimeSetterMapping      mapping = new TimeSetterMapping();
        mapping.setObjectMethod(DataObject.class.getMethod("setTime", new Class[] {java.sql.Time.class}));

        ResultSet result = (ResultSet) MockFactory.createProxy(ResultSet.class);
        IMock resultMock = (IMock) result;
        long time = System.currentTimeMillis();
        resultMock.addReturnValue(new java.sql.Time(time));

        DataObject dataObject = new DataObject();
        mapping.insertValueIntoObject(dataObject, result);
        //System.out.println(resultMock.getInvocations());
        resultMock.assertInvoked(new MethodInvocation("getTime", new Class[]{String.class}, new Object[]{null}));
        assertEquals("wrong value set on data object", time, dataObject.getTime().getTime());
    }


    public void testTimestampSetterMapping() throws Exception {
        TimestampSetterMapping      mapping = new TimestampSetterMapping();
        mapping.setObjectMethod(DataObject.class.getMethod("setTimestamp", new Class[] {java.sql.Timestamp.class}));

        ResultSet result = (ResultSet) MockFactory.createProxy(ResultSet.class);
        IMock resultMock = (IMock) result;
        long time = System.currentTimeMillis();
        resultMock.addReturnValue(new java.sql.Timestamp(time));

        DataObject dataObject = new DataObject();
        mapping.insertValueIntoObject(dataObject, result);
        //System.out.println(resultMock.getInvocations());
        resultMock.assertInvoked(new MethodInvocation("getTimestamp", new Class[]{String.class}, new Object[]{null}));
        assertEquals("wrong value set on data object", time, dataObject.getTimestamp().getTime());
    }

    public void testUrlSetterMapping() throws Exception {
        UrlSetterMapping      mapping = new UrlSetterMapping();
        mapping.setObjectMethod(DataObject.class.getMethod("setUrl", new Class[] {URL.class}));

        ResultSet result = (ResultSet) MockFactory.createProxy(ResultSet.class);
        IMock resultMock = (IMock) result;
        URL url = new URL("http://www.jenkov.com");
        resultMock.addReturnValue(url);

        DataObject dataObject = new DataObject();
        mapping.insertValueIntoObject(dataObject, result);
        //System.out.println(resultMock.getInvocations());
        resultMock.assertInvoked(new MethodInvocation("getURL", new Class[]{String.class}, new Object[]{null}));
        assertEquals("wrong value set on data object", url, dataObject.getUrl());
    }




}
