package org.androware.androbeans.utils;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jkirkley on 8/29/16.
 */
public class Type2TypeDefaultConstructorFactory {

    Map<Class, Class> type2TargetClassMap;

    public Type2TypeDefaultConstructorFactory(Class ... classes) {
        type2TargetClassMap = new HashMap<>();
        for(int i = 0; i < classes.length; i += 2) {
            if(i < classes.length-1) {
                type2TargetClassMap.put(classes[i], classes[i+1]);
            }
        }
    }

    public Object build(Class type) {
        Class targetClass = type2TargetClassMap.get(type);
        if(targetClass != null ) {
            return ReflectionUtils.newInstance(targetClass);
        }
        return null;
    }

    public Object build(Class type, Object ... params) {
        Class targetClass = type2TargetClassMap.get(type);

        if(targetClass != null ) {
            return ReflectionUtils.getAndCallConstructor(targetClass, params);
        }
        return null;
    }

    // TODO ... this needs work
    public Object build(ConstructorSpec constructorSpec) {
        Class targetClass = type2TargetClassMap.get(constructorSpec.getTargetClass());
        if(targetClass != null ) {
            return constructorSpec.build();
        }
        return null;
    }
}
