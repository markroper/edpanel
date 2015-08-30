package com.scholarscore.api.persistence.mysql;

import com.scholarscore.models.Term;
import org.testng.annotations.Test;

import java.util.Collection;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

/**
 * Created by mattg on 8/29/15.
 */
@Test(groups = {"functional"})
public class TermJdbcTest extends BaseJdbcTest {

    public void testTermCrud() {
        Term term = createTerm();
        assertNotNull(term, "Expected non-null term from create");

        Collection<Term> terms=termDao.selectAll(term.getSchoolYear().getId());
        assertTrue(terms.contains(term), "Expected term to exist in collection from selectAll call");

        termDao.delete(term.getId());

        Term result = termDao.select(term.getSchoolYear().getId(), term.getId());
        assertNull(result, "Expected null term after delete method call");

    }
}
