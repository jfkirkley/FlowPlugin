package org.androware.aop;

/**
 * Created by jkirkley on 12/12/16.
 */
public abstract class AfterAspect extends SimpleAspect {
    @Override
    public void before(Object theThis, String methodName, Object[] args) throws IntrospectionException {

    }

    // after left abstract to force being overriddne
    @Override
    public abstract void after(Object returnValue, Object theThis, String methodName, Object[] args) throws IntrospectionException;

    @Override
    public void onException(Throwable t, Object theThis, String methodName, Object[] args) throws IntrospectionException {

    }
}
