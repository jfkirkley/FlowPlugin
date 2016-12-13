package org.androware.aop;

import javafx.scene.effect.Light;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by jkirkley on 12/12/16.
 */
public class LightInterceptor  {
    private List<Aspect> aspects;

    public LightInterceptor(List<Aspect> aspects) {
        this.aspects = aspects;
    }

    @RuntimeType
    public Object intercept(@SuperCall Callable<?> zuper, @This Object theThis,  @Origin String methodName, @AllArguments Object[] args)
            throws Exception {

        if(aspects != null) {
            for(Aspect aspect: aspects) {
                aspect.before(theThis, methodName, args);
            }
        }

        Object returnValue = null;

        try {
            returnValue = zuper.call();
            return returnValue;
        } catch(Throwable t) {

            if(aspects != null) {
                for(Aspect aspect: aspects) {
                    aspect.onException(t, theThis, methodName, args);
                }
            }

        } finally {

            if(aspects != null) {
                for(Aspect aspect: aspects) {
                    aspect.after(returnValue, theThis, methodName, args);
                }
            }
        }
        return returnValue;
    }

}
