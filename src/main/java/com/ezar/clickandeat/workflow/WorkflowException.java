package com.ezar.clickandeat.workflow;

public class WorkflowException extends Exception {

    public WorkflowException() {
    }
    
    public WorkflowException(String message) {
        super(message);
    }

    public WorkflowException(String message, Throwable ex) {
        super(message,ex);
    }

    public WorkflowException(Throwable ex) {
        super(ex);
    }
    
}

