package org.androware.flow.base;

/**
 * Created by jkirkley on 8/16/16.
 */

public class NavBase {
    public String listAdapterName;
    public String compName;
    public String event;
    public String anim_in;
    public String anim_out;
    public String target;
    public boolean useStepGenerator;

    public String toString() {
        return target != null? target: listAdapterName != null? listAdapterName: super.toString();
    }
}
