package com.jenkov.db.test.mapping.method;

import com.jenkov.db.impl.mapping.method.*;
import com.jenkov.testing.mock.impl.MethodInvocation;
import com.jenkov.testing.mock.impl.MockFactory;
import com.jenkov.testing.mock.itf.IMock;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.PreparedStatement;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Jakob Jenkov - Copyright 2005 Jenkov Development
 */
public class GetterMappingTests {

    @Test
    public void testAsciiStreamGetterMapping() throws Exception {
        AsciiStreamGetterMapping      mapping = new AsciiStreamGetterMapping();
        PreparedStatement statement = createPreparedStatementProxy();
        IMock statementMock = MockFactory.getMock(statement);

        AsciiStream stream = new AsciiStream(null, 100);
        mapping.insertObject(stream, statement, 1);
        statementMock.assertInvoked(new MethodInvocation("setAsciiStream",
                new Class[]{int.class, InputStream.class, int.class},
                new Object[]{new Integer(1), null, new Integer(100)}));
    }

    @Test
    public void testBigDecimalGetterMapping() throws Exception {
        BigDecimalGetterMapping  mapping = new BigDecimalGetterMapping();
        PreparedStatement statement = createPreparedStatementProxy();
        IMock statementMock = MockFactory.getMock(statement);

        BigDecimal bigDecimal = new BigDecimal(100);
        mapping.insertObject(bigDecimal, statement, 1);
        statementMock.assertInvoked(new MethodInvocation("setBigDecimal",
                new Class[]{int.class, BigDecimal.class},
                new Object[]{new Integer(1), bigDecimal}));
    }

    @Test
    public void testBinaryStreamGetterMapping() throws Exception {
        BinaryStreamGetterMapping  mapping = new BinaryStreamGetterMapping();
        PreparedStatement statement = createPreparedStatementProxy();
        IMock statementMock = MockFactory.getMock(statement);

        BinaryStream stream = new BinaryStream(null, 100);
        mapping.insertObject(stream, statement, 1);
        statementMock.assertInvoked(new MethodInvocation("setBinaryStream",
                new Class[]{int.class, InputStream.class, int.class},
                new Object[]{new Integer(1), null, new Integer(100)}));
    }

    @Test
    public void testBlobGetterMapping() throws Exception {
        BlobGetterMapping  mapping = new BlobGetterMapping();
        PreparedStatement statement = createPreparedStatementProxy();
        IMock statementMock = MockFactory.getMock(statement);

        Blob blob = new BlobMock();
        mapping.insertObject(blob, statement, 1);
        statementMock.assertInvoked(new MethodInvocation("setBlob",
                new Class[]{int.class, Blob.class},
                new Object[]{new Integer(1), blob}));
    }

    @Test
    public void testBooleanGetterMapping() throws Exception {
        BooleanGetterMapping  mapping = new BooleanGetterMapping();
        mapping.setColumnType(java.sql.Types.BOOLEAN);
        PreparedStatement statement = createPreparedStatementProxy();
        IMock statementMock = MockFactory.getMock(statement);

        Boolean bool = new Boolean(true);
        mapping.insertObject(bool, statement, 1);
        statementMock.assertInvoked(new MethodInvocation("setBoolean",
                new Class[]{int.class, boolean.class},
                new Object[]{new Integer(1), bool}));

        statementMock.clear();
        mapping.insertObject(null, statement, 1);
        statementMock.assertInvoked(new MethodInvocation("setNull", new Class[]{int.class, int.class},
                new Object[]{new Integer(1), new Integer(java.sql.Types.BOOLEAN)}));
        assertEquals(1, statementMock.getInvocations().size());

        statementMock.clear();
        mapping.setColumnType(java.sql.Types.INTEGER);
        mapping.insertObject(bool, statement, 1);
        statementMock.assertInvoked(new MethodInvocation("setInt",
                new Class[]{int.class, int.class},
                new Object[]{new Integer(1), new Integer(1)}));

        statementMock.clear();
        bool = new Boolean(false);
        mapping.insertObject(bool, statement, 1);
//        System.out.println(statementMock.getInvocations());
        statementMock.assertInvoked(new MethodInvocation("setInt",
                new Class[]{int.class, int.class},
                new Object[]{new Integer(1), new Integer(0)}));
    }

    @Test
    public void testByteArrayGetterMapping() throws Exception {
        ByteArrayGetterMapping   mapping = new ByteArrayGetterMapping();
        PreparedStatement statement = createPreparedStatementProxy();
        IMock statementMock = MockFactory.getMock(statement);

        byte[] byteArray = new byte[10];
        mapping.insertObject(byteArray, statement, 1);
        statementMock.assertInvoked(new MethodInvocation("setBytes",
                new Class[]{int.class, byte[].class},
                new Object[]{new Integer(1), byteArray}));
    }

    @Test
    public void testByteGetterMapping() throws Exception {
        ByteGetterMapping        mapping = new ByteGetterMapping();
        PreparedStatement statement = createPreparedStatementProxy();
        IMock statementMock = MockFactory.getMock(statement);

        Byte theByte = new Byte((byte)100);
        mapping.insertObject(theByte, statement, 1);
        statementMock.assertInvoked(new MethodInvocation("setByte",
                new Class[]{int.class, byte.class},
                new Object[]{new Integer(1), theByte}));

        statementMock.clear();
        mapping.insertObject(null, statement, 1);
        statementMock.assertInvoked(new MethodInvocation("setNull", new Class[]{int.class, int.class},
                new Object[]{new Integer(1), new Integer(java.sql.Types.TINYINT)}));
        assertEquals(1, statementMock.getInvocations().size());
    }

    @Test
    public void testCharacterStreamGetterMapping() throws Exception {
        CharacterStreamGetterMapping mapping = new CharacterStreamGetterMapping();
        PreparedStatement statement = createPreparedStatementProxy();
        IMock statementMock = MockFactory.getMock(statement);

        CharacterStream stream = new CharacterStream(null, 110);
        mapping.insertObject(stream, statement, 1);
        statementMock.assertInvoked(new MethodInvocation("setCharacterStream",
                new Class[]{int.class, Reader.class, int.class},
                new Object[]{new Integer(1), null, new Integer(110)}));
    }

    @Test
    public void testClobGetterMapping() throws Exception {
        ClobGetterMapping  mapping = new ClobGetterMapping();
        PreparedStatement statement = createPreparedStatementProxy();
        IMock statementMock = MockFactory.getMock(statement);

        Clob clob = new ClobMock();
        mapping.insertObject(clob, statement, 1);
        statementMock.assertInvoked(new MethodInvocation("setClob",
                new Class[]{int.class, Clob.class},
                new Object[]{new Integer(1), clob}));
    }

    @Test
    public void testDateGetterMapping() throws Exception {
        DateGetterMapping      mapping = new DateGetterMapping();
        PreparedStatement statement = createPreparedStatementProxy();
        IMock statementMock = MockFactory.getMock(statement);

        long date = System.currentTimeMillis();
        mapping.insertObject(new java.util.Date(date), statement, 1);

        //System.out.println(statementMock.getInvocations());
        statementMock.assertInvoked(new MethodInvocation("setTimestamp",
                new Class[]{int.class, java.sql.Timestamp.class}, new Object[]{new Integer(1), new java.sql.Timestamp(date)}));
    }

    @Test
    public void testDoubleGetterMapping() throws Exception {
        DoubleGetterMapping      mapping = new DoubleGetterMapping();
        PreparedStatement statement = createPreparedStatementProxy();
        IMock statementMock = MockFactory.getMock(statement);

        mapping.insertObject(new Double(23), statement, 5);
        statementMock.assertInvoked(new MethodInvocation("setDouble",
                new Class[]{int.class, double.class}, new Object[]{new Integer(5), new Double(23)}));


        statementMock.clear();
        mapping.insertObject(null, statement, 1);
        statementMock.assertInvoked(new MethodInvocation("setNull", new Class[]{int.class, int.class},
                new Object[]{new Integer(1), new Integer(java.sql.Types.DOUBLE)}));
        assertEquals(1, statementMock.getInvocations().size());
    }

    @Test
    public void testFloatGetterMapping() throws Exception {
        FloatGetterMapping      mapping = new FloatGetterMapping();
        PreparedStatement statement = createPreparedStatementProxy();
        IMock statementMock = MockFactory.getMock(statement);

        mapping.insertObject(new Float(27), statement, 5);
        statementMock.assertInvoked(new MethodInvocation("setFloat",
                new Class[]{int.class, float.class}, new Object[]{new Integer(5), new Float(27)}));

        statementMock.clear();
        mapping.insertObject(null, statement, 1);
        statementMock.assertInvoked(new MethodInvocation("setNull", new Class[]{int.class, int.class},
                new Object[]{new Integer(1), new Integer(java.sql.Types.FLOAT)}));
        assertEquals(1, statementMock.getInvocations().size());
    }

    @Test
    public void testIntGetterMapping() throws Exception {
        IntGetterMapping      mapping = new IntGetterMapping();
        PreparedStatement statement = createPreparedStatementProxy();
        IMock statementMock = MockFactory.getMock(statement);

        mapping.insertObject(new Integer(27), statement, 5);
        statementMock.assertInvoked(new MethodInvocation("setInt",
                new Class[]{int.class, int.class}, new Object[]{new Integer(5), new Integer(27)}));

        statementMock.clear();
        mapping.insertObject(null, statement, 1);
        statementMock.assertInvoked(new MethodInvocation("setNull", new Class[]{int.class, int.class},
                new Object[]{new Integer(1), new Integer(java.sql.Types.INTEGER)}));
        assertEquals(1, statementMock.getInvocations().size());
    }

    @Test
    public void testShortGetterMapping() throws Exception {
        ShortGetterMapping      mapping = new ShortGetterMapping();
        PreparedStatement statement = createPreparedStatementProxy();
        IMock statementMock = MockFactory.getMock(statement);

        mapping.insertObject(new Short((short) 27), statement, 5);
        statementMock.assertInvoked(new MethodInvocation("setShort",
                new Class[]{int.class, short.class}, new Object[]{new Integer(5), new Short((short) 27)}));

        statementMock.clear();
        mapping.insertObject(null, statement, 1);
        statementMock.assertInvoked(new MethodInvocation("setNull", new Class[]{int.class, int.class},
                new Object[]{new Integer(1), new Integer(java.sql.Types.SMALLINT)}));
        assertEquals(1, statementMock.getInvocations().size());

    }

    @Test
    public void testStringGetterMapping() throws Exception {
        StringGetterMapping      mapping = new StringGetterMapping();
        PreparedStatement statement = createPreparedStatementProxy();
        IMock statementMock = MockFactory.getMock(statement);

        mapping.insertObject("value", statement, 1);
        statementMock.assertInvoked(new MethodInvocation("setString",
                new Class[]{int.class, String.class}, new Object[]{new Integer(1), "value"}));
    }

    @Test
    public void testLongGetterMapping() throws Exception {
        LongGetterMapping      mapping = new LongGetterMapping();
        PreparedStatement statement = createPreparedStatementProxy();
        IMock statementMock = MockFactory.getMock(statement);

        mapping.insertObject(new Long(23), statement, 5);
        statementMock.assertInvoked(new MethodInvocation("setLong",
                new Class[]{int.class, long.class}, new Object[]{new Integer(5), new Long(23)}));
        
        statementMock.clear();
        mapping.insertObject(null, statement, 1);
        statementMock.assertInvoked(new MethodInvocation("setNull", new Class[]{int.class, int.class},
                new Object[]{new Integer(1), new Integer(java.sql.Types.BIGINT)}));
        assertEquals(1, statementMock.getInvocations().size());

    }

    @Test
    public void testSqlDateGetterMapping() throws Exception {
        SqlDateGetterMapping      mapping = new SqlDateGetterMapping();
        PreparedStatement statement = createPreparedStatementProxy();
        IMock statementMock = MockFactory.getMock(statement);

        long date = System.currentTimeMillis();
        mapping.insertObject(new java.sql.Date(date), statement, 1);
        statementMock.assertInvoked(new MethodInvocation("setDate",
                new Class[]{int.class, java.sql.Date.class}, new Object[]{new Integer(1), new java.sql.Date(date)}));
    }

    @Test
    public void testTimeGetterMapping() throws Exception {
        TimeGetterMapping      mapping = new TimeGetterMapping();
        PreparedStatement statement = createPreparedStatementProxy();
        IMock statementMock = MockFactory.getMock(statement);

        long date = System.currentTimeMillis();
        mapping.insertObject(new java.sql.Time(date), statement, 1);
        statementMock.assertInvoked(new MethodInvocation("setTime",
                new Class[]{int.class, java.sql.Time.class}, new Object[]{new Integer(1), new java.sql.Time(date)}));
    }

    @Test
    public void testTimestampGetterMapping() throws Exception {
        TimestampGetterMapping      mapping = new TimestampGetterMapping();
        PreparedStatement statement = createPreparedStatementProxy();
        IMock statementMock = MockFactory.getMock(statement);

        long date = System.currentTimeMillis();
        mapping.insertObject(new java.sql.Timestamp(date), statement, 1);
        statementMock.assertInvoked(new MethodInvocation("setTimestamp",
                new Class[]{int.class, java.sql.Timestamp.class}, new Object[]{new Integer(1), new java.sql.Timestamp(date)}));
    }

    @Test
    public void testUrlGetterMapping() throws Exception {
        UrlGetterMapping      mapping = new UrlGetterMapping();
        PreparedStatement statement = createPreparedStatementProxy();
        IMock statementMock = MockFactory.getMock(statement);

        String url = "http://www.jenkov.dk/";
        mapping.insertObject(new URL(url), statement, 1);
        statementMock.assertInvoked(new MethodInvocation("setURL",
                new Class[]{int.class, URL.class}, new Object[]{new Integer(1), new URL(url)}));
    }



    private PreparedStatement createPreparedStatementProxy() {
        PreparedStatement statement = (PreparedStatement) MockFactory.createProxy(PreparedStatement.class);
        return statement;
    }
}
