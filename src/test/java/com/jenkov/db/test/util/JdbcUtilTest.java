package com.jenkov.db.test.util;

import junit.framework.TestCase;
import com.jenkov.db.impl.ObjectDao;
import com.jenkov.db.impl.PersistenceConfiguration;
import com.jenkov.db.itf.IPersistenceConfiguration;
import com.jenkov.db.itf.IObjectDao;
import com.jenkov.db.itf.PersistenceException;
import com.jenkov.db.test.Environment;
import com.jenkov.db.util.JdbcUtil;
import com.jenkov.db.PersistenceManager;
import com.jenkov.testing.mock.impl.MockFactory;
import com.jenkov.testing.mock.impl.MethodInvocation;
import com.jenkov.testing.mock.itf.IMock;

import java.sql.*;
import java.math.BigDecimal;
import java.net.URL;

/**
 * @author Jakob Jenkov - Copyright 2005 Jenkov Development
 */
public class JdbcUtilTest extends TestCase {

    public void testCloseDao() throws Exception{
        Connection connection = null;

        try{
                       connection = Environment.getConnection();
            Connection conProxy   = (Connection) MockFactory.createProxy(connection, Connection.class);
            IMock      conMock    = MockFactory.getMock(conProxy);
            IPersistenceConfiguration config     = new PersistenceConfiguration(new PersistenceManager());
            IObjectDao objectDao = new ObjectDao(conProxy, config);

            JdbcUtil.close(objectDao);
            conMock.assertInvoked(new MethodInvocation("close"));

            JdbcUtil.close((IObjectDao)null);
            JdbcUtil.closeIgnore(objectDao);
            JdbcUtil.closeIgnore((IObjectDao)null);
        } finally {
            try {
                if(connection != null) connection.close();
            } catch (SQLException e) {
                //ignore, most of the time the connection will already be closed, and close() will
                //therefore throw an exception.
            }
        }
    }

    public void testCloseConnection() throws Exception {
        Connection connection = null;

        try{
                       connection = Environment.getConnection();
            Connection conProxy   = (Connection) MockFactory.createProxy(connection, Connection.class);
            IMock      conMock    = MockFactory.getMock(conProxy);

            JdbcUtil.close(conProxy);
            conMock.assertInvoked(new MethodInvocation("close"));
            JdbcUtil.close(conProxy);

            JdbcUtil.close((Connection)null);
            JdbcUtil.closeIgnore(conProxy);
            JdbcUtil.closeIgnore((Connection)null);
        } finally {
            try {
                if(connection != null){
                    connection.close();
                }
            } catch (SQLException e) {
                //ignore, most of the time the connection will already be closed, and close() will
                //therefore throw an exception.
            }
        }
    }

    public void testCloseConnectionStatementResultSet() throws Exception {
        Connection connection = null;

        try{
                       connection = Environment.getConnection();
            Connection conProxy   = (Connection) MockFactory.createProxy(connection, Connection.class);
            IMock      conMock    = MockFactory.getMock(conProxy);
            Statement  statementProxy  = (Statement) MockFactory.createProxy(Statement.class);
            IMock      statementMock   = MockFactory.getMock(statementProxy);
            ResultSet  resultSetProxy  = (ResultSet) MockFactory.createProxy(ResultSet.class);
            IMock      resultSetMock   = MockFactory.getMock(resultSetProxy);

            JdbcUtil.close(conProxy, statementProxy, resultSetProxy);
            conMock.assertInvoked(new MethodInvocation("close"));
            statementMock.assertInvoked(new MethodInvocation("close"));
            resultSetMock.assertInvoked(new MethodInvocation("close"));

            JdbcUtil.close(conProxy, statementProxy, null);
            JdbcUtil.close(conProxy, null, resultSetProxy);
            JdbcUtil.close(conProxy, null, null);
            JdbcUtil.close(null, null, null);
            JdbcUtil.close(null, statementProxy, resultSetProxy);

            conMock.clear();
            statementMock.clear();
            resultSetMock.clear();
            JdbcUtil.closeIgnore(conProxy, statementProxy, null);
            JdbcUtil.closeIgnore(conProxy, null, resultSetProxy);
            JdbcUtil.closeIgnore(conProxy, null, null);
            JdbcUtil.closeIgnore(null, null, null);
            JdbcUtil.closeIgnore(null, statementProxy, resultSetProxy);
            conMock.assertInvoked(new MethodInvocation("close"));
            statementMock.assertInvoked(new MethodInvocation("close"));
            resultSetMock.assertInvoked(new MethodInvocation("close"));
        } finally {
            try {
                if(connection != null){
                    connection.close();
                }
            } catch (SQLException e) {
                //ignore, most of the time the connection will already be closed, and close() will
                //therefore throw an exception.
            }
        }
    }

    public void testCloseStatement() throws PersistenceException {
        Statement statementProxy = (Statement) MockFactory.createProxy(Statement.class);
        IMock     statementMock  = MockFactory.getMock(statementProxy);
        JdbcUtil.close(statementProxy);
        statementMock.assertInvoked(new MethodInvocation("close"));
        JdbcUtil.close((Statement) null);

        statementMock.clear();
        JdbcUtil.closeIgnore(statementProxy);
        statementMock.assertInvoked(new MethodInvocation("close"));
        JdbcUtil.closeIgnore((Statement) null);
    }

    public void testCloseResultSet() throws PersistenceException {
        ResultSet resultSetProxy = (ResultSet) MockFactory.createProxy(ResultSet.class);
        IMock     statementMock  = MockFactory.getMock(resultSetProxy);
        JdbcUtil.close(resultSetProxy);
        statementMock.assertInvoked(new MethodInvocation("close"));
        JdbcUtil.close((ResultSet) null);

        statementMock.clear();
        JdbcUtil.closeIgnore(resultSetProxy);
        statementMock.assertInvoked(new MethodInvocation("close"));
        JdbcUtil.closeIgnore((ResultSet) null);
    }

    public void testInsertParameter() throws Exception {
        PreparedStatement stmProxy = (PreparedStatement) MockFactory.createProxy(PreparedStatement.class);
        IMock stmMock = MockFactory.getMock(stmProxy);

        JdbcUtil.insertParameter(stmProxy, 1, "param");
        stmMock.assertInvoked(new MethodInvocation("setString", new Class[]{int.class, String.class}, new Object[]{new Integer(1), "param"}));
        JdbcUtil.insertParameter(stmProxy, 1, new Integer(555));
        stmMock.assertInvoked(new MethodInvocation("setInt", new Class[]{int.class, int.class}, new Object[]{new Integer(1), new Integer(555)}));
        JdbcUtil.insertParameter(stmProxy, 1, new Long(333));
        stmMock.assertInvoked(new MethodInvocation("setLong", new Class[]{int.class, long.class}, new Object[]{new Integer(1), new Long(333)}));
        JdbcUtil.insertParameter(stmProxy, 1, new BigDecimal(333));
        stmMock.assertInvoked(new MethodInvocation("setBigDecimal", new Class[]{int.class, BigDecimal.class}, new Object[]{new Integer(1), new BigDecimal(333)}));
        java.sql.Date anSqlDate = new java.sql.Date(System.currentTimeMillis());
        JdbcUtil.insertParameter(stmProxy, 1, anSqlDate);
        stmMock.assertInvoked(new MethodInvocation("setDate", new Class[]{int.class, java.sql.Date.class}, new Object[]{new Integer(1), new java.sql.Date(anSqlDate.getTime())}));
        Timestamp aTimestamp = new Timestamp(System.currentTimeMillis());
        JdbcUtil.insertParameter(stmProxy, 1, aTimestamp);
        stmMock.assertInvoked(new MethodInvocation("setTimestamp", new Class[]{int.class, Timestamp.class}, new Object[]{new Integer(1), aTimestamp}));
        Time aTime = new Time(System.currentTimeMillis());
        JdbcUtil.insertParameter(stmProxy, 1, aTime);
        stmMock.assertInvoked(new MethodInvocation("setTime", new Class[]{int.class, Time.class}, new Object[]{new Integer(1), aTime}));

        stmMock.clear();
        java.util.Date aDate = new java.util.Date(System.currentTimeMillis());
        JdbcUtil.insertParameter(stmProxy, 1, aDate);
        stmMock.assertInvoked(new MethodInvocation("setTimestamp", new Class[]{int.class, java.sql.Timestamp.class}, new Object[]{new Integer(1), new java.sql.Timestamp(aDate.getTime())}));

        JdbcUtil.insertParameter(stmProxy, 1, new Boolean(true));
        stmMock.assertInvoked(new MethodInvocation("setBoolean", new Class[]{int.class, boolean.class}, new Object[]{new Integer(1), new Boolean(true)}));

        JdbcUtil.insertParameter(stmProxy, 1, new Byte((byte)34));
        stmMock.assertInvoked(new MethodInvocation("setByte", new Class[]{int.class, byte.class}, new Object[]{new Integer(1), new Byte((byte) 34)}));

        byte[] bytes = new byte[35];
        JdbcUtil.insertParameter(stmProxy, 1, bytes);
        stmMock.assertInvoked(new MethodInvocation("setBytes", new Class[]{int.class, byte[].class}, new Object[]{new Integer(1), bytes}));

        JdbcUtil.insertParameter(stmProxy, 1, new Double(3));
        stmMock.assertInvoked(new MethodInvocation("setDouble", new Class[]{int.class, double.class}, new Object[]{new Integer(1), new Double(3)}));

        JdbcUtil.insertParameter(stmProxy, 1, new Float(3));
        stmMock.assertInvoked(new MethodInvocation("setFloat", new Class[]{int.class, float.class}, new Object[]{new Integer(1), new Float(3)}));

        JdbcUtil.insertParameter(stmProxy, 1, new Short((short)5));
        stmMock.assertInvoked(new MethodInvocation("setShort", new Class[]{int.class, short.class}, new Object[]{new Integer(1), new Short((short)5)}));

        JdbcUtil.insertParameter(stmProxy, 1, new URL("http://www.jenkov.com"));
        stmMock.assertInvoked(new MethodInvocation("setURL", new Class[]{int.class, URL.class}, new Object[]{new Integer(1), new URL("http://www.jenkov.com")}));

        Object object = new Object();
        JdbcUtil.insertParameter(stmProxy, 1, object);
        stmMock.assertInvoked(new MethodInvocation("setObject", new Class[]{int.class, Object.class}, new Object[]{new Integer(1), object}));


    }


}
