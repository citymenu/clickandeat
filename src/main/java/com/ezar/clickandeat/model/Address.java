package com.ezar.clickandeat.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="addresses")
public class Address extends BaseObject {
    
    private String address1;
    
    private String address2;
    
    private String address3;
    
    private String town;
    
    private String region;
    
    private String postCode;

    private double[] location;

    public Address() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Address)) return false;

        Address address = (Address) o;

        if (address1 != null ? !address1.equals(address.address1) : address.address1 != null) return false;
        if (address2 != null ? !address2.equals(address.address2) : address.address2 != null) return false;
        if (address3 != null ? !address3.equals(address.address3) : address.address3 != null) return false;
        if (postCode != null ? !postCode.equals(address.postCode) : address.postCode != null) return false;
        if (region != null ? !region.equals(address.region) : address.region != null) return false;
        if (town != null ? !town.equals(address.town) : address.town != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = address1 != null ? address1.hashCode() : 0;
        result = 31 * result + (address2 != null ? address2.hashCode() : 0);
        result = 31 * result + (address3 != null ? address3.hashCode() : 0);
        result = 31 * result + (town != null ? town.hashCode() : 0);
        result = 31 * result + (region != null ? region.hashCode() : 0);
        result = 31 * result + (postCode != null ? postCode.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Address{" +
                "address1='" + address1 + '\'' +
                ", address2='" + address2 + '\'' +
                ", address3='" + address3 + '\'' +
                ", town='" + town + '\'' +
                ", region='" + region + '\'' +
                ", postCode='" + postCode + '\'' +
                '}';
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getAddress3() {
        return address3;
    }

    public void setAddress3(String address3) {
        this.address3 = address3;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public double[] getLocation() {
        return location;
    }

    public void setLocation(double[] location) {
        this.location = location;
    }
}