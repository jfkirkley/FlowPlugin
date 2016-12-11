package org.androware.aop;


import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * Created by jkirkley on 12/11/16.
 */
public class TraceAspect implements Aspect {

    @Override
    public void before(Callable<?> zuper, Object theThis, Class origin, Method method, Object[] args) throws IntrospectionException {
        System.out.println("ta: Calling " + zuper);
        System.out.println("ta: origin " + origin);
        System.out.println("ta: this " + theThis.getClass().getName());

        for(Object arg: args) {
            System.out.println("ta: " + arg);
        }
    }

    @Override
    public void after(Object returnValue, Callable<?> zuper, Object theThis, Class origin, Method method, Object[] args) throws IntrospectionException {
        System.out.println("ta return: " + returnValue);
    }

    @Override
    public void onException(Throwable t, Callable<?> zuper, Object theThis, Class origin, Method method, Object[] args) throws IntrospectionException {

    }
}
