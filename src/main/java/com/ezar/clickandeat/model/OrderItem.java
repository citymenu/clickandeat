package com.ezar.clickandeat.model;

import com.ezar.clickandeat.config.MessageFactory;
import com.ezar.clickandeat.util.NumberUtil;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class OrderItem {

    private String orderItemId;
    
    private String menuItemId;
    
    private Integer menuItemNumber;
    
    private String menuItemTitle;

    private String menuItemSubTypeName;

    private String menuItemTypeName;

    private List<String> additionalItems;
    
    private Double cost;

    private Integer quantity;


    public OrderItem() {
        this.additionalItems = new ArrayList<String>();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderItem)) return false;

        OrderItem orderItem = (OrderItem) o;

        if (menuItemId != null ? !menuItemId.equals(orderItem.menuItemId) : orderItem.menuItemId != null) return false;

        String thisMenuItemTypeName = "null".equals(menuItemTypeName)? null: menuItemTypeName;
        String otherMenuItemTypeName = "null".equals(orderItem.menuItemTypeName)? null: orderItem.menuItemTypeName;
        
        if (thisMenuItemTypeName != null ? !thisMenuItemTypeName.equals(otherMenuItemTypeName) : otherMenuItemTypeName != null)
            return false;

        String thisMenuItemSubTypeName = "null".equals(menuItemSubTypeName)? null: menuItemSubTypeName;
        String otherMenuItemSubTypeName = "null".equals(orderItem.menuItemSubTypeName)? null: orderItem.menuItemSubTypeName;

        if (thisMenuItemSubTypeName != null ? !thisMenuItemSubTypeName.equals(otherMenuItemSubTypeName) : otherMenuItemSubTypeName != null)
            return false;

        if( additionalItems.size() != orderItem.additionalItems.size()) {
            return false;
        }
        
        for( String additionalItem: additionalItems ) {
            if( !orderItem.additionalItems.contains(additionalItem)) {
                return false;
            }
        }
        
        return true;
    }

    public String getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(String orderItemId) {
        this.orderItemId = orderItemId;
    }

    public String getMenuItemId() {
        return menuItemId;
    }

    public void setMenuItemId(String menuItemId) {
        this.menuItemId = menuItemId;
    }

    public Integer getMenuItemNumber() {
        return menuItemNumber;
    }

    public void setMenuItemNumber(Integer menuItemNumber) {
        this.menuItemNumber = menuItemNumber;
        this.additionalItems = new ArrayList<String>();
    }

    public String getMenuItemTitle() {
        return menuItemTitle;
    }

    public void setMenuItemTitle(String menuItemTitle) {
        this.menuItemTitle = menuItemTitle;
    }

    public String getMenuItemTypeName() {
        return menuItemTypeName;
    }

    public void setMenuItemTypeName(String menuItemTypeName) {
        this.menuItemTypeName = menuItemTypeName;
    }

    public String getMenuItemSubTypeName() {
        return menuItemSubTypeName;
    }

    public void setMenuItemSubTypeName(String menuItemSubTypeName) {
        this.menuItemSubTypeName = menuItemSubTypeName;
    }

    public List<String> getAdditionalItems() {
        return additionalItems;
    }

    public void setAdditionalItems(List<String> additionalItems) {
        this.additionalItems = additionalItems;
    }

    public Double getCost() {
        return cost;
    }

    public Double getTotalCost() {
        return cost * quantity;
    }
    
    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public String getFormattedCost() {
        return NumberUtil.format(cost * quantity);
    }


    public String getSummary() {
        return this.toString();
    }

    public String getDescription() {
        StringBuilder sb = new StringBuilder(menuItemTitle);
        if( StringUtils.hasText(menuItemTypeName)) {
            sb.append(" (").append(menuItemTypeName).append(")");
        }
        if( StringUtils.hasText(menuItemSubTypeName)) {
            sb.append(" (").append(menuItemSubTypeName).append(")");
        }
        for( String additionalItem: additionalItems) {
            sb.append("\n  ").append(additionalItem);
        }
        return sb.toString();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(quantity).append(" x ").append(menuItemTitle);
        if( StringUtils.hasText(menuItemTypeName)) {
            sb.append(" (").append(menuItemTypeName).append(")");
        }
        if( StringUtils.hasText(menuItemSubTypeName)) {
            sb.append(" (").append(menuItemSubTypeName).append(")");
        }
        sb.append(": ").append(getFormattedCost()).append(" ").append(MessageFactory.getMessage("vm-currency",false));
        for( String additionalItem: additionalItems) {
            sb.append("\n  ").append(additionalItem);
        }
        return sb.toString();
    }
    
}
