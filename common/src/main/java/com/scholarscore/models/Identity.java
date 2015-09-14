package com.scholarscore.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by mattg on 9/14/15.
 */
public class Identity extends ApiModel {

    public Identity() {}

    public Identity(Identity identity) {
        super(identity);
        this.username = identity.username;
        this.login = identity.login;
    }

    // FK to the Users table entry
    @JsonIgnore
    protected String username;

    @JsonInclude
    protected transient User login;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public User getLogin() {
        return login;
    }

    public void setLogin(User login) {
        this.login = login;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Identity)) return false;
        if (!super.equals(o)) return false;

        Identity identity = (Identity) o;

        if (getUsername() != null ? !getUsername().equals(identity.getUsername()) : identity.getUsername() != null)
            return false;
        return !(getLogin() != null ? !getLogin().equals(identity.getLogin()) : identity.getLogin() != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getUsername() != null ? getUsername().hashCode() : 0);
        result = 31 * result + (getLogin() != null ? getLogin().hashCode() : 0);
        return result;
    }
}
