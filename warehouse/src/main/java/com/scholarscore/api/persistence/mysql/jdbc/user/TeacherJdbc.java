package com.scholarscore.api.persistence.mysql.jdbc.user;

import com.scholarscore.api.persistence.AuthorityPersistence;
import com.scholarscore.api.persistence.TeacherPersistence;
import com.scholarscore.api.util.RoleConstants;
import com.scholarscore.models.Authority;
import com.scholarscore.models.user.Staff;
import com.scholarscore.models.user.Teacher;
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
    public Collection<Teacher> selectAll() {
        return hibernateTemplate.loadAll(Teacher.class);
    }

    @Override
    public Teacher select(long id) {
        Staff staff = hibernateTemplate.get(Staff.class, id);
        if (null != staff) {
            return new Teacher(staff);
        }
        else {
            return null;
        }

    }

    @SuppressWarnings("unchecked")
    public Teacher select(String username) {
        String query = "select t from teacher t join t.user u where u.username = :username";
        List<Teacher> teachers = (List<Teacher>) hibernateTemplate.findByNamedParam(query, "username", username);
        if (teachers.size() == 1) {
            return teachers.get(0);
        }
        return null;
    }

    @Override
    public Long createTeacher(Teacher teacher) {
        Staff staff = teacher;
        Staff staff1 = new Staff(teacher);
        transformUserValues(staff1, null);
        Staff out = hibernateTemplate.merge(staff1);
        Authority auth = new Authority();
        auth.setAuthority(RoleConstants.TEACHER);
        auth.setUserId(out.getId());
        authorityPersistence.createAuthority(auth);
        return out.getId();
    }

    @Override
    public void replaceTeacher(long id, Teacher teacher) {
        transformUserValues(teacher, select(id));
        hibernateTemplate.merge(new Staff(teacher));
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