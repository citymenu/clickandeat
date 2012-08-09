package com.ezar.clickandeat.workflow;

import com.ezar.clickandeat.model.Order;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class WorkflowStatusExceptionMessageResolver implements InitializingBean {
    
    private static final Logger LOGGER = Logger.getLogger(WorkflowStatusExceptionMessageResolver.class);

    @Autowired
    private ResourceBundleMessageSource messageSource;

    private String locale;
    
    private Locale systemLocale;

    @Override
    public void afterPropertiesSet() throws Exception {
        String[] localeArray = locale.split("_");
        this.systemLocale = new Locale(localeArray[0],localeArray[1]);
    }


    /**
     * @param ex
     * @return
     */

    public String getWorkflowStatusExceptionMessage(WorkflowStatusException ex) {
        return getWorkflowStatusExceptionMessage(ex, systemLocale);
    }
    
    
    /**
     * @param ex
     * @param locale
     * @return
     */

    public String getWorkflowStatusExceptionMessage(WorkflowStatusException ex, Locale locale) {
        
        Order order = ex.getOrder();
        String attemptedAction = ex.getAttemptedAction();

        String actionVerb = messageSource.getMessage("verb." + attemptedAction, new Object[]{}, locale);
        String orderStatusDescription = messageSource.getMessage("description." + order.getOrderStatus(), new Object[]{order.getRestaurant().getName()}, locale);
        String exceptionMessage = messageSource.getMessage("error.workflow.detail", new Object[]{actionVerb,orderStatusDescription}, locale);
        
        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Generated exception message: " + exceptionMessage);
        }

        return exceptionMessage;
    }


    @Required
    @Value(value="${locale}")
    public void setLocale(String locale) {
        this.locale = locale;
    }

}
