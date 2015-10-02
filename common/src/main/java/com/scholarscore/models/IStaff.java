package com.scholarscore.models;

/**
 * Created by mattg on 7/19/15.
 */
public interface IStaff<T> {
    void setSourceSystemId(String sourceSystemId);

    Address getHomeAddress();

    void setHomeAddress(Address homeAddress);

    String getHomePhone();

    void setHomePhone(String homePhone);

    String getSourceSystemId();

    void setName(String name);

    void setEnabled(Boolean b);

    void setUsername(String username);
}
