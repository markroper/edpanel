package com.scholarscore.api.persistence.mysql;

import com.scholarscore.models.Course;
import com.scholarscore.models.Section;
import com.scholarscore.models.Term;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

/**
 * Created by mattg on 8/29/15.
 */
@Test(groups = { "functional"})
public class SectionJdbcTest extends BaseJdbcTest {
    public void testSectionCrud() {
        Section sectionToCreate = new Section(section);
        Term term = createTerm();
        Course course = createCourse();
        sectionToCreate.setTerm(term);
        sectionToCreate.setCourse(course);
        Long sectionId = sectionDao.insert(term.getId(), sectionToCreate);
        assertNotNull(sectionId, "Expected non-null sectionId from create of section");

        sectionDao.delete(sectionId);
        assertNull(sectionDao.select(term.getId(), sectionId), "Expected section to be null after delete operation");
        schoolDao.delete(course.getSchool().getId());
        courseDao.delete(course.getId());
    }
}
