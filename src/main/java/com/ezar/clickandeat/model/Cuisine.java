package com.ezar.clickandeat.model;

public class Cuisine {
    
    private String name;
    
    private String description;

    @Override
    public String toString() {
        return "Cuisine{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
