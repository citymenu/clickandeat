package com.ezar.clickandeat.model;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Map;

@Document(collection="addressLocations")
public class AddressLocation extends PersistentObject implements Serializable {

    private static final long serialVersionUID = 1234L;

    @Indexed
    private String address;

    private String formattedAddress;

    private double[] location;

    private String locationType;
    
    private Map<String,String> locationComponents;
    
    private double radius;

    private boolean radiusWarning;

    private boolean radiusInvalid;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public String getLocationType() {
        return locationType;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    public Map<String, String> getLocationComponents() {
        return locationComponents;
    }

    public void setLocationComponents(Map<String, String> locationComponents) {
        this.locationComponents = locationComponents;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public boolean isRadiusWarning() {
        return radiusWarning;
    }

    public void setRadiusWarning(boolean radiusWarning) {
        this.radiusWarning = radiusWarning;
    }

    public boolean isRadiusInvalid() {
        return radiusInvalid;
    }

    public void setRadiusInvalid(boolean radiusInvalid) {
        this.radiusInvalid = radiusInvalid;
    }

    public double[] getLocation() {
        return location;
    }

    public void setLocation(double[] location) {
        this.location = location;
    }
    
    public String toString() {
        return address;
    }
}
