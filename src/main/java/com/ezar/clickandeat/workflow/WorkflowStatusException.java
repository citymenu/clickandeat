package com.ezar.clickandeat.workflow;

import com.ezar.clickandeat.model.Order;

public class WorkflowStatusException extends WorkflowException {

    private final String currentStatus;
    
    public WorkflowStatusException(Order order) {
        this.currentStatus = order.getOrderStatus();
    }

    public WorkflowStatusException(Order order, String message) {
        super(message);
        this.currentStatus = order.getOrderStatus();
        
    }

    public WorkflowStatusException(Order order, String message, Throwable ex) {
        super(message,ex);
        this.currentStatus = order.getOrderStatus();
    }

    public WorkflowStatusException(Order order, Throwable ex) {
        super(ex);
        this.currentStatus = order.getOrderStatus();
    }

    public String getCurrentStatus() {
        return currentStatus;
    }
}

