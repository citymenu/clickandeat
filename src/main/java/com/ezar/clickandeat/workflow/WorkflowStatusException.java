package com.ezar.clickandeat.workflow;

public class WorkflowStatusException extends WorkflowException {

    public WorkflowStatusException() {
    }

    public WorkflowStatusException(String message) {
        super(message);
    }

    public WorkflowStatusException(String message, Throwable ex) {
        super(message,ex);
    }

    public WorkflowStatusException(Throwable ex) {
        super(ex);
    }
    
}

