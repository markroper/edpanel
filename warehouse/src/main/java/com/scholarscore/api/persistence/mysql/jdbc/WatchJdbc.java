package com.scholarscore.api.persistence.mysql.jdbc;

import com.scholarscore.api.persistence.WatchPersistence;
import com.scholarscore.models.StudentWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;

import javax.transaction.Transactional;
import java.util.List;

/**
 *
 * Created by cwallace on 4/16/16.
 */
@Transactional
public class WatchJdbc implements WatchPersistence {

    private static final String WATCH_HQL_BASE = "from watch w " +
            "join fetch w.student st left join fetch st.homeAddress left join fetch st.mailingAddress " +
            "left join fetch st.contactMethods join fetch w.staff t left join fetch t.homeAddress " +
            "left join fetch t.contactMethods";

    public WatchJdbc() {

    }

    @Autowired
    private HibernateTemplate hibernateTemplate;

    public HibernateTemplate getHibernateTemplate() {
        return hibernateTemplate;
    }

    public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
        this.hibernateTemplate = hibernateTemplate;
    }


    @Override
    public Long createWatch(StudentWatch watch) {
        StudentWatch s = this.hibernateTemplate.merge(watch);
        return s.getId();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<StudentWatch> getAllForStaff(long staffId) {
        String[] params = new String[]{"staffId" };
        Object[] paramValues = new Object[]{ new Long(staffId) };
        List<StudentWatch> objects = (List<StudentWatch>) hibernateTemplate.findByNamedParam(
                WATCH_HQL_BASE + " where t.id = :staffId",
                params,
                paramValues);
        if(null == objects || objects.isEmpty()) {
            return null;
        }
        return objects;

    }

    public Long deleteWatch(long watchId) {
        StudentWatch watch = hibernateTemplate.get(StudentWatch.class, watchId);
        if (null != watch) {
            hibernateTemplate.delete(watch);
            return watchId;
        } else {
            return null;
        }

    }

    public StudentWatch getWatch(long watchId) {
        StudentWatch watch = hibernateTemplate.get(StudentWatch.class, watchId);
        return watch;
    }
}
