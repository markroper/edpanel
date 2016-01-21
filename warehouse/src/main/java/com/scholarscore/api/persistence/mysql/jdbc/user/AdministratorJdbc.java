package com.scholarscore.api.persistence.mysql.jdbc.user;

import com.scholarscore.api.persistence.AdministratorPersistence;
import com.scholarscore.api.persistence.AuthorityPersistence;
import com.scholarscore.api.util.RoleConstants;
import com.scholarscore.models.Authority;
import com.scholarscore.models.user.Staff;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;


@Transactional
public class AdministratorJdbc extends UserBaseJdbc implements AdministratorPersistence {

    @Autowired
    private HibernateTemplate hibernateTemplate;
    
    private AuthorityPersistence authorityPersistence;

    public AdministratorJdbc() {
    }

    public AdministratorJdbc(HibernateTemplate template) {
        this.hibernateTemplate = template;
    }

    public void setHibernateTemplate(HibernateTemplate template) {
        this.hibernateTemplate = template;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<Staff> selectAll() {
        String query = "select a from staff a where a.admin = true";
        return (List<Staff>)hibernateTemplate.find(query);
    }

    @Override
    public Staff select(long administratorId) {
        return hibernateTemplate.get(Staff.class, administratorId);

    }

    @Override
    @SuppressWarnings("unchecked")
    public Staff select(String username) {
        String query = "select a from staff a join a.user u where u.username = :username and a.admin = true";
        List<Staff> users = (List<Staff>)hibernateTemplate.findByNamedParam(query, "username", username);
        if (users.size() == 1) {
            return users.get(0);
        }
        return null;
    }

    @Override
    public Long createAdministrator(Staff administrator) {
        administrator.setAdmin(true);
        transformUserValues(administrator, null);
        Staff out = hibernateTemplate.merge(administrator);
        administrator.setId(out.getId());
        Authority auth = new Authority();
        auth.setAuthority(RoleConstants.ADMINISTRATOR);
        auth.setUserId(out.getId());
        authorityPersistence.createAuthority(auth);
        return out.getId();
    }

    @Override
    public void replaceAdministrator(long administratorId, Staff administrator) {
        transformUserValues(administrator, select(administratorId));
        hibernateTemplate.merge(administrator);
    }

    @Override
    public Long delete(long administratorId) {
        Staff admin = hibernateTemplate.get(Staff.class, administratorId);
        hibernateTemplate.delete(admin);
        return administratorId;
    }
    
    public void setAuthorityPersistence(AuthorityPersistence authorityPersistence) {
        this.authorityPersistence = authorityPersistence;
    }
}
