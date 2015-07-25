package com.scholarscore.models;

/**
 * Created by mattg on 7/19/15.
 */
public interface IStaff<T> extends IApiModel<T> {
    void setSourceSystemId(String sourceSystemId);

    void setLogin(User login);

    User getLogin();

    Address getHomeAddress();

    void setHomeAddress(Address homeAddress);

    String getHomePhone();

    void setHomePhone(String homePhone);

    String getSourceSystemId();

    void setName(String name);

    String getUsername();

    void setUsername(String username);
}
