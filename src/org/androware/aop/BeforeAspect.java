package org.androware.aop;

/**
 * Created by jkirkley on 12/12/16.
 */
public abstract class BeforeAspect extends SimpleAspect {

    // force before to be overridden
    @Override
    public abstract void before(Object theThis, String methodName, Object[] args) throws IntrospectionException;


    // empty implementations for after and onException
    @Override
    public void after(Object returnValue, Object theThis, String methodName, Object[] args) throws IntrospectionException {

    }

    @Override
    public void onException(Throwable t, Object theThis, String methodName, Object[] args) throws IntrospectionException {

    }
}
