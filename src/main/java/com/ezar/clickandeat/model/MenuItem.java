package com.ezar.clickandeat.model;

import com.ezar.clickandeat.util.NumberUtil;
import org.apache.commons.lang.StringEscapeUtils;

import java.util.ArrayList;
import java.util.List;

public class MenuItem {

    public static final String TYPE_STANDARD = "STANDARD";
    public static final String TYPE_SUBTYPE = "SUBTYPE";

    private String type;

    private int number;
    
    private String itemId;

    private String title;
    
    private String subtitle;
    
    private String description;
    
    private String iconClass;

    private Double cost;

    private List<MenuItemSubType> menuItemSubTypes;
    
    private List<MenuItemTypeCost> menuItemTypeCosts;

    private List<String> additionalItemChoices;
    
    private Double additionalItemCost;
    
    private Integer additionalItemChoiceLimit;
            

    public MenuItem() {
        this.menuItemSubTypes = new ArrayList<MenuItemSubType>();
        this.menuItemTypeCosts = new ArrayList<MenuItemTypeCost>();
        this.additionalItemChoices = new ArrayList<String>();
    }


    /**
     * @param menuItemType
     * @return
     */

    public MenuItemTypeCost getMenuItemTypeCost(String menuItemType ) {
        for( MenuItemTypeCost menuItemTypeCost: menuItemTypeCosts ) {
            if( menuItemType.equals(menuItemTypeCost.getType())) {
                return menuItemTypeCost;
            }
        }
        return null;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getNumber() {
        return number;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIconClass() {
        return iconClass;
    }

    public void setIconClass(String iconClass) {
        this.iconClass = iconClass;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public String getFormattedCost() {
        return NumberUtil.format(cost);
    }
    
    public List<MenuItemTypeCost> getMenuItemTypeCosts() {
        return menuItemTypeCosts;
    }

    public List<MenuItemSubType> getMenuItemSubTypes() {
        return menuItemSubTypes;
    }

    public void setMenuItemSubTypes(List<MenuItemSubType> menuItemSubTypes) {
        this.menuItemSubTypes = menuItemSubTypes;
    }

    public void setMenuItemTypeCosts(List<MenuItemTypeCost> menuItemTypeCosts) {
        this.menuItemTypeCosts = menuItemTypeCosts;
    }

    public List<String> getAdditionalItemChoices() {
        return additionalItemChoices;
    }

    public void setAdditionalItemChoices(List<String> additionalItemChoices) {
        this.additionalItemChoices = additionalItemChoices;
    }

    public String getAdditionalItemChoiceArray() {
        StringBuilder sb = new StringBuilder("[");
        String delim = "";
        for( String additionalItemChoice: additionalItemChoices ) {
            sb.append(delim);
            sb.append("'");
            sb.append(StringEscapeUtils.escapeHtml(additionalItemChoice.replace("'","###")));
            sb.append("'");
            delim = ",";
        }
        sb.append("]");
        return sb.toString();
    }

    public Double getAdditionalItemCost() {
        return additionalItemCost;
    }

    public void setAdditionalItemCost(Double additionalItemCost) {
        this.additionalItemCost = additionalItemCost;
    }

    public Double getNullSafeAdditionalItemCost() {
        return additionalItemCost == null? 0d: additionalItemCost;
    }
    
    public Integer getAdditionalItemChoiceLimit() {
        return additionalItemChoiceLimit;
    }

    public void setAdditionalItemChoiceLimit(Integer additionalItemChoiceLimit) {
        this.additionalItemChoiceLimit = additionalItemChoiceLimit;
    }
    
    public Integer getNullSafeChoiceLimit() {
        return additionalItemChoiceLimit == null? 0: additionalItemChoiceLimit;
    }
}
