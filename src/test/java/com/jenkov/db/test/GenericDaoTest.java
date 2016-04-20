package com.jenkov.db.test;

import com.jenkov.db.impl.*;
import com.jenkov.db.impl.mapping.CustomObjectMapperBase;
import com.jenkov.db.impl.mapping.ObjectMapper;
import com.jenkov.db.impl.mapping.ObjectMappingKey;
import com.jenkov.db.impl.mapping.ObjectMappingFactory;
import com.jenkov.db.itf.*;
import com.jenkov.db.itf.mapping.ICustomObjectMapper;
import com.jenkov.db.itf.mapping.IObjectMapping;
import com.jenkov.db.test.objects.PersistentObject;
import com.jenkov.db.test.objects.TableWithAutoIncrement;
import com.jenkov.db.util.JdbcUtil;
import com.jenkov.db.PersistenceManager;
import com.jenkov.testing.mock.impl.MethodInvocation;
import com.jenkov.testing.mock.impl.MockFactory;
import com.jenkov.testing.mock.itf.IMock;
import junit.framework.TestCase;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jakob Jenkov
 *         Copyright 2004 Jenkov Development
 */
public class GenericDaoTest extends TestCase{

    public static final String INSERT1 = "insert into persistent_object(id, name, objectValue) values(1, 'name1', 'value1')";
    public static final String INSERT2 = "insert into persistent_object(id, name, objectValue) values(2, 'name2', 'value2')";
    public static final String INSERT3 = "insert into persistent_object(id, name, objectValue) values(3, 'name3', 'value3')";

    public static final String DELETE  = "delete from persistent_object";


    IObjectDao dao = null;
    PersistenceManager persistenceManager = new PersistenceManager();

    public void setUp(){
        try {
            dao = persistenceManager.createDaos(Environment.getConnection()).getObjectDao();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void tearDown(){
        try {
            //System.out.println(getName() + " closing connection");
            if(dao != null) dao.closeConnection();
        } catch (PersistenceException e) {
            e.printStackTrace();
        }
    }

    public void testConfigurationConstructor(){
        IPersistenceConfiguration config = new PersistenceConfiguration(new PersistenceManager());
        Connection connection = (Connection) MockFactory.createProxy(Connection.class);
        IObjectDao dao = new ObjectDao(connection, config);

        assertSame(config, dao.getConfiguration());
        assertSame(connection, dao.getConnection());

        try {
            dao = new ObjectDao(null, config);
            fail("should throw exception");
        } catch (IllegalArgumentException e) {
            //ignore, expected
        }

        try {
            dao = new ObjectDao(connection, null);
            fail("should throw exception");
        } catch (IllegalArgumentException e) {
            //ignore, expected
        }
    }

    public void testSetAutoCommit() throws Exception {
        IPersistenceConfiguration config = new PersistenceConfiguration(new PersistenceManager());
        Connection connection = (Connection) MockFactory.createProxy(Connection.class);
        IObjectDao dao = new ObjectDao(connection, config);
        IMock mock = MockFactory.getMock(connection);

        dao.setAutoCommit(true);
        mock.assertInvoked(new MethodInvocation("setAutoCommit", boolean.class, new Boolean(true)));

        dao.setAutoCommit(false);
        mock.assertInvoked(new MethodInvocation("setAutoCommit", boolean.class, new Boolean(false)));
    }

    public void testCommit() throws Exception {
        IPersistenceConfiguration config = new PersistenceConfiguration(new PersistenceManager());
        Connection connection = (Connection) MockFactory.createProxy(Connection.class);
        IObjectDao dao = new ObjectDao(connection, config);
        IMock proxy = MockFactory.getMock(connection);

        proxy.assertNotInvoked(new MethodInvocation("commit"));

        dao.commit();
        proxy.assertInvoked(new MethodInvocation("commit"));
    }

    public void testRollback() throws Exception {
        IPersistenceConfiguration config = new PersistenceConfiguration(new PersistenceManager());
        Connection connection = (Connection) MockFactory.createProxy(Connection.class);
        IObjectDao dao = new ObjectDao(connection, config);
        IMock proxy = MockFactory.getMock(connection);

        proxy.assertNotInvoked(new MethodInvocation("rollback"));

        dao.rollback();
        proxy.assertInvoked(new MethodInvocation("rollback"));
    }

    public void testObjectMappingKey() throws Exception{
        try {
            ObjectMappingKey objectMappingKey = ObjectMappingKey.createInstance(PersistentObject.class);
            this.dao.getConfiguration().getObjectMappingCache().clear();

            PersistentObject object = (PersistentObject) this.dao.read(objectMappingKey, "select * from persistent_object");

            objectMappingKey = ObjectMappingKey.createInstance(null);
            try {
                object = (PersistentObject) this.dao.read(objectMappingKey, "select * from persistent_object");
                fail("should throw exception on null object class in object mapping key");
            } catch (PersistenceException e) {
                //ignore, fail expected
            }
            try {
                this.dao.read("", "select * from persistent_object");
                fail("should throw exception on unknown object mapping key");
            } catch (PersistenceException e) {
                //ignore, fail expected
            }
        } finally {
            Environment.executeSql(DELETE);
        }
    }

    public void testObjectMappingKey_customMapper() throws Exception {
        try {
            ICustomObjectMapper mapper = (ICustomObjectMapper) MockFactory.createProxy(ICustomObjectMapper.class);
            IMock proxy = MockFactory.getMock(mapper);

            ObjectMappingKey objectMappingKey = ObjectMappingKey.createInstance(PersistentObject.class, mapper);

            dao.getConfiguration().getObjectMappingCache().clear();
            PersistentObject object = (PersistentObject) dao.read(objectMappingKey, "select * from persistent_object");

            proxy.assertInvoked(new MethodInvocation("getObjectMapping", new Class[]{Object.class}));
            proxy.assertInvoked(new MethodInvocation("getTableName"    , new Class[]{Object.class}));
            proxy.assertInvoked(new MethodInvocation("modify"          , new Class[]{Object.class, IObjectMapping.class}));
            proxy.clear();

            object = (PersistentObject) dao.read(objectMappingKey, "select * from persistent_object");

            proxy.assertNotInvoked(new MethodInvocation("getObjectMapping", new Class[]{Object.class}));
            proxy.assertNotInvoked(new MethodInvocation("getTableName"    , new Class[]{Object.class}));
            proxy.assertNotInvoked(new MethodInvocation("modify"          , new Class[]{Object.class, IObjectMapping.class}));

            //check it doesn't fail with custom object mapperthat returns an object mapping.
            ICustomObjectMapper mapper2 = new CustomObjectMapperBase(){
                public IObjectMapping getObjectMapping(Object objectMappingKey) throws PersistenceException {
                    return dao.getConfiguration()
                              .getObjectMapper()
                              .mapToTable(PersistentObject.class, null, dao.getConnection(), null, null);
                }
            };

            objectMappingKey = ObjectMappingKey.createInstance(PersistentObject.class, mapper2);
            object = (PersistentObject) dao.read(objectMappingKey, "select * from persistent_object");
        } finally {
            Environment.executeSql(DELETE);
        }
    }

    public void testConfiguration() throws Exception{
        IPersistenceConfiguration config     = (IPersistenceConfiguration) MockFactory.createProxy(new PersistenceConfiguration(new PersistenceManager()));
        Connection                connection = null;

        try {
            connection = Environment.getConnection();
            Database.setDatabaseOnConfiguration(config, connection);

            assertTrue(connection instanceof Connection);
            Object conProxy = MockFactory.createProxy(connection, Connection.class);
            assertTrue(conProxy instanceof Connection);
            connection = (Connection               ) conProxy;
            IMock proxy = MockFactory.getMock(config);

            IObjectDao dao = new ObjectDao(connection, config);

            config.setObjectMappingCache(null);
            dao.read(PersistentObject.class, "select * from persistent_object");

            config.setObjectMapper(null);
            try {
                dao.read(PersistentObject.class, "select * from persistent_object");
                fail("should throw exception on null object mapper");
            } catch (PersistenceException e) {
                //ignore, fail expected
            }
            config.setObjectMapper(new ObjectMapper(new ObjectMappingFactory()));

            //should not fail.
            dao.read(PersistentObject.class, "select * from persistent_object");

            config.setObjectReader(null);
            try {
                dao.read(PersistentObject.class, "select * from persistent_object");
                fail("should throw exception on null object reader");
            } catch (PersistenceException e) {
                //ignore, fail expected
            }
            config.setObjectReader(new ObjectReader());

            config.setSqlGenerator(null);
            config.getReadByPrimaryKeySqlCache().clear();
            try {
                dao.readByPrimaryKey(PersistentObject.class, new Long(2));
                fail("should throw exception on null sql generator");
            } catch (PersistenceException e) {
                //ignore, fail expected
            }
            config.setSqlGenerator(new SqlGenerator());

            config.setReadByPrimaryKeySqlCache(null);
            dao.readByPrimaryKey(PersistentObject.class, new Long(2));
            config.setReadByPrimaryKeySqlCache(new SqlCache());

            config.setObjectWriter(null);
            Environment.executeSql(INSERT1);
            PersistentObject object = (PersistentObject) this.dao.readByPrimaryKey(PersistentObject.class, new Long(1));

            try {
                dao.update(object);
                fail("should throw exception when no object writer set.");
            } catch (PersistenceException e) {
                //ignore, fail expected
            }
            config.setObjectWriter(new ObjectWriter());

        } finally {
            JdbcUtil.close(connection);
            Environment.executeSql(DELETE);
        }
    }

    public void testSqlCaching() throws Exception {
        IPersistenceConfiguration config     = (IPersistenceConfiguration) MockFactory.createProxy(new PersistenceConfiguration(new PersistenceManager()));
        Connection                connection = null;

        try {
            connection = (Connection               ) MockFactory.createProxy(Environment.getConnection(), Connection.class);
            Database.setDatabaseOnConfiguration(config, connection);
            IMock proxy = MockFactory.getMock(config);

            IObjectDao dao = new ObjectDao(connection, config);

            config.getReadByPrimaryKeySqlCache().clear();
            dao.read(PersistentObject.class, "select * from persistent_object");

            config.getReadByPrimaryKeySqlCache().clear();
            dao.deleteByPrimaryKey(PersistentObject.class, new Long(2));

        } finally {
            Environment.executeSql(DELETE);
            JdbcUtil.close(connection);
        }

    }







    //==============================================================
    // Test read / write methods
    //==============================================================
    public void testReadByPrimaryKey() throws Exception {
        try {
            Environment.executeSql(INSERT1);
            Environment.executeSql(INSERT2);

            PersistentObject object = (PersistentObject) dao.readByPrimaryKey(PersistentObject.class, new Long(1));
            assertEquals("id wrong"        , 1, object.getId());
            assertEquals("name wrong"      , "name1" , object.getName());

            object = (PersistentObject) dao.readByPrimaryKey(PersistentObject.class, new Long(2));
            assertEquals("id wrong"        , 2, object.getId());
            assertEquals("name wrong"      , "name2" , object.getName());

            object = (PersistentObject) dao.readByPrimaryKey(PersistentObject.class, new Long(3));
            assertNull(object);
        } finally {
            dao.closeConnection();
            Environment.executeSql(DELETE);
        }
    }


    public void testRead_Sql() throws Exception{
        try {
            Environment.executeSql(INSERT1);
            Environment.executeSql(INSERT2);

            String sql = "select * from persistent_object where id=1";
            PersistentObject object = (PersistentObject) dao.read(PersistentObject.class, sql);
            assertEquals("id wrong"        , 1, object.getId());
            assertEquals("name wrong"      , "name1" , object.getName());

            sql = "select * from persistent_object where id=2";
            object = (PersistentObject) dao.read(PersistentObject.class, sql);
            assertEquals("id wrong"        , 2, object.getId());
            assertEquals("name wrong"      , "name2" , object.getName());

            sql = "select * from persistent_object order by id";
            object = (PersistentObject) dao.read(PersistentObject.class, sql);
            assertEquals("id wrong"        , 1, object.getId());
            assertEquals("name wrong"      , "name1" , object.getName());

            sql = "select * from persistent_object where id > 200";
            object = (PersistentObject) dao.read(PersistentObject.class, sql);
            assertNull(object);

            //test Class<T> version of method - no casting necessary.
            sql = "select * from persistent_object where id=1";
            object = dao.read(PersistentObject.class, sql);
            assertEquals("id wrong"        , 1, object.getId());
            assertEquals("name wrong"      , "name1" , object.getName());
        } finally {
            dao.closeConnection();
            Environment.executeSql(DELETE);
        }
    }

    public void testRead_ResultSet() throws Exception {
        String           sql1        = "select * from persistent_object where id = 1";
        PersistentObject object     = null;
        Connection       connection = null;
        Statement        statement  = null;
        ResultSet        result     = null;
        try{
            Environment.executeSql(INSERT1);
            Environment.executeSql(INSERT2);

            connection = this.dao.getConnection();
            statement   = connection.createStatement();

            result      = statement.executeQuery(sql1);
            result.next();
            object      = (PersistentObject) dao.read(PersistentObject.class, result);
            assertEquals("id wrong"        , 1, object.getId());
            assertEquals("name wrong"      , "name1" , object.getName());

        } finally {
            dao.closeConnection();
            Environment.executeSql(DELETE);
            JdbcUtil.closeIgnore(result);
            JdbcUtil.closeIgnore(statement);
            JdbcUtil.closeIgnore(connection);
        }
    }

    public void testRead_Statement() throws Exception {
        String           sql1        = "select * from persistent_object where id = 1";
        String           sql2        = "select * from persistent_object where id > 200";

        PersistentObject object     = null;
        Connection       connection = null;
        Statement        statement  = null;
        try{
            Environment.executeSql(INSERT1);
            Environment.executeSql(INSERT2);

            connection = connection = this.dao.getConnection();
            statement   = connection.createStatement();

            object      = (PersistentObject) dao.read(PersistentObject.class, statement, sql1);
            assertEquals("id wrong"        , 1, object.getId());
            assertEquals("name wrong"      , "name1" , object.getName());

            object      = (PersistentObject) dao.read(PersistentObject.class, statement, sql2);
            assertNull("sql should return any records", object);
        } finally {
            dao.closeConnection();
            Environment.executeSql(DELETE);
            JdbcUtil.closeIgnore(statement);
            JdbcUtil.closeIgnore(connection);
        }
    }

    public void testRead_PreparedStatement() throws Exception {
        String           sql1        = "select * from persistent_object where id = ?";
        String           sql2        = "select * from persistent_object where id > ?";

        PersistentObject  object     = null;
        Connection        connection = null;
        PreparedStatement statement  = null;
        try{
            Environment.executeSql(INSERT1);
            Environment.executeSql(INSERT2);

            connection = connection = this.dao.getConnection();
            statement   = connection.prepareStatement(sql1);
            statement.setLong(1, 1);

            object      = (PersistentObject) dao.read(PersistentObject.class, statement);
            assertEquals("id wrong"        , 1, object.getId());
            assertEquals("name wrong"      , "name1" , object.getName());

            statement   = connection.prepareStatement(sql2);
            statement.setLong(1, 200);
            object      = (PersistentObject) dao.read(PersistentObject.class, statement);
            assertNull("sql should not return any records", object);

        } finally {
            dao.closeConnection();
            Environment.executeSql(DELETE);
            JdbcUtil.closeIgnore(statement);
            JdbcUtil.closeIgnore(connection);
        }
    }

    public void testRead_Collection() throws Exception {
        String           sql1        = "select * from persistent_object where id = ?";
        String           sql2        = "select * from persistent_object where id > ?";

        try{
            Environment.executeSql(INSERT1);
            Environment.executeSql(INSERT2);

            PersistentObject object = null;
            List parameters = new ArrayList();
            parameters.add(new Long(1));

            object      = (PersistentObject) dao.read(PersistentObject.class, sql1, parameters);
            assertEquals("id wrong"        , 1, object.getId());
            assertEquals("name wrong"      , "name1" , object.getName());

            parameters.clear();
            parameters.add(new Long(200));
            object      = (PersistentObject) dao.read(PersistentObject.class, sql2, parameters);
            assertNull("sql should not return any records", object);

        } finally {
            dao.closeConnection();
            Environment.executeSql(DELETE);
        }
    }

    public void testRead_Array() throws Exception {
        String           sql1        = "select * from persistent_object where id = ?";
        String           sql2        = "select * from persistent_object where id > ?";

        try{
            Environment.executeSql(INSERT1);
            Environment.executeSql(INSERT2);

            PersistentObject object = null;
            Object[] parameters = new Object[1];
            parameters[0] = new Long(1);

            object      = (PersistentObject) dao.read(PersistentObject.class, sql1, parameters);
            assertEquals("id wrong"        , 1, object.getId());
            assertEquals("name wrong"      , "name1" , object.getName());

            parameters[0] = new Long(200);
            object      = (PersistentObject) dao.read(PersistentObject.class, sql2, parameters);
            assertNull("sql should not return any records", object);

        } finally {
            dao.closeConnection();
            Environment.executeSql(DELETE);
        }
    }


    public void testReadListByPrimaryKeys() throws Exception{
        try{
            Environment.executeSql(INSERT1);
            Environment.executeSql(INSERT2);
            Environment.executeSql(INSERT3);

            List primaryKeys = new ArrayList();
            primaryKeys.add(new Long(1));
            primaryKeys.add(new Long(2));

            List objects = this.dao.readListByPrimaryKeys(PersistentObject.class, primaryKeys);
            assertEquals("wrong number of records read", 2, objects.size());

            PersistentObject object = null;
            object = (PersistentObject) objects.get(0);

            assertEquals("wrong name", "name1", object.getName());
            assertNull("wrong object value", object.getObjectValue());

            object = (PersistentObject) objects.get(1);

            assertEquals("wrong id", 2, object.getId());
            assertEquals("wrong name", "name2", object.getName());
            assertNull("wrong object value", object.getObjectValue());


            primaryKeys.add(new Long(4));
            objects = this.dao.readListByPrimaryKeys(PersistentObject.class, primaryKeys);
            assertEquals("wrong number of records read", 2, objects.size());

            primaryKeys.clear();
            objects = this.dao.readListByPrimaryKeys(PersistentObject.class, primaryKeys);
            assertEquals("wrong number of records read", 0, objects.size());
        } finally {
            dao.closeConnection();
            Environment.executeSql(DELETE);
        }

    }

    public void testReadList() throws Exception {
        try{
            Environment.executeSql(INSERT1);
            Environment.executeSql(INSERT2);
            Environment.executeSql(INSERT3);

            String sql1 = "select * from persistent_object order by id";
            String sql2 = "select * from persistent_object where id > 1";
            String sql3 = "select * from persistent_object where id > 200";

            List objects = this.dao.readList(PersistentObject.class, sql1);
            assertEquals("wrong number of objects read", 3, objects.size());

            assertObject1Correct(objects.get(0));
            assertObject2Correct(objects.get(1));
            assertObject3Correct(objects.get(2));

            objects = this.dao.readList(PersistentObject.class, sql2);
            assertEquals("wrong number of objects read", 2, objects.size());

            assertObject2Correct(objects.get(0));
            assertObject3Correct(objects.get(1));

            objects = this.dao.readList(PersistentObject.class, sql3);
            assertEquals("list should be empty", 0, objects.size());

        } finally{
            dao.closeConnection();
            Environment.executeSql(DELETE);
        }
    }

    public void testReadList_ResultSet() throws Exception{

        Connection connection = null;
        Statement  statement  = null;
        ResultSet  result     = null;

        try{
            Environment.executeSql(INSERT1);
            Environment.executeSql(INSERT2);
            Environment.executeSql(INSERT3);

            String sql1 = "select * from persistent_object order by id";
            String sql2 = "select * from persistent_object where id > 1";
            String sql3 = "select * from persistent_object where id > 200";

            connection = connection = this.dao.getConnection();
            statement  = connection.createStatement();


            result       = statement.executeQuery(sql1);
            List objects = this.dao.readList(PersistentObject.class, result);
            assertEquals("wrong number of objects read", 3, objects.size());
            result.close();

            assertObject1Correct(objects.get(0));
            assertObject2Correct(objects.get(1));
            assertObject3Correct(objects.get(2));


            result       = statement.executeQuery(sql2);
            objects = this.dao.readList(PersistentObject.class, result);
            assertEquals("wrong number of objects read", 2, objects.size());
            result.close();

           assertObject2Correct(objects.get(0));
           assertObject3Correct(objects.get(1));


            result       = statement.executeQuery(sql3);
            objects = this.dao.readList(PersistentObject.class, result);
            assertEquals("list should be empty", 0, objects.size());
        } finally{
            Environment.executeSql(DELETE);
            JdbcUtil.close(result);
            JdbcUtil.close(statement);
            JdbcUtil.close(connection);
        }
    }


   public void testReadList_Statement() throws Exception{
        Connection connection = null;
        Statement  statement  = null;

        try{
            Environment.executeSql(INSERT1);
            Environment.executeSql(INSERT2);
            Environment.executeSql(INSERT3);

            String sql1 = "select * from persistent_object order by id";
            String sql2 = "select * from persistent_object where id > 1";
            String sql3 = "select * from persistent_object where id > 200";

            connection = connection = this.dao.getConnection();
            statement  = connection.createStatement();

            List objects = this.dao.readList(PersistentObject.class, statement, sql1);
            assertEquals("wrong number of objects read", 3, objects.size());

            assertObject1Correct(objects.get(0));
            assertObject2Correct(objects.get(1));
            assertObject3Correct(objects.get(2));

            objects = this.dao.readList(PersistentObject.class, statement, sql2);
            assertEquals("wrong number of objects read", 2, objects.size());

            assertObject2Correct(objects.get(0));
            assertObject3Correct(objects.get(1));

            objects = this.dao.readList(PersistentObject.class, statement, sql3);
            assertEquals("list should be empty", 0, objects.size());
        } finally{
            dao.closeConnection();
            Environment.executeSql(DELETE);
            JdbcUtil.closeIgnore(statement);
            JdbcUtil.closeIgnore(connection);
        }
    }

    public void testReadList_PreparedStatement() throws Exception{
        Connection connection = null;
        PreparedStatement  statement  = null;

        try{
            Environment.executeSql(INSERT1);
            Environment.executeSql(INSERT2);
            Environment.executeSql(INSERT3);

            String sql1 = "select * from persistent_object order by id";
            String sql2 = "select * from persistent_object where id > ?";
            String sql3 = "select * from persistent_object where id > ?";

            connection =connection = this.dao.getConnection();
            statement  = connection.prepareStatement(sql1);

            List objects = this.dao.readList(PersistentObject.class, statement);
            assertEquals("wrong number of objects read", 3, objects.size());

            assertObject1Correct(objects.get(0));
            assertObject2Correct(objects.get(1));
            assertObject3Correct(objects.get(2));

            statement = connection.prepareStatement(sql2);
            statement.setLong(1,1);
            objects = this.dao.readList(PersistentObject.class, statement);
            assertEquals("wrong number of objects read", 2, objects.size());

            assertObject2Correct(objects.get(0));
            assertObject3Correct(objects.get(1));

            statement = connection.prepareStatement(sql3);
            statement.setLong(1,200);
            objects = this.dao.readList(PersistentObject.class, statement);
            assertEquals("list should be empty", 0, objects.size());
        } finally{
            dao.closeConnection();
            Environment.executeSql(DELETE);
            JdbcUtil.closeIgnore(statement);
            JdbcUtil.closeIgnore(connection);
        }
    }

    public void testReadList_Collection() throws Exception {
        String sql1 = "select * from persistent_object where id = ?";
        String sql2 = "select * from persistent_object where name like ? and objectValue like ? order by name";

        try{
            Environment.executeSql(INSERT1);
            Environment.executeSql(INSERT2);
            Environment.executeSql(INSERT3);

            List parameters = new ArrayList();
            parameters.add(new Long(1));

            List objects = this.dao.readList(PersistentObject.class, sql1, parameters);
            assertEquals("only one object should be read", 1, objects.size());
            assertObject1Correct(objects.get(0));

            parameters.clear();
            parameters.add("name%");
            parameters.add("value%");
            objects = this.dao.readList(PersistentObject.class, sql2, parameters);
            assertEquals("all objects expected", 3, objects.size());
            assertObject1Correct(objects.get(0));
            assertObject2Correct(objects.get(1));
            assertObject3Correct(objects.get(2));

        } finally {
            dao.closeConnection();
            Environment.executeSql(DELETE);
        }
    }

    public void testReadList_Array() throws Exception {
        String sql1 = "select * from persistent_object where id = ?";
        String sql2 = "select * from persistent_object where name like ? and objectValue like ? order by name";

        try{
            Environment.executeSql(INSERT1);
            Environment.executeSql(INSERT2);
            Environment.executeSql(INSERT3);

            Object[] parameters = new Object[1];
            parameters[0] = new Long(1);

            List objects = this.dao.readList(PersistentObject.class, sql1, parameters);
            assertEquals("only one object should be read", 1, objects.size());
            assertObject1Correct(objects.get(0));

            parameters = new Object[2];
            parameters[0] = "name%";
            parameters[1] = "value%";
            objects = this.dao.readList(PersistentObject.class, sql2, parameters);
            assertEquals("all objects expected", 3, objects.size());
            assertObject1Correct(objects.get(0));
            assertObject2Correct(objects.get(1));
            assertObject3Correct(objects.get(2));


        } finally {
            dao.closeConnection();
            Environment.executeSql(DELETE);
        }
    }


    public void testReadList_Filtered() throws Exception{
        try{
            Environment.executeSql(INSERT1);
            Environment.executeSql(INSERT2);
            Environment.executeSql(INSERT3);

            String sql1 = "select * from persistent_object order by id";
            String sql2 = "select * from persistent_object where id > 1";
            String sql3 = "select * from persistent_object where id > 200";

            IReadFilter filter = new AcceptEveryOtherFilter(5);

            List objects = this.dao.readList(PersistentObject.class, sql1, filter);
            assertEquals("wrong number of objects read", 2, objects.size());

            assertObject1Correct(objects.get(0));
            assertObject3Correct(objects.get(1));

            filter = new AcceptEveryOtherFilter(5);
            objects = this.dao.readList(PersistentObject.class, sql2, filter);
            assertEquals("wrong number of objects read", 1, objects.size());

            assertObject2Correct(objects.get(0));

            filter = new AcceptEveryOtherFilter(5);
            objects = this.dao.readList(PersistentObject.class, sql3, filter);
            assertEquals("list should be empty", 0, objects.size());
        } finally{
            dao.closeConnection();
            Environment.executeSql(DELETE);
        }
    }

    public void testReadList_ResultSet_Filtered() throws Exception{

        Connection connection = null;
        Statement  statement  = null;
        ResultSet  result     = null;

        try{
            Environment.executeSql(INSERT1);
            Environment.executeSql(INSERT2);
            Environment.executeSql(INSERT3);

            String sql1 = "select * from persistent_object order by id";
            String sql2 = "select * from persistent_object where id > 1";
            String sql3 = "select * from persistent_object where id > 200";

            IReadFilter filter = new AcceptEveryOtherFilter(5);

            connection = connection = this.dao.getConnection();
            statement  = connection.createStatement();

            result       = statement.executeQuery(sql1);
            List objects = this.dao.readList(PersistentObject.class, result, filter);
            assertEquals("wrong number of objects read", 2, objects.size());
            result.close();

            assertObject1Correct(objects.get(0));
            assertObject3Correct(objects.get(1));


            filter = new AcceptEveryOtherFilter(5);
            result       = statement.executeQuery(sql2);
            objects = this.dao.readList(PersistentObject.class, result, filter);
            assertEquals("wrong number of objects read", 1, objects.size());
            result.close();

            assertObject2Correct(objects.get(0));

            filter = new AcceptEveryOtherFilter(5);
            result       = statement.executeQuery(sql3);
            objects = this.dao.readList(PersistentObject.class, result, filter);
            assertEquals("list should be empty", 0, objects.size());
        } finally{
            dao.closeConnection();
            Environment.executeSql(DELETE);
            JdbcUtil.closeIgnore(result);
            JdbcUtil.closeIgnore(statement);
            JdbcUtil.closeIgnore(connection);
        }
    }

    public void testReadList_Statement_Filtered() throws Exception{
        Connection connection = null;
        Statement  statement  = null;

        try{
            Environment.executeSql(INSERT1);
            Environment.executeSql(INSERT2);
            Environment.executeSql(INSERT3);

            String sql1 = "select * from persistent_object order by id";
            String sql2 = "select * from persistent_object where id > 1";
            String sql3 = "select * from persistent_object where id > 200";

            IReadFilter filter = new AcceptEveryOtherFilter(5);

            connection = this.dao.getConnection();
            statement  = connection.createStatement();

            List objects = this.dao.readList(PersistentObject.class, statement, sql1, filter);
            assertEquals("wrong number of objects read", 2, objects.size());

            assertObject1Correct(objects.get(0));
            assertObject3Correct(objects.get(1));

            filter = new AcceptEveryOtherFilter(5);
            objects = this.dao.readList(PersistentObject.class, statement, sql2, filter);
            assertEquals("wrong number of objects read", 1, objects.size());

            assertObject2Correct(objects.get(0));

            filter = new AcceptEveryOtherFilter(5);
            objects = this.dao.readList(PersistentObject.class, statement, sql3, filter);
            assertEquals("list should be empty", 0, objects.size());
        } finally{
            dao.closeConnection();
            Environment.executeSql(DELETE);
            JdbcUtil.closeIgnore(statement);
            JdbcUtil.closeIgnore(connection);
        }
    }


    public void testReadList_PreparedStatement_Filtered() throws Exception{
        Connection connection = null;
        PreparedStatement  statement  = null;

        try{
            Environment.executeSql(INSERT1);
            Environment.executeSql(INSERT2);
            Environment.executeSql(INSERT3);

            String sql1 = "select * from persistent_object order by id";
            String sql2 = "select * from persistent_object where id > ?";
            String sql3 = "select * from persistent_object where id > ?";

            IReadFilter filter = new AcceptEveryOtherFilter(5);

            connection = this.dao.getConnection();
            statement  = connection.prepareStatement(sql1);

            List objects = this.dao.readList(PersistentObject.class, statement, filter);
            assertEquals("wrong number of objects read", 2, objects.size());

            assertObject1Correct(objects.get(0));
            assertObject3Correct(objects.get(1));

            statement = connection.prepareStatement(sql2);
            statement.setLong(1,1);
            filter = new AcceptEveryOtherFilter(5);
            objects = this.dao.readList(PersistentObject.class, statement, filter);
            assertEquals("wrong number of objects read", 1, objects.size());

            assertObject2Correct(objects.get(0));

            statement = connection.prepareStatement(sql3);
            statement.setLong(1,200);
            filter = new AcceptEveryOtherFilter(5);
            objects = this.dao.readList(PersistentObject.class, statement, filter);
            assertEquals("list should be empty", 0, objects.size());
        } finally{
            dao.closeConnection();
            Environment.executeSql(DELETE);
            JdbcUtil.closeIgnore(statement);
            JdbcUtil.closeIgnore(connection);
        }
    }


    public void testReadList_Collection_Filtered() throws Exception {
        String sql1 = "select * from persistent_object where id = ?";
        String sql2 = "select * from persistent_object where name like ? and objectValue like ? order by id";

        try{
            Environment.executeSql(INSERT1);
            Environment.executeSql(INSERT2);
            Environment.executeSql(INSERT3);

            List parameters = new ArrayList();
            parameters.add(new Long(1));

            List objects = this.dao.readList(PersistentObject.class, sql1,
                    new AcceptEveryOtherFilter(3), parameters);
            assertEquals("only one object should be read", 1, objects.size());
            assertObject1Correct(objects.get(0));

            parameters.clear();
            parameters.add("name%");
            parameters.add("value%");
            objects = this.dao.readList(PersistentObject.class, sql2,
                    new AcceptEveryOtherFilter(3), parameters);
            assertEquals("all objects expected", 2, objects.size());
            assertObject1Correct(objects.get(0));
            assertObject3Correct(objects.get(1));

        } finally {
            dao.closeConnection();
            Environment.executeSql(DELETE);
        }
    }

    public void testReadList_Array_Filtered() throws Exception {
        String sql1 = "select * from persistent_object where id = ?";
        String sql2 = "select * from persistent_object where name like ? and objectValue like ? order by id";

        try{
            Environment.executeSql(INSERT1);
            Environment.executeSql(INSERT2);
            Environment.executeSql(INSERT3);

            List objects = this.dao.readList(PersistentObject.class, sql1,
                    new AcceptEveryOtherFilter(3), new Long(1));
            assertEquals("only one object should be read", 1, objects.size());
            assertObject1Correct(objects.get(0));

            objects = this.dao.readList(PersistentObject.class, sql2,
                    new AcceptEveryOtherFilter(3), "name%", "value%");
            assertEquals("all objects expected", 2, objects.size());
            assertObject1Correct(objects.get(0));
            assertObject3Correct(objects.get(1));

        } finally {
            dao.closeConnection();
            Environment.executeSql(DELETE);
        }
    }

    //*******************************************
    // Update method tests
    //*******************************************

    public void testInsert() throws Exception{
        PersistentObject object1     = createPersistentObject(1, "name1");
        PersistentObject object2     = createPersistentObject(2, "name2");
        PersistentObject objectRead  = null;
        try{
            this.dao.insert(object1);
            this.dao.insert(object2);

            objectRead = (PersistentObject) this.dao.readByPrimaryKey(PersistentObject.class, new Long(1));
            assertObject1Correct(objectRead);

            objectRead = (PersistentObject) this.dao.readByPrimaryKey(PersistentObject.class, new Long(2));
            assertObject2Correct(objectRead);

        }   finally {
            dao.closeConnection();
            Environment.executeSql(DELETE);
        }
    }

    public void testInsert_ObjectMappingKey() throws Exception {
        PersistentObject object1     = createPersistentObject(1, "name1");
        PersistentObject object2     = createPersistentObject(2, "name2");
        PersistentObject objectRead  = null;
        try{
            this.dao.insert(PersistentObject.class, object1);
            this.dao.insert(PersistentObject.class, object2);

            objectRead = (PersistentObject) this.dao.readByPrimaryKey(PersistentObject.class, new Long(1));
            assertObject1Correct(objectRead);

            objectRead = (PersistentObject) this.dao.readByPrimaryKey(PersistentObject.class, new Long(2));
            assertObject2Correct(objectRead);
        }   finally {
            dao.closeConnection();
            Environment.executeSql(DELETE);
        }
    }


    public void testInsert_AutoIncrementedId() throws Exception {
        if(! Environment.database.isPrepareStatementStatement_RETURN_GENERATED_KEYS_supported()) return;

        PersistenceManager persistenceManager = new PersistenceManager(Environment.getDataSource());
        TableWithAutoIncrement object1 = new TableWithAutoIncrement();

        IObjectMapping mapping = null;
        IDaos daos = null;
        try {
            this.dao.insert(TableWithAutoIncrement.class, object1);
            UpdateResult result = this.dao.getUpdateResult(0);
            assertNotNull(result);
            assertEquals(1, result.getGeneratedKeyAsLong(0));

        } finally {
            Environment.executeSql("delete from TableWithAutoIncrement");
        }
    }


    public void testInsertBatch() throws Exception {
        PersistentObject object1 = createPersistentObject(1, "name1");
        PersistentObject object2 = createPersistentObject(2, "name2");
        PersistentObject object3 = createPersistentObject(3, "name3");

        try{
            Environment.executeSql(DELETE);
            List objects = new ArrayList();
            objects.add(object1);
            objects.add(object2);
            objects.add(object3);

            int[] recordsAffected = this.dao.insertBatch(objects);
            assertEquals(3, recordsAffected.length);

            List objectsRead = this.dao.readList(PersistentObject.class, "select * from persistent_object order by id");

            assertEquals("wrong number of objects read", 3, objectsRead.size());
            assertObject1Correct((PersistentObject) objectsRead.get(0));
            assertObject2Correct((PersistentObject) objectsRead.get(1));
            assertObject3Correct((PersistentObject) objectsRead.get(2));

            objects.clear();
            recordsAffected = this.dao.insertBatch(objects);
            assertEquals(0, recordsAffected.length);
        } finally {
            dao.closeConnection();
            Environment.executeSql(DELETE);
        }
    }


    public void testInsertBatch_ObjectMappingKey() throws Exception {
        PersistentObject object1 = createPersistentObject(1, "name1");
        PersistentObject object2 = createPersistentObject(2, "name2");
        PersistentObject object3 = createPersistentObject(3, "name3");

        try{
            List objects = new ArrayList();
            objects.add(object1);
            objects.add(object2);
            objects.add(object3);

            this.dao.insertBatch(PersistentObject.class, objects);

            List objectsRead = this.dao.readList(PersistentObject.class, "select * from persistent_object order by id");

            assertEquals("wrong number of objects read", 3, objectsRead.size());
            assertObject1Correct((PersistentObject) objectsRead.get(0));
            assertObject2Correct((PersistentObject) objectsRead.get(1));
            assertObject3Correct((PersistentObject) objectsRead.get(2));

        } finally {
            dao.closeConnection();
            Environment.executeSql(DELETE);
        }
    }

    public void testUpdate() throws Exception {
        PersistentObject object1    = createPersistentObject(1, "name1");
        PersistentObject object2    = createPersistentObject(2, "name2");
        PersistentObject objectRead = null;

        try{
            this.dao.insert(object1);
            this.dao.insert(object2);

            object1.setName("name11");
            this.dao.update(object1);

            objectRead = (PersistentObject) this.dao.readByPrimaryKey(PersistentObject.class, new Long(1));
            assertEquals("id wrong", 1, objectRead.getId());
            assertEquals("name wrong", "name11", objectRead.getName());

            object2.setName("name22");
            this.dao.update(object2);
            objectRead = (PersistentObject) this.dao.readByPrimaryKey(PersistentObject.class, new Long(1));
            assertEquals("id wrong", 1, objectRead.getId());
            assertEquals("name wrong", "name11", objectRead.getName());

            objectRead = (PersistentObject) this.dao.readByPrimaryKey(PersistentObject.class, new Long(2));
            assertEquals("id wrong", 2, objectRead.getId());
            assertEquals("name wrong", "name22", objectRead.getName());
        } finally {
            dao.closeConnection();
            Environment.executeSql(DELETE);
        }

    }


    public void testUpdate_ObjectMappingKey() throws Exception {
        PersistentObject object1    = createPersistentObject(1, "name1");
        PersistentObject object2    = createPersistentObject(2, "name2");
        PersistentObject objectRead = null;

        try{
            this.dao.insert(object1);
            this.dao.insert(object2);

            object1.setName("name11");
            this.dao.update(PersistentObject.class, object1);

            objectRead = (PersistentObject) this.dao.readByPrimaryKey(PersistentObject.class, new Long(1));
            assertEquals("id wrong", 1, objectRead.getId());
            assertEquals("name wrong", "name11", objectRead.getName());

            object2.setName("name22");
            this.dao.update(PersistentObject.class, object2);
            objectRead = (PersistentObject) this.dao.readByPrimaryKey(PersistentObject.class, new Long(1));
            assertEquals("id wrong", 1, objectRead.getId());
            assertEquals("name wrong", "name11", objectRead.getName());

            objectRead = (PersistentObject) this.dao.readByPrimaryKey(PersistentObject.class, new Long(2));
            assertEquals("id wrong", 2, objectRead.getId());
            assertEquals("name wrong", "name22", objectRead.getName());

        } finally {
            dao.closeConnection();
            Environment.executeSql(DELETE);
        }
    }


    public void testUpdateByPrimaryKey() throws Exception {
        PersistentObject object1    = createPersistentObject(1, "name1");
        PersistentObject object2    = createPersistentObject(2, "name2");
        PersistentObject objectRead = null;

        try{
            this.dao.insert(object1);
            this.dao.insert(object2);

            object1.setId(11);
            object1.setName("name11");
            this.dao.updateByPrimaryKey(object1, new Long(1));

            objectRead = (PersistentObject) this.dao.readByPrimaryKey(PersistentObject.class, new Long(11));
            assertEquals("id wrong", 11, objectRead.getId());
            assertEquals("name wrong", "name11", objectRead.getName());

            object2.setId(22);
            object2.setName("name22");
            this.dao.updateByPrimaryKey(object2, new Long(2));
            objectRead = (PersistentObject) this.dao.readByPrimaryKey(PersistentObject.class, new Long(11));
            assertEquals("id wrong", 11, objectRead.getId());
            assertEquals("name wrong", "name11", objectRead.getName());

            objectRead = (PersistentObject) this.dao.readByPrimaryKey(PersistentObject.class, new Long(22));
            assertEquals("id wrong", 22, objectRead.getId());
            assertEquals("name wrong", "name22", objectRead.getName());

        } finally {
            dao.closeConnection();
            Environment.executeSql(DELETE);
        }
    }


    public void testUpdateByPrimaryKey_ObjectMappingKey() throws Exception {
        PersistentObject object1    = createPersistentObject(1, "name1");
        PersistentObject object2    = createPersistentObject(2, "name2");
        PersistentObject objectRead = null;

        try{
            this.dao.insert(object1);
            this.dao.insert(object2);

            object1.setId(11);
            object1.setName("name11");
            this.dao.updateByPrimaryKey(PersistentObject.class, object1, new Long(1));

            objectRead = (PersistentObject) this.dao.readByPrimaryKey(PersistentObject.class, new Long(11));
            assertEquals("id wrong", 11, objectRead.getId());
            assertEquals("name wrong", "name11", objectRead.getName());

            object2.setId(22);
            object2.setName("name22");
            this.dao.updateByPrimaryKey(PersistentObject.class, object2, new Long(2));
            objectRead = (PersistentObject) this.dao.readByPrimaryKey(PersistentObject.class, new Long(11));
            assertEquals("id wrong", 11, objectRead.getId());
            assertEquals("name wrong", "name11", objectRead.getName());

            objectRead = (PersistentObject) this.dao.readByPrimaryKey(PersistentObject.class, new Long(22));
            assertEquals("id wrong", 22, objectRead.getId());
            assertEquals("name wrong", "name22", objectRead.getName());
        } finally {
            dao.closeConnection();
            Environment.executeSql(DELETE);
        }
    }


    public void testUpdateBatch() throws Exception {
        PersistentObject object1    = createPersistentObject(1, "name1");
        PersistentObject object2    = createPersistentObject(2, "name2");
        PersistentObject object3    = createPersistentObject(3, "name3");

        try {
            List objects = new ArrayList();
            objects.add(object1);
            objects.add(object2);
            objects.add(object3);
            this.dao.insertBatch(objects);

            object1.setName("name11");
            object2.setName("name22");
            object3.setName("name33");
            int[] recordsAffected = this.dao.updateBatch(objects);
            assertEquals(3, recordsAffected.length);

            List objectsRead = this.dao.readList(PersistentObject.class, "select * from persistent_object order by id");
            assertObjectCorrect(objectsRead.get(0),1, "name11");
            assertObjectCorrect(objectsRead.get(1),2, "name22");
            assertObjectCorrect(objectsRead.get(2),3, "name33");

            objects.clear();
            recordsAffected = this.dao.updateBatch(objects);
            assertEquals(0, recordsAffected.length);
        } finally {
            dao.closeConnection();
            Environment.executeSql(DELETE);
        }
    }

    public void testUpdateBatch_ObjectMappingKey() throws Exception {
        PersistentObject object1    = createPersistentObject(1, "name1");
        PersistentObject object2    = createPersistentObject(2, "name2");
        PersistentObject object3    = createPersistentObject(3, "name3");

        try {
            List objects = new ArrayList();
            objects.add(object1);
            objects.add(object2);
            objects.add(object3);
            this.dao.insertBatch(objects);

            object1.setName("name11");
            object2.setName("name22");
            object3.setName("name33");
            this.dao.updateBatch(PersistentObject.class, objects);

            List objectsRead = this.dao.readList(PersistentObject.class, "select * from persistent_object order by id");
            assertObjectCorrect(objectsRead.get(0),1, "name11");
            assertObjectCorrect(objectsRead.get(1),2, "name22");
            assertObjectCorrect(objectsRead.get(2),3, "name33");

        } finally {
            dao.closeConnection();
            Environment.executeSql(DELETE);
        }
    }

    public void testUpdateBatchByPrimaryKeys() throws Exception {
        PersistentObject object1    = createPersistentObject(1, "name1");
        PersistentObject object2    = createPersistentObject(2, "name2");
        PersistentObject object3    = createPersistentObject(3, "name3");

        try {
            List objects = new ArrayList();
            objects.add(object1);
            objects.add(object2);
            objects.add(object3);
            this.dao.insertBatch(objects);

            object1.setName("name11");
            object2.setName("name22");
            object3.setName("name33");
            object1.setId(11);
            object2.setId(22);
            object3.setId(33);
            List primaryKeys = new ArrayList();
            primaryKeys.add(new Long(1));
            primaryKeys.add(new Long(2));
            primaryKeys.add(new Long(3));
            int[] recordsAffected = this.dao.updateBatchByPrimaryKeys(objects, primaryKeys);

            List objectsRead = this.dao.readList(PersistentObject.class, "select * from persistent_object order by id");
            assertObjectCorrect(objectsRead.get(0),11, "name11");
            assertObjectCorrect(objectsRead.get(1),22, "name22");
            assertObjectCorrect(objectsRead.get(2),33, "name33");

            objects.clear();
            primaryKeys.clear();
            recordsAffected = this.dao.updateBatchByPrimaryKeys(objects, primaryKeys);
            assertEquals(0, recordsAffected.length);

        } finally {
            dao.closeConnection();
            Environment.executeSql(DELETE);
        }
    }


    public void testUpdateBatchByPrimaryKeys_ObjectMappingKey() throws Exception {
        PersistentObject object1    = createPersistentObject(1, "name1");
        PersistentObject object2    = createPersistentObject(2, "name2");
        PersistentObject object3    = createPersistentObject(3, "name3");

        try {
            List objects = new ArrayList();
            objects.add(object1);
            objects.add(object2);
            objects.add(object3);
            this.dao.insertBatch(objects);

            object1.setName("name11");
            object2.setName("name22");
            object3.setName("name33");
            object1.setId(11);
            object2.setId(22);
            object3.setId(33);
            List primaryKeys = new ArrayList();
            primaryKeys.add(new Long(1));
            primaryKeys.add(new Long(2));
            primaryKeys.add(new Long(3));
            this.dao.updateBatchByPrimaryKeys(PersistentObject.class, objects, primaryKeys);

            List objectsRead = this.dao.readList(PersistentObject.class, "select * from persistent_object order by id");
            assertObjectCorrect(objectsRead.get(0),11, "name11");
            assertObjectCorrect(objectsRead.get(1),22, "name22");
            assertObjectCorrect(objectsRead.get(2),33, "name33");

        } finally {
            dao.closeConnection();
            Environment.executeSql(DELETE);
        }
    }

    public void testDelete() throws Exception{
        PersistentObject object1    = createPersistentObject(1, "name1");
        PersistentObject object2    = createPersistentObject(2, "name2");
        PersistentObject object3    = createPersistentObject(3, "name3");

        try{
            this.dao.insert(object1);
            this.dao.insert(object2);
            this.dao.insert(object3);

            List objects = this.dao.readList(PersistentObject.class, "select * from persistent_object");
            assertEquals("wrong number of objects", 3, objects.size());

            this.dao.delete(object1);
            objects = this.dao.readList(PersistentObject.class, "select * from persistent_object order by id");
            assertEquals("wrong number of objects", 2, objects.size());
            assertObject2Correct(objects.get(0));
            assertObject3Correct(objects.get(1));

            this.dao.delete(object2);
            objects = this.dao.readList(PersistentObject.class, "select * from persistent_object");
            assertEquals("wrong number of objects", 1, objects.size());
            assertObject3Correct(objects.get(0));

            this.dao.delete(object3);
            objects = this.dao.readList(PersistentObject.class, "select * from persistent_object");
            assertEquals("wrong number of objects", 0, objects.size());

        } finally {
            dao.closeConnection();
           Environment.executeSql(DELETE);
        }
    }


    public void testDelete_ObjectMappingKey() throws Exception{
        PersistentObject object1    = createPersistentObject(1, "name1");
        PersistentObject object2    = createPersistentObject(2, "name2");
        PersistentObject object3    = createPersistentObject(3, "name3");

        try{
            this.dao.insert(object1);
            this.dao.insert(object2);
            this.dao.insert(object3);

            List objects = this.dao.readList(PersistentObject.class, "select * from persistent_object");
            assertEquals("wrong number of objects", 3, objects.size());

            this.dao.delete(PersistentObject.class, object1);
            objects = this.dao.readList(PersistentObject.class, "select * from persistent_object");
            assertEquals("wrong number of objects", 2, objects.size());
            assertObject2Correct(objects.get(0));
            assertObject3Correct(objects.get(1));

            this.dao.delete(PersistentObject.class, object2);
            objects = this.dao.readList(PersistentObject.class, "select * from persistent_object");
            assertEquals("wrong number of objects", 1, objects.size());
            assertObject3Correct(objects.get(0));

            this.dao.delete(PersistentObject.class, object3);
            objects = this.dao.readList(PersistentObject.class, "select * from persistent_object");
            assertEquals("wrong number of objects", 0, objects.size());

        } finally {
            dao.closeConnection();
           Environment.executeSql(DELETE);
        }
    }

    public void testDeleteBatch() throws Exception {
        PersistentObject object1    = createPersistentObject(1, "name1");
        PersistentObject object2    = createPersistentObject(2, "name2");
        PersistentObject object3    = createPersistentObject(3, "name3");

        try{
            this.dao.insert(object1);
            this.dao.insert(object2);
            this.dao.insert(object3);

            List objectsRead = this.dao.readList(PersistentObject.class, "select * from persistent_object");
            assertEquals("wrong number of objects", 3, objectsRead.size());

            List objectsDelete = new ArrayList();
            objectsDelete.add(object2);
            objectsDelete.add(object3);

            int[] objectsDeleted = this.dao.deleteBatch(objectsDelete);

            objectsRead = this.dao.readList(PersistentObject.class, "select * from persistent_object");
            assertEquals("wrong number of objects", 1, objectsRead.size());
            assertObject1Correct(objectsRead.get(0));

            objectsDelete.clear();
            objectsDeleted = this.dao.deleteBatch(objectsDelete);
            assertEquals(0, objectsDeleted.length);

        } finally {
            dao.closeConnection();
            Environment.executeSql(DELETE);
        }
    }


    public void testDeleteBatch_ObjectMappingKey() throws Exception {
        PersistentObject object1    = createPersistentObject(1, "name1");
        PersistentObject object2    = createPersistentObject(2, "name2");
        PersistentObject object3    = createPersistentObject(3, "name3");

        try{
            this.dao.insert(object1);
            this.dao.insert(object2);
            this.dao.insert(object3);

            List objectsRead = this.dao.readList(PersistentObject.class, "select * from persistent_object");
            assertEquals("wrong number of objects", 3, objectsRead.size());

            List objectsDelete = new ArrayList();
            objectsDelete.add(object2);
            objectsDelete.add(object3);

            this.dao.deleteBatch(PersistentObject.class, objectsDelete);

            objectsRead = this.dao.readList(PersistentObject.class, "select * from persistent_object");
            assertEquals("wrong number of objects", 1, objectsRead.size());
            assertObject1Correct(objectsRead.get(0));

        } finally {
            dao.closeConnection();
            Environment.executeSql(DELETE);
        }
    }

    public void testDeleteByPrimaryKey() throws Exception{
        PersistentObject object1    = createPersistentObject(1, "name1");
        PersistentObject object2    = createPersistentObject(2, "name2");
        PersistentObject object3    = createPersistentObject(3, "name3");

        try{
            this.dao.insert(object1);
            this.dao.insert(object2);
            this.dao.insert(object3);

            List objects = this.dao.readList(PersistentObject.class, "select * from persistent_object");
            assertEquals("wrong number of objects", 3, objects.size());

            this.dao.deleteByPrimaryKey(PersistentObject.class, new Long(1));
            objects = this.dao.readList(PersistentObject.class, "select * from persistent_object");
            assertEquals("wrong number of objects", 2, objects.size());
            assertObject2Correct(objects.get(0));
            assertObject3Correct(objects.get(1));

            this.dao.deleteByPrimaryKey(PersistentObject.class, new Long(2));
            objects = this.dao.readList(PersistentObject.class, "select * from persistent_object");
            assertEquals("wrong number of objects", 1, objects.size());
            assertObject3Correct(objects.get(0));

            this.dao.deleteByPrimaryKey(PersistentObject.class, new Long(3));
            objects = this.dao.readList(PersistentObject.class, "select * from persistent_object");
            assertEquals("wrong number of objects", 0, objects.size());
        } finally {
            dao.closeConnection();
           Environment.executeSql(DELETE);
        }
    }


    public void testDeleteBatchByPrimaryKeys_ObjectMappingKey() throws Exception {
        PersistentObject object1    = createPersistentObject(1, "name1");
        PersistentObject object2    = createPersistentObject(2, "name2");
        PersistentObject object3    = createPersistentObject(3, "name3");

        try{
            this.dao.insert(object1);
            this.dao.insert(object2);
            this.dao.insert(object3);

            List objectsRead = this.dao.readList(PersistentObject.class, "select * from persistent_object");
            assertEquals("wrong number of objects", 3, objectsRead.size());

            List objectsDelete = new ArrayList();
            objectsDelete.add(new Long(2));
            objectsDelete.add(new Long(3));

            this.dao.deleteBatchByPrimaryKeys(PersistentObject.class, objectsDelete);

            objectsRead = this.dao.readList(PersistentObject.class, "select * from persistent_object");
            assertEquals("wrong number of objects", 1, objectsRead.size());
            assertObject1Correct(objectsRead.get(0));

        } finally {
            dao.closeConnection();
            Environment.executeSql(DELETE);
        }
    }

    private PersistentObject createPersistentObject(int id, String name) {
        PersistentObject object = null;
        object = new PersistentObject();
        object.setId(id);
        object.setName(name);
        return object;
    }

    private void assertObjectCorrect(Object target, long id, String name){
        PersistentObject object = (PersistentObject) target;
        assertEquals("id wrong"        , id, object.getId());
        assertEquals("name wrong"      , name , object.getName());
    }

    private void assertObject1Correct(Object target){
        assertObjectCorrect(target, 1, "name1");
    }

    private void assertObject2Correct(Object target){
        assertObjectCorrect(target, 2, "name2");
    }

    private void assertObject3Correct(Object target){
        assertObjectCorrect(target, 3, "name3");
    }

}
