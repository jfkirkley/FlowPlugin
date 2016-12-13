package org.androware.aop;

/**
 * Created by jkirkley on 12/12/16.
 */
public abstract class NotifyAspect extends AfterAspect {

    @Override
    public void after(Object returnValue, Object theThis, String methodName, Object[] args) throws IntrospectionException {
        this.notifyDone();
    }

    public abstract void notifyDone();

}
