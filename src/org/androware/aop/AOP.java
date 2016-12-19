package org.androware.aop;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static net.bytebuddy.matcher.ElementMatchers.any;
import static net.bytebuddy.matcher.ElementMatchers.anyOf;


/**
 * Created by jkirkley on 12/11/16.
 * <p>
 * TODO:
 * - need class cache, as bytebuddy classloader is filling up with redundant classes
 * - create a chaining system for AOP operations
 * - remember to fix ReflectionUtils class and method finding to avoid the 'exact type' problem
 * - have to deal with non-static inner class constructors that implicitly take the outer class as their first arg
 */
public class AOP {

    public static class ClassWrapper<T> {
        private Class<? extends T> aClass;

        public ClassWrapper(Class<? extends T> c) {
            this.aClass = c;
        }

        public T newInstance() {
            try {
                return aClass.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }

        public T newInstance(Object... args) {
            try {
                return (T) AOP.getConstructor(aClass, args).newInstance(args);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static <T> ClassWrapper<T> w(Class<T> tClass, Aspect aspect) throws Exception {
        List<Aspect> aspectList = new ArrayList<>();
        aspectList.add(aspect);
        return w(tClass, aspectList);
    }

    public static <T> ClassWrapper<T> w(Class<T> tClass, Method[] methods, Aspect aspect) throws Exception {
        List<Aspect> aspects = new ArrayList<>();
        aspects.add(aspect);
        return w(tClass, methods, aspects);
    }

    public static <T> ClassWrapper<T> w(Class<T> tClass, Aspect aspect, String... methods) throws Exception {
        List<Aspect> aspects = new ArrayList<>();
        aspects.add(aspect);
        return w(tClass, getMethods(tClass, methods), aspects);
    }

    public static <T> ClassWrapper<T> w(Class<T> tClass, List<Aspect> aspects) throws Exception {
        return new ClassWrapper<T>(new ByteBuddy()
                .subclass(tClass)
                .method(any()).intercept(MethodDelegation.to(new LightInterceptor(aspects)))
                .make()
                .load(LightInterceptor.class.getClassLoader())      //, ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded());
    }


    public static <T> ClassWrapper<T> w(Class<T> tClass, Method[] methods, List<Aspect> aspects) throws Exception {
        return new ClassWrapper<T>(new ByteBuddy()
                .subclass(tClass)
                .method(anyOf(methods)).intercept(MethodDelegation.to(new LightInterceptor(aspects)))
                .make()
                .load(FullInterceptor.class.getClassLoader())      //, ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded());

    }


    public static boolean TRACE_ON = false;

    public static <T> T t(Class<T> tClass) throws Exception {
        return TRACE_ON ? b(tClass, new TraceAspect()) : b(tClass, new NoopAspect());
    }

    public static <T> T t(Class<T> tClass, Object... args) throws Exception {
        return b(tClass, new TraceAspect(), args);
    }

    public static <T> T t(Class<T> tClass, String... methodNames) throws Exception {
        return b(tClass, new TraceAspect(), methodNames);
    }

    public static <T> T t(Class<T> tClass, String[] methodNames, Object... args) throws Exception {
        return b(tClass, methodNames, new TraceAspect(), args);
    }

    public static <T> T b(Class<T> tClass, Aspect aspect, Object... args) throws Exception {

        List<Aspect> aspects = new ArrayList<>();
        aspects.add(aspect);

        return b(tClass, aspects, args);
    }

    public static <T> T b(Class<T> tClass, Aspect aspect) throws Exception {
        List<Aspect> aspectList = new ArrayList<>();
        aspectList.add(aspect);
        return b(tClass, aspectList);
    }

    public static <T> T b(Class<T> tClass, Method[] methods, Aspect aspect, Object... args) throws Exception {

        List<Aspect> aspects = new ArrayList<>();
        aspects.add(aspect);

        return b(tClass, methods, aspects, args);
    }

    public static <T> T b(Class<T> tClass, Method[] methods, Aspect aspect) throws Exception {
        List<Aspect> aspects = new ArrayList<>();
        aspects.add(aspect);
        return b(tClass, methods, aspects);
    }

    public static <T> T b(Class<T> tClass, String[] methods, Aspect aspect, Object... args) throws Exception {

        List<Aspect> aspects = new ArrayList<>();
        aspects.add(aspect);

        return b(tClass, getMethods(tClass, methods), aspects, args);
    }

    public static <T> T b(Class<T> tClass, String method, Aspect aspect, Object... args) throws Exception {

        List<Aspect> aspects = new ArrayList<>();
        aspects.add(aspect);

        return b(tClass, getMethods(tClass, method), aspects, args);
    }

    public static <T> T b(Class<T> tClass, Aspect aspect, String... methods) throws Exception {
        List<Aspect> aspects = new ArrayList<>();
        aspects.add(aspect);
        return b(tClass, getMethods(tClass, methods), aspects);
    }

    public static <T> T b(Class<T> tClass, List<Aspect> aspects) throws Exception {
        return new ByteBuddy()
                .subclass(tClass)
                .method(any()).intercept(MethodDelegation.to(new LightInterceptor(aspects)))
                .make()
                .load(LightInterceptor.class.getClassLoader())      //, ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded()
                .newInstance();
    }


    public static <T> T b(Class<T> tClass, List<Aspect> aspects, Object... args) throws Exception {

        Class<? extends T> clazz = new ByteBuddy()
                .subclass(tClass)
                .method(any()).intercept(MethodDelegation.to(new LightInterceptor(aspects)))
                .make()
                .load(FullInterceptor.class.getClassLoader())      //, ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();

        return (T) getConstructor(clazz, args).newInstance(args);
    }

    public static <T> T b(Class<T> tClass, Method[] methods, List<Aspect> aspects) throws Exception {
        return new ByteBuddy()
                .subclass(tClass)
                .method(anyOf(methods)).intercept(MethodDelegation.to(new LightInterceptor(aspects)))
                .make()
                .load(FullInterceptor.class.getClassLoader())      //, ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded()
                .newInstance();
    }

    //org.androware.flow.builder.ResEx
    public static <T> T b(Class<T> tClass, Method[] methods, List<Aspect> aspects, Object... args) throws Exception {

        Class<? extends T> clazz = new ByteBuddy()
                .subclass(tClass)
                .method(anyOf(methods)).intercept(MethodDelegation.to(new LightInterceptor(aspects)))
                .make()
                .load(LightInterceptor.class.getClassLoader())      //, ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();

        return (T) getConstructor(clazz, args).newInstance(args);

        /*
        Constructor[] constructors = clazz.getConstructors();
        for(Constructor constructor: constructors) {
            if(paramListMatches(constructor.getParameterTypes(), args)) {
                return (T) constructor.newInstance(args);
            }
        }
        throw new NoSuchMethodError("No constructor found in " + tClass.getName() + " with params: " + args);


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

    public static Constructor getConstructor(Class clazz, Object... args) {
        Constructor[] constructors = clazz.getConstructors();
        for (Constructor constructor : constructors) {
            if (paramListMatches(constructor.getParameterTypes(), args)) {
                return constructor;
            }
        }
        throw new NoSuchMethodError("No constructor found in " + clazz.getName() + " with params: " + args);
    }

    public static boolean paramListMatches(Class[] types, Object[] args) {
        if (types.length != args.length) {
            return false;
        }
        int i = 0;
        for (Class type : types) {
            Object arg = args[i++];
            if (arg != null) {
                Class ac = arg.getClass();
                if (!type.isAssignableFrom(ac) && !equalPrimitiveOrBoxed(ac, type)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static HashMap primitive2boxed = new HashMap();

    static {
        primitive2boxed.put(int.class, Integer.class);
        primitive2boxed.put(long.class, Long.class);
        primitive2boxed.put(byte.class, Byte.class);
        primitive2boxed.put(float.class, Float.class);
        primitive2boxed.put(double.class, Double.class);
        primitive2boxed.put(boolean.class, Boolean.class);
        primitive2boxed.put(short.class, Short.class);
        primitive2boxed.put(char.class, Character.class);
    }

    public static boolean equalPrimitiveOrBoxed(Class c1, Class c2) {
        if (c1.isPrimitive()) {
            return c2.isPrimitive() ? c1.equals(c2) : c2.equals(primitive2boxed.get(c1));
        } else if (c2.isPrimitive()) {
            return c1.equals(primitive2boxed.get(c2));
        }
        return c1.equals(c2);
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

        for (Aspect aspect : aspects) {
            aspectList.add(aspect);
        }
        return aspectList;
    }
}