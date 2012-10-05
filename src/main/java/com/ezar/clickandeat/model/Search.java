package com.ezar.clickandeat.model;

import org.springframework.util.StringUtils;

import java.io.Serializable;

public class Search implements Serializable {

    private static final long serialVersionUID = -1L;

    private GeoLocation location;

    private String cuisine;
    
    private String sort;
    
    private String dir;

    private boolean includeOpenOnly;

    public Search() {
    }


    /**
     * @param location
     * @param cuisine
     * @param sort
     * @param dir
     */

    public Search(GeoLocation location, String cuisine, String sort, String dir ) {
        this.location = location;
        this.cuisine = cuisine;
        this.sort = sort;
        this.dir = dir;
    }

    public String getQueryString() {
        StringBuilder sb = new StringBuilder("?loc=").append(location);
        if(StringUtils.hasText(cuisine)) {
            sb.append("&c=").append(cuisine);
        }
        if( sort != null ) {
            sb.append("&s=").append(sort);
        }
        if( dir != null ) {
            sb.append("&d=").append(dir);
        }
        return sb.toString();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("Search: {");
        sb.append("location:").append(location);
        sb.append("}");
        return sb.toString();
    }
    
    public String getCoordinates() {
        if( location == null || location.getLocation() == null ) {
            return "0,0";
        }
        else {
            return location.getLocation()[1] + "," + location.getLocation()[0];
        }
    }
    
    public GeoLocation getLocation() {
        return location;
    }

    public void setLocation(GeoLocation location) {
        this.location = location;
    }

    public String getCuisine() {
        return cuisine;
    }

    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public boolean isIncludeOpenOnly() {
        return includeOpenOnly;
    }

    public void setIncludeOpenOnly(boolean includeOpenOnly) {
        this.includeOpenOnly = includeOpenOnly;
    }

}
