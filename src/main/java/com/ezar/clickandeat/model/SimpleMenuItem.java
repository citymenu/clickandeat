package com.ezar.clickandeat.model;

public class SimpleMenuItem implements MenuItem {
    
    private String name;
    
    private String description;
    
    private Double price;

    @Override
    public String toString() {
        return "SimpleMenuItem{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
