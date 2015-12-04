package com.scholarscore.api.persistence.mysql.jdbc.user;

import com.scholarscore.api.persistence.AuthInfoPersistence;
import com.scholarscore.api.persistence.mysql.jdbc.BaseJdbc;
import com.scholarscore.models.HibernateConsts;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.PreparedStatement;
import java.util.HashMap;

/**
 * User: jordan
 * Date: 12/2/15
 * Time: 6:09 PM
 */
public class AuthInfoJdbc extends BaseJdbc implements AuthInfoPersistence {

    private final static Logger LOGGER = LoggerFactory.getLogger(AuthInfoJdbc.class);
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void setOneTimePassword(Long userId, String oneTimePassword) {
        if (userId == null || userId <= 0) {
            LOGGER.error("ERROR - attempting to set password on an invalid userId (" + userId + "), ignoring.");
        } else if (StringUtils.isEmpty(oneTimePassword)) {
            LOGGER.error("ERROR - attempting to set password to a null/empty value, ignoring.");
        } else {
            // in addition to setting the new password,
            // we must always clear any existing one-time password
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("UPDATE " + HibernateConsts.USERS_TABLE + " ");
            queryBuilder.append("SET " + HibernateConsts.USER_ONETIME_PASS + "='" + oneTimePassword +"'");
            queryBuilder.append(",");
            queryBuilder.append(HibernateConsts.USER_ONETIME_PASS_CREATED + "=now()");
            queryBuilder.append(" WHERE USER_ID = " + userId);

            jdbcTemplate.update(queryBuilder.toString(), (HashMap<String, ?>) null);
        }
    }

    @Override
    public void updatePassword(Long userId, String newPassword) {
        if (userId == null || userId <= 0) {
            LOGGER.error("ERROR - attempting to set password on an invalid userId (" + userId + "), ignoring.");
        } else if (StringUtils.isEmpty(newPassword)) {
            LOGGER.error("ERROR - attempting to set password to a null/empty value, ignoring.");
        } else {
            String password;
            if (passwordEncoder != null) {
                password = passwordEncoder.encode(newPassword);
            } else {
                password = newPassword;
            }
 
            // in addition to setting the new password,
            // we must always clear any existing one-time password
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("UPDATE " + HibernateConsts.USERS_TABLE + " ");
            queryBuilder.append("SET " + HibernateConsts.USER_PASSWORD + "='" + password +"'");
            queryBuilder.append(",");
            queryBuilder.append(HibernateConsts.USER_ONETIME_PASS + "=NULL");
            queryBuilder.append(",");
            queryBuilder.append(HibernateConsts.USER_ONETIME_PASS_CREATED + "=NULL");
            queryBuilder.append(" WHERE USER_ID = " + userId);

            jdbcTemplate.update(queryBuilder.toString(), (HashMap<String, ?>) null);
        }
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
}
