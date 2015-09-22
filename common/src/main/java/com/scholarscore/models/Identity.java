package com.scholarscore.models;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by mattg on 9/14/15.
 */
public class Identity extends ApiModel {

    public Identity() {}

    public Identity(Identity identity) {
        super(identity);
        this.username = identity.username;
        this.user = identity.user;
    }

    // FK to the Users table entry
    protected String username;

    @JsonInclude
    protected transient User user;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Identity)) return false;
        if (!super.equals(o)) return false;

        Identity identity = (Identity) o;

        if (getUsername() != null ? !getUsername().equals(identity.getUsername()) : identity.getUsername() != null)
            return false;
        return !(getUser() != null ? !getUser().equals(identity.getUser()) : identity.getUser() != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getUsername() != null ? getUsername().hashCode() : 0);
        result = 31 * result + (getUser() != null ? getUser().hashCode() : 0);
        return result;
    }
}
