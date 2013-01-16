package com.ezar.clickandeat.model;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Document(collection="geoLocations")
public class GeoLocation extends PersistentObject implements Serializable {

    private static final long serialVersionUID = 1234L;

    @Indexed
    private String address;

    private boolean valid;

    private String fullAddress;
    
    private String displayAddress;

    private double[] location;

    private Map<String,String> locationComponents = new HashMap<String, String>();
    
    private double radius;

    private boolean radiusWarning;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String getDisplayAddress() {
        return displayAddress;
    }

    public void setDisplayAddress(String displayAddress) {
        this.displayAddress = displayAddress;
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

    public boolean getRadiusWarning() {
        return radiusWarning;
    }

    public void setRadiusWarning(boolean radiusWarning) {
        this.radiusWarning = radiusWarning;
    }

    public double[] getLocation() {
        return location;
    }

    public void setLocation(double[] location) {
        this.location = location;
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder("GeoLocation:{");
        sb.append("address:'").append(address).append("',");
        sb.append("displayAddress:'").append(displayAddress).append("',");
        String delim = "";
        sb.append("locationComponents[");
        for( Map.Entry<String,String> entry: locationComponents.entrySet()) {
            sb.append(delim);
            sb.append("{").append(entry.getKey()).append(":'").append(entry.getValue()).append("'}");
            delim = ",";
        }
        sb.append("],radius:").append(radius);
        sb.append(",radiusWarning:").append(radiusWarning);
        sb.append("}");
        return sb.toString();
    }
}
