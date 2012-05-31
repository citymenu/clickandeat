package com.ezar.clickandeat.model;

import java.util.ArrayList;
import java.util.List;

public class MenuSubCategory {

    String name;

    String summary;

    String iconClass;

    List<MenuItem> menuItems;

    public MenuSubCategory() {
        this.menuItems = new ArrayList<MenuItem>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getIconClass() {
        return iconClass;
    }

    public void setIconClass(String iconClass) {
        this.iconClass = iconClass;
    }

    public List<MenuItem> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(List<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }
}
