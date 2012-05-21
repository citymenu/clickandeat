package com.ezar.clickandeat.model;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection="menus")
public class Menu extends BaseObject {

    String name;

    boolean collection;

    boolean delivery;



    List<MenuCategory> menuCategories = new ArrayList<MenuCategory>();

    public Menu() {
    }

    public String getName() {
        return name;
    }

    public boolean isCollection() {
        return collection;
    }

    public void setCollection(boolean collection) {
        this.collection = collection;
    }

    public boolean isDelivery() {
        return delivery;
    }

    public void setDelivery(boolean delivery) {
        this.delivery = delivery;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MenuCategory> getMenuCategories() {
        return menuCategories;
    }

    public void setMenuCategories(List<MenuCategory> menuCategories) {
        this.menuCategories = menuCategories;
    }
}
