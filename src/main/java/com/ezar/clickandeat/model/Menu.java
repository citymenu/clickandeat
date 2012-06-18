package com.ezar.clickandeat.model;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection="menus")
public class Menu extends PersistentObject {

    private boolean numberItems;

    private List<MenuCategory> menuCategories;

    public Menu() {
        this.menuCategories = new ArrayList<MenuCategory>();
    }

    public boolean isNumberItems() {
        return numberItems;
    }

    public void setNumberItems(boolean numberItems) {
        this.numberItems = numberItems;
    }

    public List<MenuCategory> getMenuCategories() {
        return menuCategories;
    }

    public void setMenuCategories(List<MenuCategory> menuCategories) {
        this.menuCategories = menuCategories;
    }

}
