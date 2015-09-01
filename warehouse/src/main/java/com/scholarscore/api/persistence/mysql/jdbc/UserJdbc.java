package com.scholarscore.api.persistence.mysql.jdbc;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.scholarscore.models.Administrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.scholarscore.api.persistence.mysql.DbConst;
import com.scholarscore.api.persistence.mysql.UserPersistence;
import com.scholarscore.api.persistence.mysql.mapper.UserMapper;
import com.scholarscore.models.User;
import org.springframework.orm.hibernate4.HibernateTemplate;

import javax.transaction.Transactional;

/**
 * Maintain User identities separate from Student / Teacher entities for Spring Security
 * 
 * @author mattg
 */
@Transactional
public class UserJdbc implements UserPersistence {
    @Autowired
    private HibernateTemplate hibernateTemplate;

    @Override
    public Collection<User> selectAllUsers() {
        return hibernateTemplate.loadAll(User.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public User selectUser(String username) {
        List values = hibernateTemplate.findByNamedParam("from user u where u.username = :username", "username", username);
        if (values.size() == 1) {
            return (User)values.get(0);
        }
        return null;
    }

    @Override
    public String createUser(User user) {
        hibernateTemplate.save(user);
        return user.getUsername();
    }

    @Override
    @SuppressWarnings("unchecked")
    public String replaceUser(String username, User value) {
        User fromDB = selectUser(username);
        fromDB.setName(value.getName());
        fromDB.setPassword(value.getPassword());
        fromDB.setEnabled(value.getEnabled());
        hibernateTemplate.saveOrUpdate(fromDB);
        return username;
    }

    @Override
    public String deleteUser(String username) {
        User fromDB = selectUser(username);
        if (null != fromDB) {
            hibernateTemplate.delete(fromDB);
        }
        return username;
    }

    public HibernateTemplate getHibernateTemplate() {
        return hibernateTemplate;
    }

    public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
        this.hibernateTemplate = hibernateTemplate;
    }
}
