package org.androware.aop;

import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by jkirkley on 12/11/16.
 */
public class FullInterceptor {
    private List<Aspect> aspects;

    public FullInterceptor(List<Aspect> aspects) {
        this.aspects = aspects;
    }

    @RuntimeType
    public Object intercept(@SuperCall Callable<?> zuper, @This Object theThis, @Origin Class origin, @Origin Method method, @AllArguments Object[] args)
            throws Exception {

        if(aspects != null) {
            for(Aspect aspect: aspects) {
                aspect.before(zuper, theThis, origin, method, args);
            }
        }

        Object returnValue = null;

        try {
            returnValue = zuper.call();
            return returnValue;
        } catch(Throwable t) {

            if(aspects != null) {
                for(Aspect aspect: aspects) {
                    aspect.onException(t, zuper, theThis, origin, method, args);
                }
            }

        } finally {

            if(aspects != null) {
                for(Aspect aspect: aspects) {
                    aspect.after(returnValue, zuper, theThis, origin, method, args);
                }
            }
        }
        return returnValue;
    }

}
