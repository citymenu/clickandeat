package com.ezar.clickandeat.model;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "searches")
public class Search extends PersistentObject {

    private static final long serialVersionUID = -10l;

    @Indexed(unique=true)
    private String searchId;
    
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
        this.cuisines = cuisines == null? new ArrayList<String>(): cuisines;
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


    public String getSearchId() {
        return searchId;
    }

    public void setSearchId(String searchId) {
        this.searchId = searchId;
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
