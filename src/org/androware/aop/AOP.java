package org.androware.aop;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;

import java.util.ArrayList;
import java.util.List;

import static net.bytebuddy.matcher.ElementMatchers.any;

/**
 * Created by jkirkley on 12/11/16.
 */
public class AOP {

    public static <T> T t(Class<T> tClass) throws Exception {

        List<Aspect> aspects = new ArrayList<>();
        aspects.add(new TraceAspect());

        return w(tClass, aspects);
    }


    public static <T> T t(Class<T> tClass, Object... args) throws Exception {

        List<Aspect> aspects = new ArrayList<>();
        aspects.add(new TraceAspect());

        return w(tClass, aspects, args);
    }


    public static <T> T w(Class<T> tClass, List<Aspect> aspects) throws Exception {
        return new ByteBuddy()
                .subclass(tClass)
                .method(any()).intercept(MethodDelegation.to(new Interceptor(aspects)))
                .make()
                .load(Interceptor.class.getClassLoader())      //, ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded()
                .newInstance();
    }


    public static <T> T w(Class<T> tClass, List<Aspect> aspects, Object... args) throws Exception {

        Class<? extends T> clazz = new ByteBuddy()
                .subclass(tClass)
                .method(any()).intercept(MethodDelegation.to(new Interceptor(aspects)))
                .make()
                .load(Interceptor.class.getClassLoader())      //, ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();

        Class paramsTypes[] = new Class[args.length];
        int i = 0;
        for (Object arg : args) {
            paramsTypes[i++] = arg.getClass();
        }
        return (T) clazz.getConstructor(paramsTypes).newInstance(args);
    }

    public static <T> Class<? extends T> c(Class<T> tClass) throws Exception {
        List<Aspect> aspects = new ArrayList<>();
        aspects.add(new TraceAspect());

        return new ByteBuddy()
                .subclass(tClass)
                .method(any()).intercept(MethodDelegation.to(new Interceptor(aspects)))
                .make()
                .load(Interceptor.class.getClassLoader())      //, ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();
    }

}
