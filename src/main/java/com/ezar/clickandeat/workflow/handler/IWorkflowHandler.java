package com.ezar.clickandeat.workflow.handler;

import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.workflow.WorkflowException;

import java.util.Map;

public interface IWorkflowHandler {
    
    String getWorkflowAction();
    
    Order handle(Order order,Map<String,Object> context) throws WorkflowException;
}
