package org.androware.aop;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * Created by jkirkley on 12/13/16.
 */
public class NoopAspect implements Aspect {
    @Override
    public void before(Callable<?> zuper, Object theThis, Class origin, Method method, Object[] args) throws IntrospectionException {

    }

    @Override
    public void after(Object returnValue, Callable<?> zuper, Object theThis, Class origin, Method method, Object[] args) throws IntrospectionException {

    }

    @Override
    public void onException(Throwable t, Callable<?> zuper, Object theThis, Class origin, Method method, Object[] args) throws IntrospectionException {

    }

    @Override
    public void before(Object theThis, String methodName, Object[] args) throws IntrospectionException {

    }

    @Override
    public void after(Object returnValue, Object theThis, String methodName, Object[] args) throws IntrospectionException {

    }

    @Override
    public void onException(Throwable t, Object theThis, String methodName, Object[] args) throws IntrospectionException {

    }
}
