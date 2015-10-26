package com.scholarscore.models;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.Objects;

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
    public int hashCode() {
        return Objects.hash(username, password);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final LoginRequest other = (LoginRequest) obj;
        return Objects.equals(this.username, other.username)
                && Objects.equals(this.password, other.password);
    }

    /**
     * Each class's Builder holds a copy of each attribute that the parent POJO has. We build up these properties using
     * a pattern of with[Attribute](Attribute attribute) and return the same instance of the Builder so that one can easily
     * chain setting attributes together.
     */
    public static class LoginRequestBuilder {

        private String username;
        private String password;

        public LoginRequestBuilder withUsername(final String username){
            this.username = username;
            return this;
        }

        public LoginRequestBuilder withPassword(final String password){
            this.password = password;
            return this;
        }

        public LoginRequest build(){
            LoginRequest request = new LoginRequest();
            request.setUsername(username);
            request.setPassword(password);
            return request;
        }
    }
}
