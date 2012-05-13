package com.ezar.clickandeat.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="postCodeLocations")
public class PostCodeLocation extends BaseObject {
    
    private String postCode;

    private double[] location;

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
