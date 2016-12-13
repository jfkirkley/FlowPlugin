package org.androware.aop;


/**
 * Created by jkirkley on 12/11/16.
 */
public class TraceAspect extends SimpleAspect {
    private static String indent = ""; // static so all instances will use the same indent


    // override to direct output to a place other than System.out
    public void trace(String s) {
        System.out.println(indent + s);
    }

    @Override
    public void before(Object theThis, String methodName, Object[] args) throws IntrospectionException {
        trace("call " + theThis.getClass().getName() + " -> " + methodName);

        for(Object arg: args) {
            trace(": " + arg);
        }
        indent += "  ";
    }

    @Override
    public void after(Object returnValue, Object theThis, String methodName, Object[] args) throws IntrospectionException {
        trace("return " + returnValue + " from " + methodName);
        if(indent.length()>=2) {
            indent = indent.substring(2);
        }
    }

    @Override
    public void onException(Throwable t, Object theThis, String methodName, Object[] args) throws IntrospectionException {
        trace("exception: " + t.getMessage());
    }


}
