package com.ezar.clickandeat.workflow;

import com.ezar.clickandeat.model.Order;

public class WorkflowStatusException extends WorkflowException {

    private final Order order;

    private final String attemptedAction;

    public WorkflowStatusException(Order order, String attemptedAction) {
        this.order = order;
        this.attemptedAction = attemptedAction;
    }

    public Order getOrder() {
        return order;
    }

    public String getAttemptedAction() {
        return attemptedAction;
    }
    
    public String getMessage() {
        return "Attempted action: " + attemptedAction + ", order status: " + order.getOrderStatus();
    }
}

