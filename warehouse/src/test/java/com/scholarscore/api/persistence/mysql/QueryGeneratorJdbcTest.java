package com.scholarscore.api.persistence.mysql;

import org.hibernate.Session;
import org.testng.annotations.Test;

import java.sql.SQLSyntaxErrorException;

import static org.testng.Assert.assertNotNull;

/**
 * User: jordan
 * Date: 2/23/16
 * Time: 9:27 PM
 */
@Test(groups = { "functional" })
public class QueryGeneratorJdbcTest extends BaseJdbcTest {
    
    @Test(dataProvider = "queriesProvider", dataProviderClass = QuerySqlGeneratorUnitTest.class)
    public void testSqlGeneratorOutputAgainstDatabase(QuerySqlGeneratorUnitTest.TestQuery testQuery) {
        System.out.println("Starting test case.");
        testQuery(testQuery.buildSQL());
        System.out.println("Finishing test case.");
    }
    
    private void testQuery(String query) {
        // make sure we have our template...
        assertNotNull(hibernateTemplate, "Hibernate Template inherited from BaseJdbcTest is unexpectedly null!");
        
        Session session = hibernateTemplate.getSessionFactory().openSession();
        assertNotNull(session, "Could not obtain Hibernate session!");

        assertNotNull(session.createSQLQuery(query).list());
        
        session.close();
    }
}
