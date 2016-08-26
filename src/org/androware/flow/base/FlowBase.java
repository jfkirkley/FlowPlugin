package org.androware.flow.base;

import org.androware.androbeans.utils.ConstructorSpec;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jkirkley on 8/16/16.
 */

public class FlowBase {
    public HashMap<String, StepBase> steps;
    public String layout;
    public String processor;
    public List<ObjectLoaderSpecBase> objectLoaderSpecs;
    public ConstructorSpec stepGeneratorSpec;
    public NavBase startNav;    // navigates to the first step
    public String fragmentContainer;

    public void __get_type_overrides__(Map map) {
        map.put(StepBase.class, "org.androware.flow.Step");
        map.put(NavBase.class, "org.androware.flow.Nav");
        map.put(NavBase.class, "org.androware.flow.Nav");
        map.put(ObjectLoaderSpecBase.class, "org.androware.flow.binding.ObjectLoaderSpec");
    }


}
