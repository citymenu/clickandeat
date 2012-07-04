package com.ezar.clickandeat.model;

import java.util.ArrayList;
import java.util.List;

public class Search {

    private String location;
    
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

    public Search(String location, List<String> cuisines, String sort, String dir) {
        this.location = location;
        this.cuisines = cuisines;
        this.sort = sort;
        this.dir = dir;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
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
