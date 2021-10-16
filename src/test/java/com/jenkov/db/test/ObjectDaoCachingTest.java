package com.jenkov.db.test;

import com.jenkov.db.PersistenceManager;
import com.jenkov.db.test.objects.PrimaryObject;
import com.jenkov.db.test.objects.RelatedObject;
import com.jenkov.db.impl.ResultSetProcessorBase;
import com.jenkov.db.itf.IDaos;
import com.jenkov.db.itf.PersistenceException;
import com.jenkov.db.itf.IResultSetProcessor;
import com.jenkov.testing.mock.impl.MockFactory;
import com.jenkov.testing.mock.impl.MethodInvocation;
import com.jenkov.testing.mock.itf.IMock;

import java.sql.ResultSet;
import java.sql.SQLException;

/**

 */
public class ObjectDaoCachingTest {

    /*
    @Test
    public void testRead() throws Exception {
        PersistenceManager manager = new PersistenceManager(Environment.getDataSource());
        IDaos daos    = manager.createDaos();

        try{
            Environment.executeSql("insert into PrimaryObjects(id, text) values(0, 'p0') ");
            Environment.executeSql("insert into RelatedObjects(id, a_value, primary_id) values(2, 'r0', 0)");

            daos.getJdbcDao().read("select * from PRIMARYOBJECTS, RELATEDOBJECTS where primary_id = PRIMARYOBJECTS.ID",
                new ResultSetProcessorBase(){
                ResultSetView resultSetView  = null;
                ResultSetView resultSetView2 = null;
                ResultSet     resultProxy    = null;

                    public void init(ResultSet result, IDaos daos) throws SQLException, PersistenceException {
                        this.resultProxy    = (ResultSet) MockFactory.createProxy(result);
                        this.resultSetView  = daos.getResultSetView(PrimaryObject.class, resultProxy);
                        this.resultSetView.setAlias("ID", "PRIMARYOBJECTS.ID");
                        this.resultSetView2 = daos.getResultSetView(RelatedObject.class, resultProxy);
                        this.resultSetView2.setAlias("ID", "RELATEDOBJECTS.ID");
                    }

                    public void process(ResultSet result, IDaos daos) throws SQLException, PersistenceException {
                        PrimaryObject primary = (PrimaryObject) daos.getObjectDao().read(PrimaryObject.class, resultSetView);
                        assertEquals(0, primary.getId());
                        assertEquals("p0", primary.getText());

                        RelatedObject related = (RelatedObject) daos.getObjectDao().read(RelatedObject.class, resultSetView2);
                        assertEquals(2, related.getId());
                        assertEquals("r0", related.getAValue());
                        assertEquals(0, related.getPrimaryId());
                    }
                });
        } finally {
            Environment.executeSql("delete from PrimaryObjects");
            Environment.executeSql("delete from RelatedObjects");
        }
    }
    */
}
