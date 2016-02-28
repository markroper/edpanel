package com.scholarscore.api.persistence.mysql;

import com.scholarscore.api.persistence.mysql.querygenerator.QuerySqlGenerator;
import com.scholarscore.api.persistence.mysql.querygenerator.SqlGenerationException;
import com.scholarscore.api.persistence.mysql.querygenerator.SqlWithParameters;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.exception.SQLGrammarException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertNotNull;

/**
 * User: jordan
 * Date: 2/23/16
 * Time: 9:27 PM
 */
@Test(groups = { "functional" })
public class QueryGeneratorJdbcTest extends BaseJdbcTest {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    
    @Test(dataProvider = "validQueriesProvider", dataProviderClass = QuerySqlGeneratorUnitTest.class)
    public void testSqlGeneratorOutputAgainstDatabase(QuerySqlGeneratorUnitTest.TestQuery testQuery) throws SqlGenerationException {
        testQuery(testQuery);
    }
    
    private void testQuery(QuerySqlGeneratorUnitTest.TestQuery testQuery) throws SqlGenerationException {

        // make sure we have our template...
        assertNotNull(hibernateTemplate, "Hibernate Template inherited from BaseJdbcTest is unexpectedly null!");
        Session session = hibernateTemplate.getSessionFactory().openSession();
        assertNotNull(session, "Could not obtain Hibernate session!");

        SqlWithParameters query = QuerySqlGenerator.generate(testQuery.buildQuery());
        
        // if query has parameters, replace them with the real values
        String querySql = query.getSql();
        LOGGER.debug("Checking params...");
        if (query.getParams() != null) {
            for (String paramKey: query.getParams().keySet()) {
                Object value = query.getParams().get(paramKey);
                if (value instanceof String) {
                    LOGGER.debug("Query " + testQuery.queryName() + " contains String parameter :" + paramKey + " which will be replaced with '" + value + "'");
                    if (paramKey != null) {
                        querySql = querySql.replace(":" + paramKey, "'" + value + "'");
                    }
                }
                // if non-string parameters are added later, they will need to be handled here
            }
        }

        SQLQuery constructedQuery = session.createSQLQuery(querySql);
        List result = null;
        try {
            result = constructedQuery.list();
        } catch (SQLGrammarException sqlge) {
            throw new RuntimeException("Query " + testQuery.queryName() + " failed to execute with grammar exception. Query: " + querySql, sqlge);
        }
        assertNotNull(result, "Query failed to execute: " + querySql);
        session.close();
    }
}
