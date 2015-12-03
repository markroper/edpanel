package com.scholarscore.models.user;

import java.io.Serializable;

/**
 * Certain fields on user are @JsonIgnore'd for security reasons (oneTimePass, password, etc).  However,
 * we have administrative APIs that return one time passwords so that they can be shared with end users.
 * These endpoints return this object instead of a vanilla user so that oneTimePass is included in the JSON.
 *
 * Created by markroper on 12/3/15.
 */
public class UserWithOneTimePass implements Serializable {
    private User user;
    private String tempPass;

    public UserWithOneTimePass() {

    }
    public UserWithOneTimePass(User u) {
        this.user = u;
        this.tempPass = u.getOneTimePass();
    }

    public String getTempPass() {
        return tempPass;
    }

    public void setTempPass(String oneTimePass) {
        this.tempPass = oneTimePass;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
