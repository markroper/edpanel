package com.scholarscore.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scholarscore.models.user.User;

/**
 * Created by mattg on 9/14/15.
 */
public class Identity extends ApiModel {
    protected User user;
    
    public Identity() {}

    public Identity(Identity identity) {
        super(identity);
        this.user = identity.user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void mergePropertiesIfNull(Identity mergeFrom) {
        if (null == this.getUser()) {
            this.setUser(mergeFrom.getUser());
        }
        super.mergePropertiesIfNull(mergeFrom);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Identity)) return false;
        if (!super.equals(o)) return false;

        Identity identity = (Identity) o;

        return !(getUser() != null ? !getUser().equals(identity.getUser()) : identity.getUser() != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getUser() != null ? getUser().hashCode() : 0);
        return result;
    }
}
