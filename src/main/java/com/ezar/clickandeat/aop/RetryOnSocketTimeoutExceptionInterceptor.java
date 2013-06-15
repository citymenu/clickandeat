package com.ezar.clickandeat.aop;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.net.SocketTimeoutException;

public class RetryOnSocketTimeoutExceptionInterceptor {

    private static final Logger LOGGER = Logger.getLogger(RetryOnSocketTimeoutExceptionInterceptor.class);

    private static final int MAX_RETRIES = 3;


    /**
     * @param pjp
     * @return
     * @throws Throwable
     */

    public Object retryOnSocketTimeoutException(ProceedingJoinPoint pjp) throws Throwable {

        int retryAttempts = 0;
        Exception thrownException = null;

        while(retryAttempts < MAX_RETRIES ) {
            try {
                return pjp.proceed();
            }
            catch(Exception ex) {
                Object rootCause = ExceptionUtils.getRootCause(ex);
                if(rootCause instanceof SocketTimeoutException) {
                    retryAttempts++;
                    thrownException = ex;
                    LOGGER.info("Caught SocketTimeoutException, retry attempt: " + retryAttempts);
                }
                else {
                    throw ex;
                }
            }
        }
        throw thrownException;
    }

}
