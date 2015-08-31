package com.scholarscore.models;

import java.io.Serializable;
import java.util.Objects;

@SuppressWarnings("serial")
public class Address implements Serializable {
    private String street;
    private String city;
    private String state;
    private String postalCode;
    
    public Address() {
        
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
    
    @Override
    public boolean equals(Object obj) {
    if (this == obj) {
        return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
        return false;
    }
    final Address other = (Address) obj;
    return Objects.equals(this.street, other.street)
            && Objects.equals(this.city, other.city)
            && Objects.equals(this.state, other.state)
            && Objects.equals(this.postalCode, other.postalCode);
    }
    
    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(street, city, state, postalCode);
    }
}
