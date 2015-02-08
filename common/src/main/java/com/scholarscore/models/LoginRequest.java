package com.scholarscore.models;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;

@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginRequest implements Serializable {
    private String username;
    private String password;
    
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!super.equals(obj)) {
            return false;
        }
        final LoginRequest other = (LoginRequest) obj;
        return Objects.equals(this.username, other.username) && Objects.equals(this.password, other.password);
    }
    
    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(username, password);
    }
}
