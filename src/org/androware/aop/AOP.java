package org.androware.aop;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import org.omg.PortableInterceptor.Interceptor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static javafx.scene.input.KeyCode.T;
import static net.bytebuddy.matcher.ElementMatchers.any;
import static net.bytebuddy.matcher.ElementMatchers.anyOf;


/**
 * Created by jkirkley on 12/11/16.
 *
 * TODO: need class cache, as bytebuddy classloader is filling up with redundant classes
 */
public class AOP {
    public static boolean TRACE_ON = false;

    public static <T> T t(Class<T> tClass) throws Exception {
        return TRACE_ON? w(tClass, new TraceAspect()): w(tClass, new NoopAspect());
    }

    public static <T> T t(Class<T> tClass, Object... args) throws Exception {
        return w(tClass, new TraceAspect(), args);
    }

    public static <T> T t(Class<T> tClass, String... methodNames) throws Exception {
        return w(tClass, new TraceAspect(), methodNames);
    }

    public static <T> T t(Class<T> tClass, String [] methodNames, Object... args) throws Exception {
        return w(tClass, methodNames, new TraceAspect(), args);
    }

    public static <T> T w(Class<T> tClass, Aspect aspect, Object... args) throws Exception {

        List<Aspect> aspects = new ArrayList<>();
        aspects.add(aspect);

        return w(tClass, aspects, args);
    }

    public static <T> T w(Class<T> tClass, Aspect aspect) throws Exception {
        List<Aspect> aspectList = new ArrayList<>();
        aspectList.add(aspect);
        return w(tClass, aspectList);
    }

    public static <T> T w(Class<T> tClass,Method [] methods, Aspect aspect, Object... args) throws Exception {

        List<Aspect> aspects = new ArrayList<>();
        aspects.add(aspect);

        return w(tClass, methods, aspects, args);
    }

    public static <T> T w(Class<T> tClass, Method [] methods, Aspect aspect) throws Exception {
        List<Aspect> aspects = new ArrayList<>();
        aspects.add(aspect);
        return w(tClass, methods, aspects);
    }

    public static <T> T w(Class<T> tClass, String [] methods, Aspect aspect, Object... args) throws Exception {

        List<Aspect> aspects = new ArrayList<>();
        aspects.add(aspect);

        return w(tClass, getMethods(tClass, methods), aspects, args);
    }

    public static <T> T w(Class<T> tClass, String method, Aspect aspect, Object... args) throws Exception {

        List<Aspect> aspects = new ArrayList<>();
        aspects.add(aspect);

        return w(tClass, getMethods(tClass, method), aspects, args);
    }

    public static <T> T w(Class<T> tClass, Aspect aspect, String... methods) throws Exception {
        List<Aspect> aspects = new ArrayList<>();
        aspects.add(aspect);
        return w(tClass, getMethods(tClass, methods), aspects);
    }

    public static <T> T w(Class<T> tClass, List<Aspect> aspects) throws Exception {
        return new ByteBuddy()
                .subclass(tClass)
                .method(any()).intercept(MethodDelegation.to(new LightInterceptor(aspects)))
                .make()
                .load(LightInterceptor.class.getClassLoader())      //, ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded()
                .newInstance();
    }


    public static <T> T w(Class<T> tClass, List<Aspect> aspects, Object... args) throws Exception {

        Class<? extends T> clazz = new ByteBuddy()
                .subclass(tClass)
                .method(any()).intercept(MethodDelegation.to(new LightInterceptor(aspects)))
                .make()
                .load(FullInterceptor.class.getClassLoader())      //, ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();

        Class paramsTypes[] = new Class[args.length];
        int i = 0;
        for (Object arg : args) {
            paramsTypes[i++] = arg.getClass();
        }
        return (T) clazz.getConstructor(paramsTypes).newInstance(args);
    }

    public static <T> T w(Class<T> tClass, Method [] methods, List<Aspect> aspects) throws Exception {
        return new ByteBuddy()
                .subclass(tClass)
                .method(anyOf(methods)).intercept(MethodDelegation.to(new LightInterceptor(aspects)))
                .make()
                .load(FullInterceptor.class.getClassLoader())      //, ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded()
                .newInstance();
    }

//org.androware.flow.builder.ResEx
    public static <T> T w(Class<T> tClass, Method [] methods, List<Aspect> aspects, Object... args) throws Exception {

        Class<? extends T> clazz = new ByteBuddy()
                .subclass(tClass)
                .method(anyOf(methods)).intercept(MethodDelegation.to(new LightInterceptor(aspects)))
                .make()
                .load(LightInterceptor.class.getClassLoader())      //, ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();

        Constructor[] constructors = clazz.getConstructors();
        for(Constructor constructor: constructors) {
            if(paramListMatches(constructor.getParameterTypes(), args)) {
                return (T) constructor.newInstance(args);
            }
        }
        throw new NoSuchMethodError("No constructor found in " + tClass.getName() + " with params: " + args);
        /*

        Class paramsTypes[] = new Class[args.length];
        int i = 0;
        for (Object arg : args) {
            paramsTypes[i++] = arg.getClass();
        }
        //  !!!!!!!!!!!  getConstructor does not seem to handle matching against subclass types ???????????
        return (T) clazz.getConstructor(paramsTypes).newInstance(args);
        */
    }



    public static <T> Class<? extends T> c(Class<T> tClass) throws Exception {
        List<Aspect> aspects = new ArrayList<>();
        aspects.add(new TraceAspect());

        return new ByteBuddy()
                .subclass(tClass)
                .method(any()).intercept(MethodDelegation.to(new LightInterceptor(aspects)))
                .make()
                .load(FullInterceptor.class.getClassLoader())      //, ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();
    }

    public static boolean paramListMatches(Class[] types, Object [] args) {
        if(types.length != args.length) {
            return false;
        }
        int i = 0;
        for(Class type: types) {
            Class ac = args[i++].getClass();
            if(!type.isAssignableFrom(ac)) {
                return false;
            }
        }
        return true;
    }

    public static Method[] getMethods(Class tClass, String... names) throws Exception {
        List<Method> methodList = new ArrayList<>();
        Method[] allMethods = tClass.getMethods();

        for (String name : names) {
            for (Method method : allMethods) {
                if (method.getName().equals(name)) {
                    methodList.add(method);
                }
            }
        }

        int i = 0;
        Method[] methods = new Method[methodList.size()];
        for (Method method : methodList) {
            methods[i++] = method;
        }

        return methods;
    }

    public static Method[] getMethods(Class tClass, String name) throws Exception {
        List<Method> methodList = new ArrayList<>();
        Method[] allMethods = tClass.getMethods();

        for (Method method : allMethods) {
            if (method.getName().equals(name)) {
                methodList.add(method);
            }
        }

        int i = 0;
        Method[] methods = new Method[methodList.size()];
        for (Method method : methodList) {
            methods[i++] = method;
        }

        return methods;
    }

    public static List<Aspect> aspectList(Aspect... aspects) {
        List<Aspect> aspectList = new ArrayList<>();

        for(Aspect aspect: aspects) {
            aspectList.add(aspect);
        }
        return aspectList;
    }
}