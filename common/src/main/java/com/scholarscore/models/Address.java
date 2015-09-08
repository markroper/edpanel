package com.scholarscore.models;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@SuppressWarnings("serial")
@Entity(name=HibernateConsts.ADDRESS_TABLE)
public class Address implements Serializable {
    private String street;
    private String city;
    private String state;
    private String postalCode;

    private Long id;
    
    public Address() {
        
    }

    public Address(Address clone) {
        this.setState(clone.getState());
        this.setCity(clone.getCity());
        this.setPostalCode(clone.getPostalCode());
        this.setStreet(clone.getStreet());
        this.setId(clone.getId());
    }

    @Column(name = HibernateConsts.ADDRESS_STREET)
    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    @Column(name = HibernateConsts.ADDRESS_CITY)
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Column(name = HibernateConsts.ADDRESS_STATE)
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Column(name = HibernateConsts.ADDRESS_POSTAL_CODE)
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
    @Column(name = HibernateConsts.ADDRESS_ID)
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
