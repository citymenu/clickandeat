package com.ezar.clickandeat.workflow;

import com.ezar.clickandeat.config.MessageFactory;
import com.ezar.clickandeat.model.Order;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class WorkflowStatusExceptionMessageResolver {
    
    private static final Logger LOGGER = Logger.getLogger(WorkflowStatusExceptionMessageResolver.class);

    /**
     * @param ex
     * @return
     */

    public String getWorkflowStatusExceptionMessage(WorkflowStatusException ex) {
        
        Order order = ex.getOrder();
        String attemptedAction = ex.getAttemptedAction();

        String actionVerb = MessageFactory.getMessage("verb." + attemptedAction,true);
        String orderStatusDescription = MessageFactory.formatMessage("order-status-description." + order.getOrderStatus(), false, order.getRestaurant().getName());
        String exceptionMessage = MessageFactory.formatMessage("error.workflow.detail", false, actionVerb,orderStatusDescription);
        
        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Generated exception message: " + exceptionMessage);
        }

        return exceptionMessage;
    }


}
