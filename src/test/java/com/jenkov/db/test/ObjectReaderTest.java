package com.jenkov.db.test;

import com.jenkov.db.impl.ObjectReader;
import com.jenkov.db.impl.SqlGenerator;
import com.jenkov.db.impl.mapping.ObjectMapper;
import com.jenkov.db.impl.mapping.ObjectMappingFactory;
import com.jenkov.db.itf.IObjectReader;
import com.jenkov.db.itf.ISqlGenerator;
import com.jenkov.db.itf.PersistenceException;
import com.jenkov.db.itf.Database;
import com.jenkov.db.itf.mapping.IObjectMapper;
import com.jenkov.db.itf.mapping.IObjectMapping;
import com.jenkov.db.test.objects.PersistentObject;
import com.jenkov.db.util.JdbcUtil;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Jakob Jenkov,  Jenkov Development
 */
public class ObjectReaderTest {

    protected String sqlRead1   = "select * from persistent_object where id=1";
    protected String sqlWrite1  = "insert into persistent_object(id, name, fieldValue, objectValue) " +
                                  " values (1, 'name', 'value', 'objectValue')";
    protected String sqlDelete1 = "delete from persistent_object where id=1";

    protected String sqlRead2   = "select * from persistent_object where id=2";
    protected String sqlWrite2  = "insert into persistent_object(id, name, fieldValue, objectValue) " +
                                  " values (2, 'name2', 'value2', 'objectValue2')";
    protected String sqlDelete2 = "delete from persistent_object where id=2";

    protected String sqlRead3   = "select * from persistent_object where id=3";
    protected String sqlWrite3  = "insert into persistent_object(id, name, fieldValue, objectValue) " +
                                  " values (3, 'name3', 'value3', 'objectValue3')";
    protected String sqlDelete3 = "delete from persistent_object where id=3";

    protected String sqlRead4   = "select * from persistent_object where id=4";
    protected String sqlWrite4  = "insert into persistent_object(id, name, fieldValue, objectValue) " +
                                  " values (4, 'name4', 'value4', 'objectValue4')";
    protected String sqlDelete4 = "delete from persistent_object where id=4";

    protected String sqlRead5   = "select * from persistent_object where id=5";
    protected String sqlWrite5  = "insert into persistent_object(id, name, fieldValue, objectValue) " +
                                  " values (5, 'name5', 'value5', 'objectValue5')";
    protected String sqlDelete5 = "delete from persistent_object where id=5";

    protected String sqlDelete  = "delete from persistent_object";

    protected Connection     connection = null;
    protected IObjectReader  reader     = null;
    protected IObjectMapper  mapper     = null;
    protected IObjectMapping mapping    = null;
    protected ISqlGenerator  generator  = null;

    //public ObjectReaderTest(String testName){
    //    super(testName);
    //}

    @BeforeEach
    public void setUp() throws Exception{
        this.connection = Environment.getConnection();
        this.reader = new ObjectReader();
        this.reader.setDatabase(Database.determineDatabase(connection));
        this.mapper = new ObjectMapper(new ObjectMappingFactory());
        this.mapping = this.mapper.mapToTable(PersistentObject.class, null, this.connection, null, null);
        this.generator = new SqlGenerator();
    }

    @AfterEach
    public void tearDown() throws Exception{
        this.mapper = null;
        this.mapping = null;

        JdbcUtil.close(this.connection);
    }


    // ======================================
    // Testing Read Single Object Functions
    // ======================================

    @Test
    public void testReadByPrimaryKey() throws Exception{
        PersistentObject object = null;

        try{
            Environment.executeSql(sqlDelete);
            this.connection.createStatement().execute(sqlWrite1);
            this.connection.createStatement().execute(sqlWrite3);

            object = (PersistentObject) this.reader.readByPrimaryKey(this.mapping, new Long(1),
                    this.generator.generateReadByPrimaryKeyStatement(this.mapping), this.connection );
            assertNotNull(object, "object1 should not be null");
            assertObject1ReadCorrectly(object);

            object = (PersistentObject) this.reader.readByPrimaryKey(this.mapping, new Long(3),
                    this.generator.generateReadByPrimaryKeyStatement(this.mapping), this.connection );
            assertNotNull(object, "object3 should not be null");
            assertObject3ReadCorrectly(object);

            object = (PersistentObject) this.reader.readByPrimaryKey(this.mapping, new Long(4),
                    this.generator.generateReadByPrimaryKeyStatement(this.mapping), this.connection );
            assertNull(object, "object should be null");

            try{
                object = (PersistentObject) this.reader.readByPrimaryKey(this.mapping, null,
                        this.generator.generateReadByPrimaryKeyStatement(this.mapping), this.connection );
            } catch(PersistenceException e){
                //ignore, exception expected when primary key is null.
            }

        } finally {
            this.connection.createStatement().execute(sqlDelete1);
            this.connection.createStatement().execute(sqlDelete3);

        }
    }

    @Test
    public void testReadFromResultSet() throws Exception{
        try{
            this.connection.createStatement().execute(sqlWrite1);

            ResultSet result = this.connection.createStatement().executeQuery(sqlRead1);

            result.next();
            PersistentObject object = (PersistentObject) this.reader.read(this.mapping, result);
            assertObject1ReadCorrectly(object);

        } finally {
            this.connection.createStatement().execute(sqlDelete1);
        }
    }

    @Test
    public void testReadFromStatement() throws Exception{
        Statement statement = null;

        try{
            Connection connection = this.connection;
            executeSql(sqlWrite1, sqlWrite2);

            statement = connection.createStatement();

            PersistentObject object = (PersistentObject) this.reader.read(this.mapping, statement, sqlRead1);
            assertObject1ReadCorrectly(object);

            assertNull(this.reader.read(this.mapping, statement, "select * from persistent_object where id=9"),
                    "should get null from empty result set");

            try{
                this.reader.read(this.mapping, statement, "selcet from persistent_object");
                fail("should throw PersistenceException on bad sql");
            } catch(PersistenceException e){
            }

            this.reader.read(this.mapping, statement, "select * from persistent_object");

        } finally {
            statement.close();
            executeSql(sqlDelete1, sqlDelete2);
        }
    }

    @Test
    public void testReadFromSqlString() throws Exception{
        try{
            Environment.executeSql(sqlDelete);
            executeSql(sqlWrite1, sqlWrite2);

            PersistentObject object = (PersistentObject) this.reader.read(this.mapping,  sqlRead1, this.connection);
            assertObject1ReadCorrectly(object);

            assertNull(this.reader.read(this.mapping, "select * from persistent_object where id=-1", this.connection),
                    "should get null from empty result set");

            try{
                this.reader.read(this.mapping, "selcet from persistent_object", this.connection);
                fail("should throw PersistenceException on bad sql");
            } catch(PersistenceException e){
            }

            this.reader.read(this.mapping, "select * from persistent_object", this.connection);

        } finally {
            executeSql(sqlDelete1, sqlDelete2);
        }
    }

    @Test
    public void testReadFromPreparedStatement() throws Exception{
        PreparedStatement preparedStatement = null;

        try{
            Connection connection = this.connection;
            executeSql(sqlWrite1, sqlWrite2);

            preparedStatement = connection.prepareStatement(sqlRead1);

            PersistentObject object = (PersistentObject) this.reader.read(this.mapping, preparedStatement);
            assertObject1ReadCorrectly(object);

             preparedStatement = connection.prepareStatement("select * from persistent_object where id=-1");
             assertNull(this.reader.read(this.mapping, preparedStatement ),
                     "should get null from empty result set");


            try{
                preparedStatement = connection.prepareStatement("select * from persistent_objectos");
                this.reader.read(this.mapping, preparedStatement);
                fail("should throw PersistenceException on bad sql");
            } catch(PersistenceException e){
            } catch(SQLException e){
                //will be thrown in case the SQL is sent to the database for precompilation.
            }

            preparedStatement = connection.prepareStatement("select * from persistent_object");
            this.reader.read(this.mapping, preparedStatement);
        } finally {
            preparedStatement.close();
            executeSql(sqlDelete1, sqlDelete2);
        }
    }

    @Test
    public void testRead_Collection() throws Exception {
        String sql1 = "select * from persistent_object where id=?";
        String sql2 = "select * from persistent_object where name like ? and fieldValue like ?";

        try{
            Environment.executeSql(sqlWrite1, this.connection);
            Environment.executeSql(sqlWrite2, this.connection);
            Environment.executeSql(sqlWrite3, this.connection);

            List parameters = new ArrayList();
            parameters.add(new Long(1));

            PersistentObject object = (PersistentObject)
                    this.reader.read(this.mapping, sql1, parameters, this.connection);
            assertObject1ReadCorrectly(object);

            parameters.clear();

            parameters.add("name3");
            parameters.add("value3");

            object = (PersistentObject)
                    this.reader.read(this.mapping, sql2, parameters, this.connection);
            assertObject3ReadCorrectly(object);

            parameters.clear();
            parameters.add("no_name");
            parameters.add("no_value");

            object = (PersistentObject)
                    this.reader.read(this.mapping, sql2, parameters, this.connection);
            assertNull(object);

        } finally{
            Environment.executeSql(sqlDelete);
        }
    }

    @Test
    public void testRead_Collection_Exceptions() throws Exception{
        String sql1 = "select * from persistent_object where id=?";
        String sql2 = "select * from persistent_object where name like ? and fieldValue like ?";

        try{
            Environment.executeSql(sqlWrite1);
            Environment.executeSql(sqlWrite2);
            Environment.executeSql(sqlWrite3);

            List parameters = new ArrayList();


            PersistentObject object;
            try {
                object = (PersistentObject)
                                    this.reader.read(this.mapping, sql1, parameters, this.connection);
                fail("should throw exception when no parameters available");
            } catch (PersistenceException e) {
                //ignore, exception expected
            }

            try {
                object = (PersistentObject)
                        this.reader.read(this.mapping, null, parameters, this.connection);
                fail("should throw exception when sql is null");
            } catch (Throwable e) {
                //ignore, exception expected
            }

            try {
                object = (PersistentObject)
                                    this.reader.read(this.mapping, sql1, (Collection) null, this.connection);
                fail("should throw exception when parameter collection reference is null");
            } catch (NullPointerException e) {
                //ignore, exception expected
            }

            try {
                parameters.add(new Long(1));
                object = (PersistentObject)
                                    this.reader.read(null, sql1, parameters, this.connection);
                fail("should throw exception when object mapping is null");
            } catch (NullPointerException e) {
                //ignore, exception expected
            }

            try {
                object = (PersistentObject)
                                    this.reader.read(this.mapping, sql1, parameters, null);
                fail("should throw exception when connection is null");
            } catch (NullPointerException e) {
                //ignore, exception expected
            }
        } finally{
            Environment.executeSql(sqlDelete);
        }
    }

    @Test
    public void testRead_Array() throws Exception {
        String sql1 = "select * from persistent_object where id=?";
        String sql2 = "select * from persistent_object where name like ? and fieldValue like ?";

        try{
            Environment.executeSql(sqlWrite1);
            Environment.executeSql(sqlWrite2);
            Environment.executeSql(sqlWrite3);

            Object[] parameters = new Object[1];

            parameters[0] = new Long(1);

            PersistentObject object = (PersistentObject)
                    this.reader.read(this.mapping, sql1, parameters, this.connection);
            assertObject1ReadCorrectly(object);

            parameters = new Object[2];

            parameters[0] = "name3";
            parameters[1] = "value3";

            object = (PersistentObject)
                    this.reader.read(this.mapping, sql2, parameters, this.connection);
            assertObject3ReadCorrectly(object);

            parameters[0] = "no_name";
            parameters[1] = "no_value";

            object = (PersistentObject)
                    this.reader.read(this.mapping, sql2, parameters, this.connection);
            assertNull(object);
        } finally{
            Environment.executeSql(sqlDelete);
        }
    }



    // ======================================
    // Testing Read List Functions
    // ======================================
    @Test
    public void testReadListByPrimaryKeys() throws Exception {
        List objects = null;
        try{
            this.connection.createStatement().execute(sqlWrite1);
            this.connection.createStatement().execute(sqlWrite3);

            List primaryKeys = new ArrayList();
            primaryKeys.add(new Long(1));
            primaryKeys.add(new Long(3));
            objects = this.reader.readListByPrimaryKeys(this.mapping, primaryKeys ,
                    "select * from persistent_object where id in (?,?)", this.connection );

            assertNotNull(objects, "objects should not be null");
            assertEquals(2, objects.size(), "should read two objects");

            assertNotNull(objects.get(0), "object1 should not be null");
            assertObject1ReadCorrectly((PersistentObject) objects.get(0));

            assertNotNull(objects.get(1), "object3 should not be null");
            assertObject3ReadCorrectly((PersistentObject) objects.get(1));

            objects = this.reader.readListByPrimaryKeys(this.mapping, primaryKeys ,
                    "select * from persistent_object where id in (?,?)", this.connection );


            primaryKeys.clear();
            primaryKeys.add(new Long(4));
            primaryKeys.add(new Long(5));
            
            objects = this.reader.readListByPrimaryKeys(this.mapping, primaryKeys ,
                    "select * from persistent_object where id in (?,?)", this.connection );
            assertNotNull(objects);
            assertEquals(0, objects.size(), "list should be empty");

            primaryKeys.clear();
            objects = this.reader.readListByPrimaryKeys(this.mapping, primaryKeys ,
                    "select * from persistent_object where id in (?,?)", this.connection );
            assertNotNull(objects);
            assertEquals(0, objects.size(), "list should be empty");

        } finally {
            this.connection.createStatement().execute(sqlDelete1);
            this.connection.createStatement().execute(sqlDelete3);

        }
    }

    @Test
    public void testReadListFromResultSet() throws Exception {
        try{
            executeSql(sqlWrite1, sqlWrite2, sqlWrite3);

            ResultSet result = this.connection.createStatement().executeQuery("select * from persistent_object order by id");
            List objects = this.reader.readList(this.mapping, result);
            assertEquals(3, objects.size(), "should be 3 objects in list");

            assertObject1ReadCorrectly((PersistentObject) objects.get(0));

            result  = this.connection.createStatement().executeQuery("select * from persistent_object order by id");
            result.next();
            objects = this.reader.readList(this.mapping, result);
            assertEquals(3, objects.size(), "should be 3 objects in list");

        } finally {
            executeSql(sqlDelete1, sqlDelete2, sqlDelete3);
        }
    }

    @Test
    public void testReadListFromStatement() throws Exception {
        try{
            executeSql(sqlWrite1, sqlWrite2, sqlWrite3);

            Statement statement = this.connection.createStatement();
            List objects = this.reader.readList(this.mapping, statement,
                "select * from persistent_object order by id");
            assertEquals(3, objects.size(), "should be 3 objects in list");

            assertObject1ReadCorrectly((PersistentObject) objects.get(0));
         } finally {
            executeSql(sqlDelete1, sqlDelete2, sqlDelete3);
        }
    }


    @Test
    public void testReadListFromSqlString() throws Exception {
        try{
            executeSql(sqlWrite1, sqlWrite2, sqlWrite3);

            List objects = this.reader.readList(this.mapping,
                    "select * from persistent_object order by id", this.connection);
            assertEquals(3, objects.size(), "should be 3 objects in list");

            assertObject1ReadCorrectly((PersistentObject) objects.get(0));
         } finally {
            executeSql(sqlDelete1, sqlDelete2, sqlDelete3);
        }
    }

    @Test
    public void testReadListFromPreparedStatement() throws Exception {
        try{
            executeSql(sqlWrite1, sqlWrite2, sqlWrite3);

            PreparedStatement preparedStatement = this.connection
                        .prepareStatement("select * from persistent_object order by id");
            List objects = this.reader.readList(this.mapping, preparedStatement);
            assertEquals(3, objects.size(), "should be 3 objects in list");

            assertObject1ReadCorrectly((PersistentObject) objects.get(0));
         } finally {
            executeSql(sqlDelete1, sqlDelete2, sqlDelete3);
        }
    }

    @Test
    public void testReadList_Collection() throws Exception {
        String sql1 = "select * from persistent_object where id > ? order by id";
        String sql2 = "select * from persistent_object where name like ? and fieldValue like ? order by id";

        try{
            Environment.executeSql(sqlWrite1);
            Environment.executeSql(sqlWrite2);
            Environment.executeSql(sqlWrite3);
            Environment.executeSql(sqlWrite4);
            Environment.executeSql(sqlWrite5);

            List parameters = new ArrayList();
            parameters.add(new Long(1));

            List objects = this.reader.readList(this.mapping, sql1, parameters, this.connection);
            assertEquals(4, objects.size(), "too many objects read");
            assertObject3ReadCorrectly((PersistentObject) objects.get(1));

            parameters.clear();
            parameters.add("name%");
            parameters.add("value%");
            objects = this.reader.readList(this.mapping, sql2, parameters, this.connection);
            assertEquals(5, objects.size(), "wrong number of objects");
            assertObject1ReadCorrectly((PersistentObject) objects.get(0));
            assertObjectCorrect((PersistentObject) objects.get(1), 2, "name2");
            assertObject3ReadCorrectly((PersistentObject) objects.get(2));
            assertObjectCorrect((PersistentObject) objects.get(3), 4, "name4");
            assertObjectCorrect((PersistentObject) objects.get(4), 5, "name5");

            parameters.clear();
            parameters.add("no_name");
            parameters.add("no_value");
            objects = this.reader.readList(this.mapping, sql2, parameters, this.connection);
            assertEquals(0, objects.size(), "no objects should be read");
        } finally {
            Environment.executeSql(sqlDelete);
        }
    }

    @Test
    public void testReadList_Array() throws Exception {
        String sql1 = "select * from persistent_object where id > ? order by id";
        String sql2 = "select * from persistent_object where name like ? and fieldValue like ? order by id";

        try{
            Environment.executeSql(sqlWrite1);
            Environment.executeSql(sqlWrite2);
            Environment.executeSql(sqlWrite3);
            Environment.executeSql(sqlWrite4);
            Environment.executeSql(sqlWrite5);

            Object[] parameters = new Object[1];
            parameters[0] = new Long(1);

            List objects = this.reader.readList(this.mapping, sql1, parameters, this.connection);
            assertEquals(4, objects.size(), "too many objects read");
            assertObject3ReadCorrectly((PersistentObject) objects.get(1));

            parameters = new Object[2];
            parameters[0] = "name%";
            parameters[1] = "value%";
            objects = this.reader.readList(this.mapping, sql2, parameters, this.connection);
            assertEquals(5, objects.size(), "wrong number of objects");
            assertObject1ReadCorrectly((PersistentObject) objects.get(0));
            assertObjectCorrect((PersistentObject) objects.get(1), 2, "name2");
            assertObject3ReadCorrectly((PersistentObject) objects.get(2));
            assertObjectCorrect((PersistentObject) objects.get(3), 4, "name4");
            assertObjectCorrect((PersistentObject) objects.get(4), 5, "name5");

            parameters = new Object[2];
            parameters[0] = "no_name";
            parameters[1] = "no_value";
            objects = this.reader.readList(this.mapping, sql2, parameters, this.connection);
            assertEquals(0, objects.size(), "no objects should be read");
        } finally {
            Environment.executeSql(sqlDelete);
        }
    }


    // ======================================
    // Testing Filtered Read List Functions
    // ======================================
    @Test
    public void testReadListFromResultSetUsingFilter() throws Exception {
        try{
            executeSql(sqlWrite1, sqlWrite2, sqlWrite3, sqlWrite4, sqlWrite5);

            ResultSet result = this.connection.createStatement().executeQuery("select * from persistent_object order by id");
            List objects = this.reader.readList(this.mapping, result, new AcceptEveryOtherFilter(3));
            assertEquals(3, objects.size(), "should be 3 objects in list");
            assertObject1ReadCorrectly((PersistentObject) objects.get(0));
            assertObject3ReadCorrectly((PersistentObject) objects.get(1));
            result.close();

            result  = this.connection.createStatement().executeQuery("select * from persistent_object order by id");
            objects = this.reader.readList(this.mapping, result, new AcceptEveryOtherFilter(2));
            assertEquals(2, objects.size(), "should be 2 objects in list");
            assertObject1ReadCorrectly((PersistentObject) objects.get(0));
            assertObject3ReadCorrectly((PersistentObject) objects.get(1));
            result.close();

            result  = this.connection.createStatement().executeQuery("select * from persistent_object order by id");
            objects = this.reader.readList(this.mapping, result, new AcceptEveryOtherFilter(1));
            assertEquals(1, objects.size(), "should be 1 objects in list");
            assertObject1ReadCorrectly((PersistentObject) objects.get(0));
            result.close();

        } finally {
            executeSql(sqlDelete1, sqlDelete2, sqlDelete3, sqlDelete4, sqlDelete5);
        }
    }

    @Test
    public void testReadListByPrimaryKeysUsingFilter() throws Exception {
        try{
            executeSql(sqlWrite1, sqlWrite2, sqlWrite3, sqlWrite4, sqlWrite5);

            List primaryKeys = new ArrayList();
            primaryKeys.add(new Long(1));
            primaryKeys.add(new Long(2));
            primaryKeys.add(new Long(3));
            primaryKeys.add(new Long(4));
            primaryKeys.add(new Long(5));

            Comparator comparator = new Comparator(){
                public int compare(Object o1, Object o2) {
                    PersistentObject obj1 = (PersistentObject) o1;
                    PersistentObject obj2 = (PersistentObject) o2;

                    long comparison = obj1.getId() - obj2.getId();
                    return (int) comparison ;
                }
            };

            String sql = this.generator.generateReadListByPrimaryKeysStatement(this.mapping, primaryKeys.size());
            sql = sql + " order by id"; //necessary to be able to know for sure what records are returned.
            List objects = this.reader.readListByPrimaryKeys(this.mapping, primaryKeys, sql, this.connection, new AcceptEveryOtherFilter(3));
            Collections.sort(objects, comparator);
            assertEquals(3, objects.size(), "should be 3 objects in list");
            assertObject1ReadCorrectly((PersistentObject) objects.get(0));
            assertObject3ReadCorrectly((PersistentObject) objects.get(1));

            objects = this.reader.readListByPrimaryKeys(this.mapping, primaryKeys, sql, this.connection, new AcceptEveryOtherFilter(2));
            Collections.sort(objects, comparator);
            assertEquals(2, objects.size(), "should be 2 objects in list");
            assertObject1ReadCorrectly((PersistentObject) objects.get(0));
            assertObject3ReadCorrectly((PersistentObject) objects.get(1));

            objects = this.reader.readListByPrimaryKeys(this.mapping, primaryKeys, sql, this.connection, new AcceptEveryOtherFilter(1));
            Collections.sort(objects, comparator);
            assertEquals(1, objects.size(), "should be 1 objects in list");
            assertObject1ReadCorrectly((PersistentObject) objects.get(0));

            primaryKeys.clear();
            objects = this.reader.readListByPrimaryKeys(this.mapping, primaryKeys, sql, this.connection, new AcceptEveryOtherFilter(1));
            assertNotNull(objects);
            assertEquals(0, objects.size(), "list should be empty");

        } finally {
            executeSql(sqlDelete1, sqlDelete2, sqlDelete3, sqlDelete4, sqlDelete5);
        }
    }

    @Test
    public void testReadListFromStatementUsingFilter() throws Exception {
        try{
            executeSql(sqlWrite1, sqlWrite2, sqlWrite3, sqlWrite4, sqlWrite5);

            String sql = "select * from persistent_object order by id";
            Statement statement = this.connection.createStatement();

            List objects = this.reader.readList(this.mapping, statement, sql , new AcceptEveryOtherFilter(3));
            assertEquals(3, objects.size(), "should be 3 objects in list");
            assertObject1ReadCorrectly((PersistentObject) objects.get(0));
            assertObject3ReadCorrectly((PersistentObject) objects.get(1));

            objects = this.reader.readList(this.mapping, statement, sql, new AcceptEveryOtherFilter(2));
            assertEquals(2, objects.size(), "should be 2 objects in list");
            assertObject1ReadCorrectly((PersistentObject) objects.get(0));
            assertObject3ReadCorrectly((PersistentObject) objects.get(1));

            objects = this.reader.readList(this.mapping, statement, sql , new AcceptEveryOtherFilter(1));
            assertEquals(1, objects.size(), "should be 1 objects in list");
            assertObject1ReadCorrectly((PersistentObject) objects.get(0));
            statement.close();
        } finally {
            executeSql(sqlDelete1, sqlDelete2, sqlDelete3, sqlDelete4, sqlDelete5);
        }
    }

    @Test
    public void testReadListFromSqlStringUsingFilter() throws Exception {
        try{
            executeSql(sqlWrite1, sqlWrite2, sqlWrite3, sqlWrite4, sqlWrite5);

            String sql = "select * from persistent_object order by id";

            List objects = this.reader.readList(this.mapping,  sql, this.connection, new AcceptEveryOtherFilter(3));
            assertEquals(3, objects.size(), "should be 3 objects in list");
            assertObject1ReadCorrectly((PersistentObject) objects.get(0));
            assertObject3ReadCorrectly((PersistentObject) objects.get(1));

            objects = this.reader.readList(this.mapping, sql, this.connection, new AcceptEveryOtherFilter(2));
            assertEquals(2, objects.size(), "should be 2 objects in list");
            assertObject1ReadCorrectly((PersistentObject) objects.get(0));
            assertObject3ReadCorrectly((PersistentObject) objects.get(1));

            objects = this.reader.readList(this.mapping, sql, this.connection, new AcceptEveryOtherFilter(1));
            assertEquals(1, objects.size(), "should be 1 objects in list");
            assertObject1ReadCorrectly((PersistentObject) objects.get(0));
        } finally {
            executeSql(sqlDelete1, sqlDelete2, sqlDelete3, sqlDelete4, sqlDelete5);
        }
    }

    @Test
    public void testReadListFromPreparedStatementUsingFilter() throws Exception {
        try{
            executeSql(sqlWrite1, sqlWrite2, sqlWrite3, sqlWrite4, sqlWrite5);

            String sql = "select * from persistent_object where id > ? order by id";
            PreparedStatement statement = this.connection.prepareStatement(sql);
            statement.setLong(1, 0);

            List objects = this.reader.readList(this.mapping, statement, new AcceptEveryOtherFilter(3));
            assertEquals(3, objects.size(), "should be 3 objects in list");
            assertObject1ReadCorrectly((PersistentObject) objects.get(0));
            assertObject3ReadCorrectly((PersistentObject) objects.get(1));

            objects = this.reader.readList(this.mapping, statement, new AcceptEveryOtherFilter(2));
            assertEquals(2, objects.size(), "should be 2 objects in list");
            assertObject1ReadCorrectly((PersistentObject) objects.get(0));
            assertObject3ReadCorrectly((PersistentObject) objects.get(1));

            objects = this.reader.readList(this.mapping, statement, new AcceptEveryOtherFilter(1));
            assertEquals(1, objects.size(), "should be 1 objects in list");
            assertObject1ReadCorrectly((PersistentObject) objects.get(0));
            statement.close();
        } finally {
            executeSql(sqlDelete1, sqlDelete2, sqlDelete3, sqlDelete4, sqlDelete5);
        }
    }

    @Test
    public void testReadList_Collection_Filtered() throws Exception {
        String sql1 = "select * from persistent_object where id > ? order by id";
        String sql2 = "select * from persistent_object where name like ? and fieldValue like ? order by id";

        try{
            Environment.executeSql(sqlWrite1);
            Environment.executeSql(sqlWrite2);
            Environment.executeSql(sqlWrite3);
            Environment.executeSql(sqlWrite4);
            Environment.executeSql(sqlWrite5);

            List parameters = new ArrayList();
            parameters.add(new Long(1));

            List objects = this.reader.readList(this.mapping, sql1, parameters,
                    this.connection, new AcceptEveryOtherFilter(3));
            assertEquals(2, objects.size(), "too many objects read");
            assertObjectCorrect((PersistentObject) objects.get(0), 2, "name2");

            parameters.clear();
            parameters.add("name%");
            parameters.add("value%");
            objects = this.reader.readList(this.mapping, sql2, parameters,
                    this.connection, new AcceptEveryOtherFilter(3));
            assertEquals(3, objects.size(), "wrong number of objects");
            assertObject1ReadCorrectly((PersistentObject) objects.get(0));
            assertObject3ReadCorrectly((PersistentObject) objects.get(1));
            assertObjectCorrect((PersistentObject) objects.get(2), 5, "name5");

            parameters.clear();
            parameters.add("no_name");
            parameters.add("no_value");
            objects = this.reader.readList(this.mapping, sql2, parameters, this.connection);
            assertEquals(0, objects.size(), "no objects should be read");
        } finally {
            Environment.executeSql(sqlDelete);
        }
    }

    @Test
    public void testReadList_Array_Filtered() throws Exception {
        String sql1 = "select * from persistent_object where id > ? ";
        String sql2 = "select * from persistent_object where name like ? and fieldValue like ? order by id";

        try{
            Environment.executeSql(sqlWrite1);
            Environment.executeSql(sqlWrite2);
            Environment.executeSql(sqlWrite3);
            Environment.executeSql(sqlWrite4);
            Environment.executeSql(sqlWrite5);

            Object[] parameters = new Object[1];
            parameters[0] = new Long(1);

            List objects = this.reader.readList(this.mapping, sql1, parameters,
                    this.connection, new AcceptEveryOtherFilter(3));
            assertEquals(2, objects.size(), "too many objects read");
            assertObjectCorrect((PersistentObject) objects.get(0), 2, "name2");

            parameters = new Object[2];
            parameters[0] = "name%";
            parameters[1] = "value%";
            objects = this.reader.readList(this.mapping, sql2, parameters,
                    this.connection, new AcceptEveryOtherFilter(3));
            assertEquals(3, objects.size(), "wrong number of objects");
            assertObject1ReadCorrectly((PersistentObject) objects.get(0));
            assertObject3ReadCorrectly((PersistentObject) objects.get(1));
            assertObjectCorrect((PersistentObject) objects.get(2), 5, "name5");

            parameters[0] = "no_name";
            parameters[1] = "no_value";
            objects = this.reader.readList(this.mapping, sql2, parameters, this.connection);
            assertEquals(0, objects.size(), "no objects should be read");
        } finally {
            Environment.executeSql(sqlDelete);
        }
    }


    

    protected void assertObjectCorrect(PersistentObject object, long id, String name) throws Exception{
        assertEquals(id     , object.getId(), "id    retrieved wrong");
        assertEquals(name, object.getName(), "name  retrieved wrong");
        assertEquals(null, object.getObjectValue(), "value retrieved wrong");
        assertEquals(null, object.getObject(), "object should not be retrieved");
    }

    protected void assertObject1ReadCorrectly(PersistentObject object) throws Exception{
        assertObjectCorrect(object, 1, "name");
        assertTrue(object.wasResultSetConstructorCalled(), "ResultSet constructor not called");
    }

    protected void assertObject3ReadCorrectly(PersistentObject object) throws Exception{
        assertObjectCorrect(object, 3, "name3");

        assertTrue(object.wasResultSetConstructorCalled(), "ResultSet constructor not called");
    }



    protected void executeSql(String sql1, String sql2) throws Exception{
        this.connection.createStatement().execute(sql1);
        this.connection.createStatement().execute(sql2);
    }

    protected void executeSql(String sql1, String sql2, String sql3) throws Exception{
        executeSql(sql1, sql2);
        this.connection.createStatement().execute(sql3);
    }

    protected void executeSql(String sql1, String sql2, String sql3, String sql4, String sql5) throws Exception{
        executeSql(sql1, sql2, sql3);
        this.connection.createStatement().execute(sql4);
        this.connection.createStatement().execute(sql5);
    }


}
