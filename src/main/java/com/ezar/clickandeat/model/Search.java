package com.ezar.clickandeat.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Search implements Serializable {

    private static final long serialVersionUID = 1234L;

    private AddressLocation location;

    private List<String> cuisines;
    
    private String sort;
    
    private String dir;

    private boolean includeOpenOnly;

    public Search() {
        this.cuisines = new ArrayList<String>();
    }


    /**
     * @param location
     * @param cuisines
     * @param sort
     * @param dir
     */

    public Search(AddressLocation location, List<String> cuisines, String sort, String dir ) {
        this.location = location;
        this.cuisines = cuisines;
        this.sort = sort;
        this.dir = dir;
    }

    public String getQueryString() {
        StringBuilder sb = new StringBuilder("?loc=").append(location);
        if(cuisines != null) {
            for( String cuisine: cuisines) {
                sb.append("&c=").append(cuisine);
            }
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
    
    public AddressLocation getLocation() {
        return location;
    }

    public void setLocation(AddressLocation location) {
        this.location = location;
    }

    public List<String> getCuisines() {
        return cuisines;
    }

    public void setCuisines(List<String> cuisines) {
        this.cuisines = cuisines;
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
