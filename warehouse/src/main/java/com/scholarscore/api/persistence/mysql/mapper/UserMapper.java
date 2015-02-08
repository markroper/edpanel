package com.scholarscore.api.persistence.mysql.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.scholarscore.api.persistence.mysql.DbConst;
import com.scholarscore.models.User;

/**
 * Maps a User identity from the Users table for the purpose of Jdbc Persistence
 * 
 * @author mattg
 */
public class UserMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setUsername(rs.getString(DbConst.USER_USERNAME_COL));
        user.setPassword(rs.getString(DbConst.USER_PASSWORD_COL));
        // booleans, how quaint...
        user.setEnabled(rs.getBoolean(DbConst.USER_ENABLED_COL));
        return user;
    }

}
