package com.jenkov.db.test;

import com.jenkov.db.PersistenceManager;
import com.jenkov.db.test.objects.PrimaryObject;
import com.jenkov.db.test.objects.RelatedObject;
import com.jenkov.db.impl.ResultSetProcessorBase;
import com.jenkov.db.itf.IDaos;
import com.jenkov.db.itf.PersistenceException;
import com.jenkov.testing.mock.impl.MockFactory;
import com.jenkov.testing.mock.impl.MethodInvocation;
import com.jenkov.testing.mock.itf.IMock;

import java.sql.ResultSet;
import java.sql.SQLException;

/**

 */
public class ResultSetViewTest {

    /*
    public void test() throws Exception {
        PersistenceManager manager = new PersistenceManager(Environment.getDataSource());
        IDaos daos    = manager.createDaos();

        try{
            Environment.executeSql("insert into PrimaryObjects(id, text) values(0, 'p0') ");
            Environment.executeSql("insert into RelatedObjects(id, a_value, primary_id) values(2, 'r0', 0)");

            daos.getJdbcDao().read("select * from PRIMARYOBJECTS, RELATEDOBJECTS where primary_id = PRIMARYOBJECTS.ID",
                new ResultSetProcessorBase(){
                    ResultSetView resultSetView  = null;
                    ResultSetView resultSetView2 = null;
                    ResultSet resultProxy    = null;

                    public void init(ResultSet result, IDaos daos) throws SQLException, PersistenceException {
                        this.resultProxy    = (ResultSet) MockFactory.createProxy(result);
                        this.resultSetView  = daos.getResultSetView(PrimaryObject.class, resultProxy);
                        this.resultSetView2 = daos.getResultSetView(RelatedObject.class, resultProxy);
                    }

                    public void process(ResultSet result, IDaos daos) throws SQLException, PersistenceException {
                        assertEquals(2, resultSetView.getMetaData().getColumnCount());
                        assertTrue("ID".equals(resultSetView.getMetaData().getColumnName(1)) ||
                                   "ID".equals(resultSetView.getMetaData().getColumnName(2))
                        );
                        assertTrue("TEXT".equals(resultSetView.getMetaData().getColumnName(1)) ||
                                   "TEXT".equals(resultSetView.getMetaData().getColumnName(2))
                        );

                        IMock resultMock = MockFactory.getMock(resultProxy);
                        resultSetView.setAlias("ID", "PRIMARYOBJECTS.ID");
                        assertEquals(0, resultSetView.getLong("ID"));
                        resultMock.assertInvoked(new MethodInvocation("getLong", new Class[]{String.class}, new Object[]{"PRIMARYOBJECTS.ID"}));



                        assertEquals(3, resultSetView2.getMetaData().getColumnCount());
                        assertTrue("ID".equals(resultSetView2.getMetaData().getColumnName(1)) ||
                                   "ID".equals(resultSetView2.getMetaData().getColumnName(2)) ||
                                   "ID".equals(resultSetView2.getMetaData().getColumnName(3))
                        );
                        assertTrue("A_VALUE".equals(resultSetView2.getMetaData().getColumnName(1)) ||
                                   "A_VALUE".equals(resultSetView2.getMetaData().getColumnName(2)) ||
                                   "A_VALUE".equals(resultSetView2.getMetaData().getColumnName(3))
                        );
                        assertTrue("PRIMARY_ID".equals(resultSetView2.getMetaData().getColumnName(1)) ||
                                   "PRIMARY_ID".equals(resultSetView2.getMetaData().getColumnName(2)) ||
                                   "PRIMARY_ID".equals(resultSetView2.getMetaData().getColumnName(3))
                        );

                        resultMock.clear();
                        resultSetView2.setAlias("ID", "RELATEDOBJECTS.ID");
                        assertEquals(2, resultSetView2.getLong("ID"));
                        resultMock.assertInvoked(new MethodInvocation("getLong", new Class[]{String.class}, new Object[]{"RELATEDOBJECTS.ID"}));

                    }
                });
        } finally {
            Environment.executeSql("delete from PrimaryObjects");
            Environment.executeSql("delete from RelatedObjects");
        }

    }
    */
}
