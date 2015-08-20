package com.scholarscore.models;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@SuppressWarnings("serial")
@Entity(name="address")
public class Address implements Serializable {
    private String street;
    private String city;
    private String state;
    private String postalCode;

    private Long id;
    
    public Address() {
        
    }

    @Column(name = "address_street")
    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    @Column(name = "address_city")
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Column(name = "address_state")
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Column(name = "address_postal_code")
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
            && Objects.equals(this.postalCode, other.postalCode)
            && Objects.equals(this.id, other.id);
    }
    
    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(street, city, state, postalCode, id);
    }

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = "address_id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Address{" +
                "street='" + street + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", id=" + id +
                '}';
    }
}
