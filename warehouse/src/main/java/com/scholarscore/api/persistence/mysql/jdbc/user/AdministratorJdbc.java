package com.scholarscore.api.persistence.mysql.jdbc.user;

import com.scholarscore.api.persistence.AdministratorPersistence;
import com.scholarscore.api.persistence.AuthorityPersistence;
import com.scholarscore.api.util.RoleConstants;
import com.scholarscore.models.Authority;
import com.scholarscore.models.user.Administrator;
import com.scholarscore.models.user.Staff;
import com.scholarscore.models.user.StaffRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;

import javax.transaction.Transactional;
import java.util.ArrayList;
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
    public Collection<Administrator> selectAll() {
        String query = "select a from staff a where a.staffRole = :staffType";
        List<Staff> staff = (List<Staff>)hibernateTemplate.findByNamedParam(query, "staffType", StaffRole.ADMIN);
        List<Administrator> admins = new ArrayList<>();
        for (Staff s : staff) {
            admins.add(new Administrator(s));
        }
        return admins;
    }

    @Override
    public Administrator select(long administratorId) {
        Staff staff = hibernateTemplate.get(Staff.class, administratorId);
        return new Administrator(staff);

    }

    @Override
    @SuppressWarnings("unchecked")
    public Administrator select(String username) {
        String query = "select a from staff a join a.user u where u.username = :username";
        List<Administrator> users = (List<Administrator>)hibernateTemplate.findByNamedParam(query, "username", username);
        if (users.size() == 1) {
            return users.get(0);
        }
        return null;
    }

    @Override
    public Long createAdministrator(Administrator administrator) {
        transformUserValues(administrator, null);
        Staff staff = new Staff(administrator);
        Staff out = hibernateTemplate.merge(staff);
        administrator.setId(out.getId());
        Authority auth = new Authority();
        auth.setAuthority(RoleConstants.ADMINISTRATOR);
        auth.setUserId(out.getId());
        authorityPersistence.createAuthority(auth);
        return out.getId();
    }

    @Override
    public void replaceAdministrator(long administratorId, Administrator administrator) {
        transformUserValues(administrator, select(administratorId));
        hibernateTemplate.merge(new Staff(administrator));
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
