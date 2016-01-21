package com.scholarscore.api.persistence.mysql.jdbc.user;

import com.scholarscore.api.persistence.AuthorityPersistence;
import com.scholarscore.api.persistence.TeacherPersistence;
import com.scholarscore.api.util.RoleConstants;
import com.scholarscore.models.Authority;
import com.scholarscore.models.user.Staff;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;

@Transactional
public class TeacherJdbc extends UserBaseJdbc implements TeacherPersistence {

    @Autowired
    private HibernateTemplate hibernateTemplate;
    
    private AuthorityPersistence authorityPersistence;

    public TeacherJdbc() {
    }

    public TeacherJdbc(HibernateTemplate template) {
        this.hibernateTemplate = template;
    }

    public void setHibernateTemplate(HibernateTemplate template) {
        this.hibernateTemplate = template;
    }

    @Override
    public Collection<Staff> selectAll() {
        return hibernateTemplate.loadAll(Staff.class);
    }

    @Override
    public Staff select(long id) {
        return hibernateTemplate.get(Staff.class, id);

    }

    @SuppressWarnings("unchecked")
    public Staff select(String username) {
        String query = "select t from teacher t join t.user u where u.username = :username and t.teacher = true";
        List<Staff> teachers = (List<Staff>) hibernateTemplate.findByNamedParam(query, "username", username);
        if (teachers.size() == 1) {
            return teachers.get(0);
        }
        return null;
    }

    @Override
    public Long createTeacher(Staff teacher) {
        teacher.setTeacher(true);
        transformUserValues(teacher, null);
        Staff out = hibernateTemplate.merge(teacher);
        Authority auth = new Authority();
        auth.setAuthority(RoleConstants.TEACHER);
        auth.setUserId(out.getId());
        authorityPersistence.createAuthority(auth);
        return out.getId();
    }

    @Override
    public void replaceTeacher(long id, Staff teacher) {
        transformUserValues(teacher, select(id));
        hibernateTemplate.merge(teacher);
    }

    @Override
    public Long delete(long id) {
        Staff staff = hibernateTemplate.get(Staff.class, id);
        hibernateTemplate.delete(staff);
        return id;
    }
    
    public void setAuthorityPersistence(AuthorityPersistence authorityPersistence) {
        this.authorityPersistence = authorityPersistence;
    }
}