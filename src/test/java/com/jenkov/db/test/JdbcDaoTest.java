package com.jenkov.db.test;

import com.jenkov.db.PersistenceManager;
import com.jenkov.db.test.objects.PersistentObject;
import com.jenkov.db.impl.PreparedStatementManagerBase;
import com.jenkov.db.impl.ResultSetProcessorBase;
import com.jenkov.db.impl.ResultSetGraphProcessorBase;
import com.jenkov.db.itf.IResultSetProcessor;
import com.jenkov.db.itf.IPreparedStatementManager;
import com.jenkov.db.itf.PersistenceException;
import com.jenkov.db.itf.IDaos;
import com.jenkov.testing.mock.impl.MockFactory;
import com.jenkov.testing.mock.impl.MethodInvocation;
import com.jenkov.testing.mock.itf.IMock;
import junit.framework.TestCase;

import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Set;


/**
 * @author Jakob Jenkov - Copyright 2004-2006 Jenkov Development
 */
public class JdbcDaoTest extends TestCase {

    PersistenceManager manager = new PersistenceManager(Environment.getDataSource());

    public void testRead_UsingGraphProcessor() throws Exception{
        IDaos daos = manager.createDaos();
        try{
            daos.getJdbcDao().update("insert into persistent_object(id, name) values (33, 'aName')");
            daos.getJdbcDao().update("insert into persistent_object(id, name) values (34, 'aName')");
            daos.getJdbcDao().update("insert into persistent_object(id, name) values (35, 'aName')");
            daos.getJdbcDao().update("insert into persistent_object(id, name) values (36, 'aName2')");

            IResultSetProcessor graphProcessor     = new ResultSetGraphProcessorBase(){
                protected void onInit(ResultSet result) throws SQLException, PersistenceException {
                    this.columnSets = new Set[]{ columnSet("id"), columnSet("name")};
                }

                protected void onColumnChange(ResultSet result, IDaos daos, Set columnSet) throws PersistenceException {
                    if(columnSet.contains("name")){
                        PersistentObject object = (PersistentObject) daos.getObjectDao().read(PersistentObject.class, result);
                        assertTrue(object.getId() == 33 || object.getId() == 36);
                        assertTrue(object.getName().equals("aName") || object.getName().equals("aName2"));
                        if(object.getId() == 33) assertEquals("aName" , object.getName());
                        if(object.getId() == 36) assertEquals("aName2", object.getName());
                    }
                }
            };
            IResultSetProcessor graphProxy         = (IResultSetProcessor) MockFactory.createProxy(graphProcessor);
            IMock               graphProcessorMock = MockFactory.getMock(graphProxy);

            System.out.println(graphProcessorMock.getInvocations());
//            graphProcessorMock.assertInvoked(new MethodInvocation("onColumnChange",
//                    new Class[]{ResultSet.class, IDaos.class, Set.class}));

            daos.getJdbcDao().read("select * from persistent_object order by id", graphProxy);


        } finally {
            daos.getJdbcDao().update("delete from persistent_object");
            daos.getConnection().close();
        }
    }

    public void testReadLong() throws Exception {


        IDaos daos = manager.createDaos();
        try {
            daos.getJdbcDao().update("insert into persistent_object(id, name) values (33, 'aName')");

            Long id = daos.getJdbcDao().readLong("select id from persistent_object where name='aName'");

            assertEquals(new Long(33), id);
        } finally {
            daos.getJdbcDao().update("delete from persistent_object where id=33");
            daos.getConnection().close();
        }
    }

    public void testRead() throws Exception {

        IDaos daos = manager.createDaos();
        try{
            daos.getJdbcDao().update("insert into persistent_object(id, name) values (33, 'aName')");

            IResultSetProcessor processor     = (IResultSetProcessor) MockFactory.createProxy(IResultSetProcessor.class);
            IMock               processorMock = MockFactory.getMock(processor);

            daos.getJdbcDao().read("select * from persistent_object where id = 33", processor);

            processorMock.assertInvoked(new MethodInvocation("init", new Class[]{ResultSet.class, IDaos.class}));
            processorMock.assertInvoked(new MethodInvocation("process", new Class[]{ResultSet.class, IDaos.class}));
            processorMock.assertInvoked(new MethodInvocation("getResult"));

            processorMock.clear();
            daos.getJdbcDao().read("select * from persistent_object where id = ?", processor, 33);

            processorMock.assertInvoked(new MethodInvocation("init", new Class[]{ResultSet.class, IDaos.class}));
            processorMock.assertInvoked(new MethodInvocation("process", new Class[]{ResultSet.class, IDaos.class}));
            processorMock.assertInvoked(new MethodInvocation("getResult"));

            processorMock.clear();
             daos.getJdbcDao().read("select * from persistent_object where id = ?", processor, 34);

            System.out.println(processorMock.getInvocations());
            processorMock.assertInvoked(   new MethodInvocation("init", new Class[]{ResultSet.class, IDaos.class}));
            processorMock.assertNotInvoked(new MethodInvocation("process", new Class[]{ResultSet.class, IDaos.class}));
            processorMock.assertInvoked(   new MethodInvocation("getResult"));
        } finally {
            daos.getJdbcDao().update("delete from persistent_object where id = 33");
            daos.getConnection().close();
        }
    }

    public void testRead_PreparedStatementManager() throws Exception{
        IDaos daos = manager.createDaos();
        try{
            daos.getJdbcDao().update("insert into persistent_object(id, name) values (33, 'aName')");

            IResultSetProcessor processor     = (IResultSetProcessor) MockFactory.createProxy(IResultSetProcessor.class);
            IMock               processorMock = MockFactory.getMock(processor);

            IPreparedStatementManager statementManagerDelegate = new PreparedStatementManagerBase();
            IPreparedStatementManager statementManager     = (IPreparedStatementManager) MockFactory.createProxy(statementManagerDelegate);
            IMock                     statementManagerMock = MockFactory.getMock(statementManager);

            daos.getJdbcDao().read("select * from persistent_object where id=33", statementManager, processor);

//            System.out.println(statementManagerMock.getInvocations());

            statementManagerMock.assertInvoked(new MethodInvocation("prepare"    , new Class[]{String.class, Connection.class}));
            statementManagerMock.assertInvoked(new MethodInvocation("init"       , new Class[]{PreparedStatement.class}));
            statementManagerMock.assertInvoked(new MethodInvocation("execute"    , new Class[]{PreparedStatement.class}));
            statementManagerMock.assertInvoked(new MethodInvocation("postProcess", new Class[]{PreparedStatement.class}));

            processorMock.assertInvoked(new MethodInvocation("init", new Class[]{ResultSet.class, IDaos.class}));
            processorMock.assertInvoked(new MethodInvocation("process", new Class[]{ResultSet.class, IDaos.class}));
            processorMock.assertInvoked(new MethodInvocation("getResult"));

            statementManagerDelegate = new PreparedStatementManagerBase(){
                public PreparedStatement prepare(String sql, Connection connection) throws SQLException, PersistenceException {
                    return null;
                }
            };
            statementManager     = (IPreparedStatementManager) MockFactory.createProxy(statementManagerDelegate);
            daos.getJdbcDao().read("select * from persistent_object where id=33", statementManagerDelegate, processor);

        } finally {
            daos.getJdbcDao().update("delete from persistent_object where id = 33");
            daos.getConnection().close();
        }
    }

    public void testRead_exceptions() throws Exception {
        IDaos daos = manager.createDaos();
        try{
            boolean autoCommit = daos.getConnection().getAutoCommit();
            IPreparedStatementManager manager   = new PreparedStatementManagerBase();
            IResultSetProcessor       processor = new ResultSetProcessorBase();
            daos.getJdbcDao().read("select * from blablabla", manager, processor);
            fail("Exception exptected");
        } catch (Exception e){
            //e.printStackTrace();
            //ignore, expected
        } finally {
            daos.getConnection().close();
        }
    }

    public void testUpdate() throws Exception {
        IDaos daos = manager.createDaos();

        try{
            daos.getJdbcDao().update("insert into persistent_object(id, name) values (?, 'aName')", new Object[]{new Long(45)});

            assertEquals(new Long(45), daos.getJdbcDao().readLong("select id from persistent_object where name='aName'"));

            IPreparedStatementManager statementManager = new PreparedStatementManagerBase();
            daos.getJdbcDao().update("update persistent_object set name='newName' where id=45", statementManager);

            statementManager = (IPreparedStatementManager) MockFactory.createProxy(statementManager);
            daos.getJdbcDao().update("update persistent_object set name='newName' where id=45", statementManager);


        } finally {
            daos.getJdbcDao().update("delete from persistent_object");
            daos.getConnection().close();
        }
    }


    public void testUpdate_exceptions() throws Exception{
        IDaos daos = manager.createDaos();
        try{
            daos.getJdbcDao().update("insert into blablabla(id, name) values (?, 'aName')", new Object[]{new Long(45)});
            fail("Exception exptected");
        } catch(Exception e){
            //ignore, expected
        }
        finally {
            daos.getJdbcDao().update("delete from persistent_object");
            daos.getConnection().close();
        }
    }



    public void testReadIdString() throws Exception {
        IDaos daos = manager.createDaos();
        try{
            daos.getJdbcDao().update("insert into persistent_object (id, name) values(1, 'name1')");
            daos.getJdbcDao().update("insert into persistent_object (id, name) values(2, 'name2')");
            daos.getJdbcDao().update("insert into persistent_object (id, name) values(3, 'name3')");

            String idString = daos.getJdbcDao().readIdString("select id from persistent_object");
            assertEquals("(1,2,3)", idString);

            idString = daos.getJdbcDao().readIdString("select id from persistent_object where id > ?", new Object[]{new Long(1)});
            assertEquals("(2,3)", idString);

            idString = daos.getJdbcDao().readIdString("select id from persistent_object where id > 5");
            assertEquals("", idString);

        } finally {
            daos.getJdbcDao().update("delete from persistent_object");
        }

    }


}
