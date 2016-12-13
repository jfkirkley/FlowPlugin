package org.androware.aop;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * Created by jkirkley on 12/12/16.
 */
public abstract class BaseAspect implements Aspect {
    @Override
    public abstract void before(Callable<?> zuper, Object theThis, Class origin, Method method, Object[] args) throws IntrospectionException;


    @Override
    public abstract void after(Object returnValue, Callable<?> zuper, Object theThis, Class origin, Method method, Object[] args) throws IntrospectionException;


    @Override
    public abstract void onException(Throwable t, Callable<?> zuper, Object theThis, Class origin, Method method, Object[] args) throws IntrospectionException;


    @Override
    public abstract void before(Object theThis, String methodName, Object[] args) throws IntrospectionException;


    @Override
    public abstract void after(Object returnValue, Object theThis, String methodName, Object[] args) throws IntrospectionException;

    @Override
    public abstract void onException(Throwable t, Object theThis, String methodName, Object[] args) throws IntrospectionException;

}
