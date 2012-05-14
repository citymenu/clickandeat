package com.ezar.clickandeat.model;

import java.util.ArrayList;
import java.util.List;

public class MenuCategory {
    
    String name;
    
    List<MenuItem> menuItems = new ArrayList<MenuItem>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MenuItem> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(List<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }
}
