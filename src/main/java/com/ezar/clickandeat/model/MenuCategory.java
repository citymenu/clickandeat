package com.ezar.clickandeat.model;

import java.util.ArrayList;
import java.util.List;

public class MenuCategory {
    
    public static final String TYPE_STANDARD = "STANDARD";
    public static final String TYPE_GRID = "GRID";

    private String categoryId;

    private String name;

    private String type;

    private String summary;

    private String iconClass;

    private List<MenuItem> menuItems;

    private List<MenuSubCategory> subCategories;

    private List<String> itemTypes;

    public MenuCategory() {
        this.menuItems = new ArrayList<MenuItem>();
        this.subCategories = new ArrayList<MenuSubCategory>();
        this.itemTypes = new ArrayList<String>();
    }

    public int getItemTypeCount() {
        return itemTypes.size();
    }
    
    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public List<String> getItemTypes() {
        return itemTypes;
    }

    public void setItemTypes(List<String> itemTypes) {
        this.itemTypes = itemTypes;
    }
}
